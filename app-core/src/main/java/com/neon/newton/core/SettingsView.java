package com.neon.newton.core;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;

public class SettingsView extends VBox {
    private final Sidebar sidebar;

    public SettingsView(Sidebar sidebar) {
        this.sidebar = sidebar;
        setSpacing(20);
        setPadding(new Insets(20));
        getStyleClass().add("settings-view");

        setupHeader();
        setupThemeSettings();
        setupSidebarSettings();
    }

    private void setupHeader() {
        VBox titleBox = new VBox(5);
        Label title = new Label("Settings");
        title.getStyleClass().add("plugin-manager-title");

        Label desc = new Label("Configure application preferences and appearance.");
        desc.getStyleClass().add("plugin-manager-desc");
        titleBox.getChildren().addAll(title, desc);

        getChildren().add(titleBox);
    }

    private void setupThemeSettings() {
        VBox section = new VBox(10);
        section.getStyleClass().add("settings-section");

        Label sectionTitle = new Label("Appearance");
        sectionTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        HBox themeBox = new HBox(15);
        themeBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        themeBox.getStyleClass().add("theme-selector-container");

        Label themeLabel = new Label("Application Theme:");
        themeLabel.getStyleClass().add("theme-label");

        ComboBox<Theme> themeCombo = new ComboBox<>();
        themeCombo.setItems(ThemeService.getInstance().getAvailableThemes());
        themeCombo.setValue(ThemeService.getInstance().getCurrentTheme());
        themeCombo.setOnAction(e -> ThemeService.getInstance().setTheme(themeCombo.getValue()));

        themeBox.getChildren().addAll(themeLabel, themeCombo);
        section.getChildren().addAll(sectionTitle, themeBox);

        getChildren().add(section);
    }

    private void setupSidebarSettings() {
        VBox section = new VBox(10);
        section.getStyleClass().add("settings-section");

        Label sectionTitle = new Label("Navigation");
        sectionTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        HBox sidebarBox = new HBox(15);
        sidebarBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label sidebarLabel = new Label("Sidebar Mode:");
        sidebarLabel.getStyleClass().add("theme-label");

        ToggleGroup sidebarGroup = new ToggleGroup();
        RadioButton expandedRadio = new RadioButton("Expanded");
        RadioButton collapsedRadio = new RadioButton("Collapsed");

        expandedRadio.setToggleGroup(sidebarGroup);
        collapsedRadio.setToggleGroup(sidebarGroup);

        // Load current preference
        boolean isCollapsed = UserPreferenceService.getInstance().getBoolean("sidebar.collapsed", false);
        if (isCollapsed) {
            collapsedRadio.setSelected(true);
        } else {
            expandedRadio.setSelected(true);
        }

        // Save on change and trigger sidebar toggle
        sidebarGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == collapsedRadio) {
                UserPreferenceService.getInstance().setBoolean("sidebar.collapsed", true);
                if (!sidebar.isCollapsed()) {
                    sidebar.toggle();
                }
            } else {
                UserPreferenceService.getInstance().setBoolean("sidebar.collapsed", false);
                if (sidebar.isCollapsed()) {
                    sidebar.toggle();
                }
            }
        });

        sidebarBox.getChildren().addAll(sidebarLabel, expandedRadio, collapsedRadio);
        section.getChildren().addAll(sectionTitle, sidebarBox);

        getChildren().add(section);
    }
}
