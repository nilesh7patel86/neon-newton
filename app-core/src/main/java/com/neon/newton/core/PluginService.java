package com.neon.newton.core;

import com.neon.newton.api.ViewExtension;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;
import org.pf4j.PluginState;
import org.pf4j.PluginWrapper;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class PluginService {
    private static PluginService instance;
    private final PluginManager pluginManager;
    private final ObservableList<ViewExtension> extensions = FXCollections.observableArrayList();
    private final ObservableList<PluginWrapper> plugins = FXCollections.observableArrayList();
    private WatchService watchService;
    private final ExecutorService watchExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "PluginFolderMonitor");
        t.setDaemon(true);
        return t;
    });
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "PluginDebounceScheduler");
        t.setDaemon(true);
        return t;
    });
    private final Map<Path, Long> lastEventMap = new ConcurrentHashMap<>();

    private PluginService() {
        // Resolve the base directory for data (plugins, preferences)
        // In a portable app, we want these next to the executable.
        Path baseDir = Paths.get("").toAbsolutePath();

        // Check if we are in a dev environment (running from app-core or parent)
        if (baseDir.toString().endsWith("app-core")) {
            baseDir = baseDir.getParent();
        }

        Path pluginsDir = baseDir.resolve("plugins");

        // Ensure the directory exists
        if (!Files.exists(pluginsDir)) {
            try {
                Files.createDirectories(pluginsDir);
            } catch (IOException e) {
                System.err.println("Could not create plugins directory: " + e.getMessage());
            }
        }

        System.out.println("Using plugins directory: " + pluginsDir.toAbsolutePath());
        this.pluginManager = new DefaultPluginManager(pluginsDir);
    }

    public static PluginService getInstance() {
        if (instance == null) {
            instance = new PluginService();
        }
        return instance;
    }

    public void init() {
        reload();
        startMonitoring();
    }

    private void startMonitoring() {
        try {
            this.watchService = FileSystems.getDefault().newWatchService();
            Path pluginsRoot = pluginManager.getPluginsRoots().getFirst();
            System.out.println("Monitoring plugins directory: " + pluginsRoot.toAbsolutePath());

            pluginsRoot.register(watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY);

            watchExecutor.submit(() -> {
                try {
                    WatchKey key;
                    while ((key = watchService.take()) != null) {
                        for (WatchEvent<?> event : key.pollEvents()) {
                            if (event.kind() == StandardWatchEventKinds.OVERFLOW)
                                continue;

                            Path fileName = (Path) event.context();
                            String nameStr = fileName.toString();

                            if ((nameStr.endsWith(".jar") || nameStr.endsWith(".zip")) &&
                                    !nameStr.startsWith(".") && !nameStr.contains("~")) {

                                Path fullPath = pluginsRoot.resolve(fileName);
                                long now = System.currentTimeMillis();
                                lastEventMap.put(fullPath, now);

                                System.out.println("Detected change for: " + nameStr + ", scheduling check...");

                                scheduler.schedule(() -> {
                                    Long lastEvent = lastEventMap.get(fullPath);
                                    if (lastEvent != null && lastEvent == now) {
                                        // No newer events for this file in the last 1000ms
                                        lastEventMap.remove(fullPath);
                                        Platform.runLater(() -> processPluginPath(fullPath, nameStr));
                                    }
                                }, 1000, TimeUnit.MILLISECONDS);
                            }
                        }
                        key.reset();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    System.err.println("Critical error in monitor thread: " + e.getMessage());
                }
            });
        } catch (IOException e) {
            System.err.println("Could not start plugin monitoring: " + e.getMessage());
        }
    }

    private void processPluginPath(Path fullPath, String nameStr) {
        try {
            if (!Files.exists(fullPath))
                return;

            PluginWrapper existing = pluginManager.getPlugins().stream()
                    .filter(p -> p.getPluginPath().equals(fullPath))
                    .findFirst().orElse(null);

            if (existing != null) {
                System.out.println("Plugin already loaded, reloading: " + existing.getPluginId());
                pluginManager.unloadPlugin(existing.getPluginId());
            }

            System.out.println("Attempting to auto-load: " + nameStr);
            String pluginId = pluginManager.loadPlugin(fullPath);
            if (pluginId != null) {
                pluginManager.startPlugin(pluginId);
                refreshLists();
                System.out.println("Successfully auto-loaded: " + pluginId);
            }
        } catch (Exception e) {
            System.err.println("Auto-load failed for " + nameStr + ": " + e.getMessage());
        }
    }

    public void reload() {
        // Stop and unload existing plugins to ensure a clean state
        pluginManager.stopPlugins();
        pluginManager.unloadPlugins();

        // Load and start plugins from the folder
        pluginManager.loadPlugins();
        pluginManager.startPlugins();

        refreshLists();
    }

    private void refreshLists() {
        // Update the extensions list (only from STARTED plugins)
        extensions.setAll(pluginManager.getExtensions(ViewExtension.class));
        // Update the plugins list (all loaded plugins)
        plugins.setAll(pluginManager.getPlugins());
    }

    public void togglePlugin(String pluginId) {
        PluginWrapper plugin = pluginManager.getPlugin(pluginId);
        if (plugin != null) {
            if (plugin.getPluginState() == PluginState.STARTED) {
                pluginManager.stopPlugin(pluginId);
                pluginManager.disablePlugin(pluginId);
            } else {
                pluginManager.enablePlugin(pluginId);
                pluginManager.startPlugin(pluginId);
            }
            refreshLists();
        }
    }

    public void uninstallPlugin(String pluginId) {
        PluginWrapper plugin = pluginManager.getPlugin(pluginId);
        if (plugin != null) {
            Path pluginPath = plugin.getPluginPath();
            pluginManager.stopPlugin(pluginId);
            pluginManager.unloadPlugin(pluginId);

            try {
                if (Files.isDirectory(pluginPath)) {
                    // Recursive deletion of the unzipped directory
                    try (java.util.stream.Stream<Path> walk = Files.walk(pluginPath)) {
                        walk.sorted(java.util.Comparator.reverseOrder())
                                .map(Path::toFile)
                                .forEach(java.io.File::delete);
                    }

                    // Also try to find and delete the original .zip file
                    String zipName = pluginPath.getFileName().toString() + ".zip";
                    Path zipPath = pluginPath.getParent().resolve(zipName);
                    Files.deleteIfExists(zipPath);
                } else {
                    // It's a single JAR file
                    Files.deleteIfExists(pluginPath);
                }
            } catch (IOException e) {
                System.err.println("Failed to delete plugin files: " + e.getMessage());
            }
            refreshLists();
        }
    }

    public void installPlugin(Path sourcePath) throws IOException {
        Path pluginsDir = pluginManager.getPluginsRoots().getFirst();
        Path targetPath = pluginsDir.resolve(sourcePath.getFileName());

        System.out.println("Installing plugin by copying to: " + targetPath);
        Files.copy(sourcePath, targetPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    }

    public void stop() {
        pluginManager.stopPlugins();
        pluginManager.unloadPlugins();
    }

    public ObservableList<ViewExtension> getExtensions() {
        return extensions;
    }

    public ObservableList<PluginWrapper> getPlugins() {
        return plugins;
    }

    /**
     * Get all plugins grouped by category.
     * 
     * @return Map of category name to list of plugins in that category
     */
    public Map<String, List<ViewExtension>> getPluginsByCategory() {
        return extensions.stream()
                .collect(Collectors.groupingBy(ViewExtension::getCategory));
    }

    /**
     * Get all unique categories from loaded plugins.
     * 
     * @return Sorted list of category names
     */
    public List<String> getAllCategories() {
        return extensions.stream()
                .map(ViewExtension::getCategory)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Search/filter plugins by query string.
     * Searches in plugin name, description, keywords, and category.
     * 
     * @param query search query (case-insensitive)
     * @return List of matching plugins
     */
    public List<ViewExtension> searchPlugins(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>(extensions);
        }

        String lowerQuery = query.toLowerCase();
        return extensions.stream()
                .filter(ext -> {
                    String name = ext.getMenuTitle().toLowerCase();
                    String desc = ext.getDescription().toLowerCase();
                    String keywords = ext.getKeywords().toLowerCase();
                    String category = ext.getCategory().toLowerCase();

                    return name.contains(lowerQuery) ||
                            desc.contains(lowerQuery) ||
                            keywords.contains(lowerQuery) ||
                            category.contains(lowerQuery);
                })
                .collect(Collectors.toList());
    }
}
