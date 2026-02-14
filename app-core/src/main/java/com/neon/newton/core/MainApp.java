package com.neon.newton.core;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Initialize Plugin Service
        PluginService.getInstance().init();

        // Load Main UI
        MainController mainController = new MainController();
        Scene scene = new Scene(mainController.getRoot(), 1000, 700);

        // Add CSS
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        primaryStage.setTitle("Neon Newton Plugin Framework");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        PluginService.getInstance().stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
