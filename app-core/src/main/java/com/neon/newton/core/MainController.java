package com.neon.newton.core;

import com.neon.newton.api.ViewExtension;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class MainController {
    private final BorderPane root;
    private final Sidebar sidebar;
    private final StackPane contentArea;
    private ViewExtension activeExtension;

    public MainController() {
        root = new BorderPane();
        contentArea = new StackPane();
        sidebar = new Sidebar(this::handleSelection);

        setupUI();
    }

    private void setupUI() {
        contentArea.getStyleClass().add("content-area");

        Label welcomeMsg = new Label("Select a plugin to begin");
        welcomeMsg.getStyleClass().add("welcome-text");
        contentArea.getChildren().add(welcomeMsg);

        root.setLeft(sidebar);
        root.setCenter(contentArea);
    }

    private void handleSelection(Object selection) {
        if (selection instanceof String && selection.equals("MANAGE")) {
            activeExtension = null;
            contentArea.getChildren().clear();
            contentArea.getChildren().add(new PluginManagementView());
        } else if (selection instanceof ViewExtension ext) {
            activeExtension = ext;
            contentArea.getChildren().clear();
            contentArea.getChildren().add(ext.getView());
        }
    }

    public Pane getRoot() {
        return root;
    }
}
