package com.neon.newton.core;

import fr.brouillard.oss.cssfx.CSSFX;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Initialize Plugin Service
        PluginService.getInstance().init();

        // Load Main UI
        MainController mainController = new MainController();
        Scene scene = new Scene(mainController.getRoot(), 1000, 700);

        // Initialize Theme Service and apply theme
        ThemeService themeService = ThemeService.getInstance();
        themeService.applyTheme(scene);
        themeService.addThemeChangeListener(t -> themeService.applyTheme(scene));

        primaryStage.setTitle("Neon Newton Plugin Framework");
        primaryStage.getIcons().add(new Image("icons/app-icon (1).png"));
        primaryStage.setScene(scene);
        primaryStage.show();
        CSSFX.start();
    }

    @Override
    public void stop() {
        PluginService.getInstance().stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
