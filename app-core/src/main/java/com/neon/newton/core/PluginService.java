package com.neon.newton.core;

import com.neon.newton.api.ViewExtension;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.pf4j.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
            Path pluginsRoot = pluginManager.getPluginsRoot();
            System.out.println("Monitoring plugins directory: " + pluginsRoot.toAbsolutePath());

            pluginsRoot.register(watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY);

            watchExecutor.submit(() -> {
                try {
                    WatchKey key;
                    while ((key = watchService.take()) != null) {
                        for (WatchEvent<?> event : key.pollEvents()) {
                            WatchEvent.Kind<?> kind = event.kind();
                            if (kind == StandardWatchEventKinds.OVERFLOW)
                                continue;

                            Path fileName = (Path) event.context();
                            String nameStr = fileName.toString();

                            // Only care about jars and zips. Ignore temp files (common during copy).
                            if ((nameStr.endsWith(".jar") || nameStr.endsWith(".zip")) &&
                                    !nameStr.startsWith(".") && !nameStr.contains("~")) {

                                System.out.println("Detected interesting change: " + nameStr);

                                // Debounce: Wait for file to be completely written.
                                // Large files can take multiple write events.
                                Thread.sleep(1000);

                                Platform.runLater(() -> {
                                    try {
                                        Path fullPath = pluginsRoot.resolve(fileName);
                                        if (!Files.exists(fullPath))
                                            return;

                                        System.out.println("Attempting to auto-load: " + nameStr);

                                        // If already loaded, unload first to support "hot swap" of same file
                                        PluginWrapper existing = pluginManager.getPlugins().stream()
                                                .filter(p -> p.getPluginPath().equals(fullPath))
                                                .findFirst().orElse(null);

                                        if (existing != null) {
                                            System.out.println(
                                                    "Plugin already loaded, unloading first: "
                                                            + existing.getPluginId());
                                            pluginManager.unloadPlugin(existing.getPluginId());
                                        }

                                        String pluginId = pluginManager.loadPlugin(fullPath);
                                        if (pluginId != null) {
                                            pluginManager.startPlugin(pluginId);
                                            refreshLists();
                                            System.out.println("Successfully auto-loaded: " + pluginId);
                                        }
                                    } catch (PluginAlreadyLoadedException p) {
                                        System.err.println("Auto-load failed for " + nameStr + ": " + p.getMessage());
                                    } catch (Exception e) {
                                        System.err.println("Auto-load failed for " + nameStr + ": " + e.getMessage());
                                        e.printStackTrace();
                                    }
                                });
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
        Path pluginsDir = pluginManager.getPluginsRoot();
        Path targetPath = pluginsDir.resolve(sourcePath.getFileName());
        Files.copy(sourcePath, targetPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        String pluginId = pluginManager.loadPlugin(targetPath);
        if (pluginId != null) {
            pluginManager.startPlugin(pluginId);
        }
        refreshLists();
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
}
