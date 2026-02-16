package com.neon.newton.core;

import com.neon.newton.core.plugins.PluginService;
import com.neon.newton.core.theme.Theme;
import com.neon.newton.core.theme.ThemeService;
import fr.brouillard.oss.cssfx.CSSFX;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.function.Consumer;

public class NeonNewtonApp extends Application {
    // Keep a strong reference to prevent GC when using weak listeners
    private Consumer<Theme> themeChangeListener;

    @Override
    public void start(Stage primaryStage) {
        // Load Main UI (synchronous for instant display)
        NeonNewtonUI neonNewtonUI = new NeonNewtonUI();
        Scene scene = new Scene(neonNewtonUI.getRoot());

        // Set up window
        primaryStage.setTitle("Neon Newton Plugin Framework");
        // Load icon using resource stream for robustness
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/app-icon (1).png"))));

        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();

        // Initialize Theme Service and apply theme (synchronous)
        startThemeService(scene);

        // Initialize Plugin Service in background (async for better startup performance)
        startPluginService();

        // Enable CSS hot-reload only in dev mode
        if (Boolean.getBoolean("cssfx.enabled")) {
            CSSFX.start();
        }
    }

    private void startThemeService(Scene scene) {
        ThemeService themeService = ThemeService.getInstance();
        themeService.applyTheme(scene);

        // Store listener as field to prevent GC with weak references
        themeChangeListener = t -> themeService.applyTheme(scene);
        themeService.addThemeChangeListener(themeChangeListener);
    }

    private static void startPluginService() {
        Thread pluginThread = new Thread(new PluginServiceInitTask(), "PluginInitializer");
        pluginThread.setDaemon(true);
        pluginThread.start();
    }

    @Override
    public void stop() {
        PluginService.getInstance().stop();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static class PluginServiceInitTask extends Task<Object> {
        @Override
        protected Void call() {
            PluginService.getInstance().init();
            return null;
        }
    }
}
