package com.neon.newton.core;

import com.neon.newton.api.ViewExtension;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MainController {
    private final BorderPane root;
    private final VBox sidebar;
    private final StackPane contentArea;
    private ViewExtension activeExtension;

    public MainController() {
        root = new BorderPane();
        sidebar = new VBox(10);
        contentArea = new StackPane();

        setupUI();
        initPluginObserver();
    }

    private void setupUI() {
        sidebar.setPrefWidth(250);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPadding(new Insets(20));

        Label title = new Label("APPLICATIONS");
        title.getStyleClass().add("sidebar-title");
        sidebar.getChildren().add(title);

        // Add a Refresh Button
        Button refreshBtn = new Button("Refresh Plugins");
        refreshBtn.getStyleClass().add("refresh-button");
        refreshBtn.setOnAction(e -> PluginService.getInstance().reload());
        sidebar.getChildren().add(refreshBtn);

        contentArea.getStyleClass().add("content-area");

        Label welcomeMsg = new Label("Select a plugin to begin");
        welcomeMsg.getStyleClass().add("welcome-text");
        contentArea.getChildren().add(welcomeMsg);

        root.setLeft(sidebar);
        root.setCenter(contentArea);
    }

    private void initPluginObserver() {
        PluginService.getInstance().getExtensions().addListener((ListChangeListener<ViewExtension>) c -> {
            updateSidebar();
        });
        updateSidebar();
    }

    private void updateSidebar() {
        // Clear existing plugin buttons (keep title and refresh btn)
        sidebar.getChildren()
                .removeIf(node -> node instanceof Button && !((Button) node).getText().equals("Refresh Plugins"));

        for (ViewExtension ext : PluginService.getInstance().getExtensions()) {
            Button navButton = new Button(ext.getMenuTitle());
            navButton.setMaxWidth(Double.MAX_VALUE);
            navButton.getStyleClass().add("nav-button");
            navButton.setOnAction(e -> {
                activeExtension = ext;
                contentArea.getChildren().clear();
                contentArea.getChildren().add(ext.getView());
            });
            sidebar.getChildren().add(navButton);
        }

        // Cleanup content if active plugin is gone
        if (activeExtension != null && !PluginService.getInstance().getExtensions().contains(activeExtension)) {
            contentArea.getChildren().clear();
            contentArea.getChildren().add(new Label("Plugin was unloaded."));
            activeExtension = null;
        }
    }

    public Pane getRoot() {
        return root;
    }
}
