package com.neon.newton.core;

import com.neon.newton.api.ViewExtension;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.List;
import java.util.function.Consumer;

/**
 * Command palette-style plugin launcher overlay.
 * Triggered by Ctrl+K keyboard shortcut.
 */
public class PluginLauncher extends StackPane {
    private final TextField searchField;
    private final ListView<ViewExtension> resultsList;
    private final Consumer<ViewExtension> onPluginSelected;
    private final Runnable onClose;

    public PluginLauncher(Consumer<ViewExtension> onPluginSelected, Runnable onClose) {
        this.onPluginSelected = onPluginSelected;
        this.onClose = onClose;

        getStyleClass().add("plugin-launcher-overlay");
        setAlignment(Pos.CENTER);

        // Create launcher dialog
        VBox dialog = new VBox(0);
        dialog.getStyleClass().add("plugin-launcher-dialog");
        dialog.setMaxWidth(600);
        dialog.setMaxHeight(500);

        // Search field
        searchField = new TextField();
        searchField.setPromptText("Search plugins... (type to filter)");
        searchField.getStyleClass().add("plugin-launcher-search");

        // Results list
        resultsList = new ListView<>();
        resultsList.getStyleClass().add("plugin-launcher-results");
        resultsList.setCellFactory(lv -> new PluginCell());
        VBox.setVgrow(resultsList, Priority.ALWAYS);

        // Hint text
        HBox hint = new HBox(10);
        hint.setAlignment(Pos.CENTER);
        hint.getStyleClass().add("plugin-launcher-hint");
        hint.setPadding(new Insets(10));

        Label hintText = new Label("↑↓ Navigate  •  Enter Select  •  Esc Close");
        hintText.setStyle("-fx-text-fill: #888; -fx-font-size: 11px;");
        hint.getChildren().add(hintText);

        dialog.getChildren().addAll(searchField, resultsList, hint);

        getChildren().add(dialog);

        setupEventHandlers();
        loadAllPlugins();
    }

    private void setupEventHandlers() {
        // Search as you type
        searchField.textProperty().addListener((obs, old, newVal) -> filterPlugins(newVal));

        // Keyboard navigation
        searchField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DOWN) {
                resultsList.requestFocus();
                if (resultsList.getSelectionModel().isEmpty() && !resultsList.getItems().isEmpty()) {
                    resultsList.getSelectionModel().selectFirst();
                }
                event.consume();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                close();
                event.consume();
            } else if (event.getCode() == KeyCode.ENTER) {
                selectCurrentPlugin();
                event.consume();
            }
        });

        resultsList.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                close();
                event.consume();
            } else if (event.getCode() == KeyCode.ENTER) {
                selectCurrentPlugin();
                event.consume();
            }
        });

        // Mouse selection
        resultsList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                selectCurrentPlugin();
            }
        });

        // Close on background click
        setOnMouseClicked(event -> {
            if (event.getTarget() == this) {
                close();
            }
        });
    }

    private void loadAllPlugins() {
        List<ViewExtension> plugins = PluginService.getInstance().searchPlugins("");
        resultsList.getItems().setAll(plugins);
        if (!plugins.isEmpty()) {
            resultsList.getSelectionModel().selectFirst();
        }
    }

    private void filterPlugins(String query) {
        List<ViewExtension> filtered = PluginService.getInstance().searchPlugins(query);
        resultsList.getItems().setAll(filtered);
        if (!filtered.isEmpty()) {
            resultsList.getSelectionModel().selectFirst();
        }
    }

    private void selectCurrentPlugin() {
        ViewExtension selected = resultsList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            onPluginSelected.accept(selected);
            close();
        }
    }

    private void close() {
        onClose.run();
    }

    public void show() {
        setVisible(true);
        searchField.clear();
        loadAllPlugins();
        searchField.requestFocus();
    }

    /**
     * Custom cell renderer for plugin results
     */
    private static class PluginCell extends ListCell<ViewExtension> {
        private final HBox container;
        private final FontIcon icon;
        private final VBox textContainer;
        private final Label nameLabel;
        private final Label descLabel;
        private final Label categoryLabel;

        public PluginCell() {
            container = new HBox(12);
            container.setAlignment(Pos.CENTER_LEFT);
            container.setPadding(new Insets(8, 12, 8, 12));

            icon = new FontIcon();
            icon.setIconSize(24);

            textContainer = new VBox(2);
            HBox.setHgrow(textContainer, Priority.ALWAYS);

            nameLabel = new Label();
            nameLabel.getStyleClass().add("plugin-launcher-item-name");

            descLabel = new Label();
            descLabel.getStyleClass().add("plugin-launcher-item-desc");

            categoryLabel = new Label();
            categoryLabel.getStyleClass().add("plugin-launcher-item-category");

            textContainer.getChildren().addAll(nameLabel, descLabel);
            container.getChildren().addAll(icon, textContainer, categoryLabel);
        }

        @Override
        protected void updateItem(ViewExtension plugin, boolean empty) {
            super.updateItem(plugin, empty);

            if (empty || plugin == null) {
                setGraphic(null);
            } else {
                nameLabel.setText(plugin.getMenuTitle());
                descLabel.setText(plugin.getDescription());
                categoryLabel.setText(plugin.getCategory());

                if (plugin.getIcon() instanceof FontIcon pluginIcon) {
                    icon.setIconCode(pluginIcon.getIconCode());
                }

                setGraphic(container);
            }
        }
    }
}
