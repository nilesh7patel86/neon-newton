package com.neon.newton.core;

import org.pf4j.PluginWrapper;
import org.pf4j.PluginState;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import java.io.File;
import java.nio.file.Path;
import java.io.IOException;

public class PluginManagementView extends VBox {

    public PluginManagementView() {
        setSpacing(20);
        setPadding(new Insets(20));
        getStyleClass().add("plugin-manager-view");

        setupHeader();
        setupPluginList();
    }

    private void setupHeader() {
        HBox header = new HBox();
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        VBox titleBox = new VBox(5);
        Label title = new Label("Plugin Manager");
        title.getStyleClass().add("plugin-manager-title");

        Label desc = new Label("Enable, disable, or install plugins at runtime.");
        desc.getStyleClass().add("plugin-manager-desc");
        titleBox.getChildren().addAll(title, desc);
        HBox.setHgrow(titleBox, Priority.ALWAYS);

        Button installBtn = new Button("Install Plugin");
        installBtn.getStyleClass().add("action-button");
        installBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Plugin File");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Plugin Files", "*.jar", "*.zip"));
            File selectedFile = fileChooser.showOpenDialog(getScene().getWindow());
            if (selectedFile != null) {
                try {
                    PluginService.getInstance().installPlugin(selectedFile.toPath());
                } catch (IOException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to install plugin: " + ex.getMessage());
                    alert.showAndWait();
                }
            }
        });

        header.getChildren().addAll(titleBox, installBtn);
        getChildren().add(header);

        setupThemeSelector();
    }

    private void setupThemeSelector() {
        HBox themeBox = new HBox(15);
        themeBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        themeBox.getStyleClass().add("theme-selector-container");

        Label themeLabel = new Label("Application Theme:");
        themeLabel.getStyleClass().add("theme-label");

        javafx.scene.control.ComboBox<Theme> themeCombo = new javafx.scene.control.ComboBox<>();
        themeCombo.setItems(ThemeService.getInstance().getAvailableThemes());
        themeCombo.setValue(ThemeService.getInstance().getCurrentTheme());

        themeCombo.setOnAction(e -> ThemeService.getInstance().setTheme(themeCombo.getValue()));

        themeBox.getChildren().addAll(themeLabel, themeCombo);
        getChildren().add(themeBox);
    }

    private void setupPluginList() {
        VBox listContainer = new VBox(10);
        listContainer.getStyleClass().add("plugin-list-container");

        PluginService.getInstance().getPlugins()
                .addListener((javafx.collections.ListChangeListener<PluginWrapper>) c -> {
                    renderPlugins(listContainer);
                });

        renderPlugins(listContainer);

        ScrollPane scrollPane = new ScrollPane(listContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        getChildren().add(scrollPane);
    }

    private void renderPlugins(VBox container) {
        container.getChildren().clear();

        for (PluginWrapper plugin : PluginService.getInstance().getPlugins()) {
            HBox row = new HBox(15);
            row.setPadding(new Insets(15));
            row.setStyle("-fx-background-color: rgba(255, 255, 255, 0.05); -fx-background-radius: 8;");
            row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            VBox info = new VBox(5);
            Label name = new Label(plugin.getPluginId() + " [" + plugin.getDescriptor().getVersion() + "]");
            name.getStyleClass().add("plugin-manager-title"); // Reuse for consistency or add a small variant
            name.setStyle("-fx-font-size: 16px;"); // Minor tweak

            Label stateLabel = new Label("Status: " + plugin.getPluginState());
            stateLabel.getStyleClass().add(getStatusClass(plugin.getPluginState()));

            info.getChildren().addAll(name, stateLabel);
            HBox.setHgrow(info, Priority.ALWAYS);

            Button toggleBtn = new Button(plugin.getPluginState() == PluginState.STARTED ? "Disable" : "Enable");
            toggleBtn.getStyleClass().add("action-button");
            toggleBtn.setOnAction(e -> PluginService.getInstance().togglePlugin(plugin.getPluginId()));

            Button uninstallBtn = new Button("Uninstall");
            uninstallBtn.getStyleClass().add("uninstall-button");
            uninstallBtn.setOnAction(e -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                        "Are you sure you want to uninstall " + plugin.getPluginId() + "?", ButtonType.YES,
                        ButtonType.NO);
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        PluginService.getInstance().uninstallPlugin(plugin.getPluginId());
                    }
                });
            });

            row.getChildren().addAll(info, toggleBtn, uninstallBtn);
            container.getChildren().add(row);
        }

        if (PluginService.getInstance().getPlugins().isEmpty()) {
            container.getChildren().add(new Label("No plugins discovered yet."));
        }
    }

    private String getStatusClass(PluginState state) {
        switch (state) {
            case STARTED:
                return "plugin-status-started";
            case STOPPED:
                return "plugin-status-stopped";
            case DISABLED:
                return "plugin-status-disabled";
            default:
                return "plugin-status-default";
        }
    }
}
