package com.neon.newton.core;

import fr.brouillard.oss.cssfx.CSSFX;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.function.Consumer;

public class MainApp extends Application {
    // Keep a strong reference to prevent GC when using weak listeners
    private Consumer<Theme> themeChangeListener;

    @Override
    public void start(Stage primaryStage) {
        // Load Main UI (synchronous for instant display)
        MainController mainController = new MainController();
        Scene scene = new Scene(mainController.getRoot(), 1000, 700);

        // Initialize Theme Service and apply theme (synchronous)
        ThemeService themeService = ThemeService.getInstance();
        themeService.applyTheme(scene);

        // Store listener as field to prevent GC with weak references
        themeChangeListener = t -> themeService.applyTheme(scene);
        themeService.addThemeChangeListener(themeChangeListener);

        // Set up window
        primaryStage.setTitle("Neon Newton Plugin Framework");

        // Load icon using resource stream for robustness
        try (InputStream iconStream = getClass().getResourceAsStream("/icons/app-icon (1).png")) {
            if (iconStream != null) {
                primaryStage.getIcons().add(new Image(iconStream));
            }
        } catch (Exception e) {
            System.err.println("Failed to load application icon: " + e.getMessage());
        }

        primaryStage.setScene(scene);
        primaryStage.show();

        // Initialize Plugin Service in background (async for better startup
        // performance)
        Task<Void> pluginInitTask = new Task<>() {
            @Override
            protected Void call() {
                PluginService.getInstance().init();
                return null;
            }
        };

        Thread pluginThread = new Thread(pluginInitTask, "PluginInitializer");
        pluginThread.setDaemon(true);
        pluginThread.start();

        // Enable CSS hot-reload only in dev mode
        if (Boolean.getBoolean("cssfx.enabled")) {
            CSSFX.start();
        }
    }

    @Override
    public void stop() {
        PluginService.getInstance().stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
