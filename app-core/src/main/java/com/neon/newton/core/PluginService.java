package com.neon.newton.core;

import com.neon.newton.api.ViewExtension;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PluginService {
    private static PluginService instance;
    private final PluginManager pluginManager;
    private final ObservableList<ViewExtension> extensions = FXCollections.observableArrayList();

    private PluginService() {
        Path pluginsDir = Paths.get("plugins");
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
    }

    public void reload() {
        // Stop and unload existing plugins to ensure a clean state
        pluginManager.stopPlugins();
        pluginManager.unloadPlugins();

        // Load and start plugins from the folder
        pluginManager.loadPlugins();
        pluginManager.startPlugins();

        // Update the observable list
        extensions.setAll(pluginManager.getExtensions(ViewExtension.class));
    }

    public void stop() {
        pluginManager.stopPlugins();
        pluginManager.unloadPlugins();
    }

    public ObservableList<ViewExtension> getExtensions() {
        return extensions;
    }
}
