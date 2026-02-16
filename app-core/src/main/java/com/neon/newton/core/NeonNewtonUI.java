package com.neon.newton.core;

import com.neon.newton.api.ViewExtension;
import com.neon.newton.core.plugins.PluginLauncher;
import com.neon.newton.core.plugins.PluginManagementView;
import com.neon.newton.core.preferences.UserPreferencesView;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class NeonNewtonUI {
    private final BorderPane root;
    private final Sidebar sidebar;
    private final StackPane contentArea;
    private final PluginLauncher pluginLauncher;
    private ViewExtension activeExtension;

    public NeonNewtonUI() {
        root = new BorderPane();
        contentArea = new StackPane();
        sidebar = new Sidebar(this::handleSelection);
        pluginLauncher = new PluginLauncher(this::launchPlugin, this::hideLauncher);
        pluginLauncher.setVisible(false);

        setupUI();
        setupKeyboardShortcuts();
    }

    private void setupUI() {
        contentArea.getStyleClass().add("content-area");

        Label welcomeMsg = new Label("Select a plugin to begin");
        welcomeMsg.getStyleClass().add("welcome-text");
        contentArea.getChildren().add(welcomeMsg);

        // Add launcher overlay on top
        contentArea.getChildren().add(pluginLauncher);

        root.setLeft(sidebar);
        root.setCenter(contentArea);
    }

    private void setupKeyboardShortcuts() {
        // Ctrl+K to open plugin launcher
        KeyCombination launcherShortcut = new KeyCodeCombination(KeyCode.K, KeyCombination.CONTROL_DOWN);

        root.setOnKeyPressed(event -> {
            if (launcherShortcut.match(event)) {
                showLauncher();
                event.consume();
            }
        });
    }

    private void handleSelection(Object selection) {
        if (selection instanceof String && selection.equals("SETTINGS")) {
            activeExtension = null;
            switchView(new UserPreferencesView(sidebar), "SETTINGS");
        } else if (selection instanceof String && selection.equals("MANAGE")) {
            activeExtension = null;
            switchView(new PluginManagementView(), "MANAGE");
        } else if (selection instanceof ViewExtension ext) {
            activeExtension = ext;
            switchView(ext.getView(), ext);
        }
    }

    private void switchView(javafx.scene.Node view, Object activeItem) {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(view);
        contentArea.getChildren().add(pluginLauncher); // Keep launcher on top
        sidebar.setActiveItem(activeItem);
    }

    private void showLauncher() {
        pluginLauncher.show();
    }

    private void hideLauncher() {
        pluginLauncher.setVisible(false);
    }

    private void launchPlugin(ViewExtension plugin) {
        activeExtension = plugin;
        switchView(plugin.getView(), plugin);
    }

    public Pane getRoot() {
        return root;
    }
}
