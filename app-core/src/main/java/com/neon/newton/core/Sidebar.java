package com.neon.newton.core;

import com.neon.newton.api.ViewExtension;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2MZ;

import java.util.function.Consumer;

public class Sidebar extends VBox {
    private static final String PREF_SIDEBAR_COLLAPSED = "sidebar-collapsed";
    private final Consumer<Object> onSelection; // Object can be ViewExtension or "MANAGE"
    private boolean collapsed;
    private final VBox navContainer;
    private final Button toggleBtn;
    private final Button manageBtn;
    private final Button refreshBtn;
    private final Button logoutBtn;
    private Object activeItem; // ViewExtension or "MANAGE"
    private final Label titleLabel;
    private final Timeline animation = new Timeline();

    public Sidebar(Consumer<Object> onSelection) {
        this.onSelection = onSelection;
        this.collapsed = UserPreferenceService.getInstance().getBoolean(PREF_SIDEBAR_COLLAPSED, false);

        getStyleClass().add("sidebar");
        if (collapsed) {
            getStyleClass().add("collapsed");
        }

        setSpacing(10);
        setPadding(new Insets(20));
        setPrefWidth(collapsed ? 80 : 250);
        setMinWidth(USE_PREF_SIZE);
        setMaxWidth(USE_PREF_SIZE);

        // Header with Toggle
        VBox header = new VBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        toggleBtn = new Button();
        toggleBtn.setGraphic(new FontIcon(Material2MZ.MENU));
        toggleBtn.getStyleClass().add("toggle-button");
        toggleBtn.setOnAction(e -> toggle());

        titleLabel = new Label(collapsed ? "" : "APPLICATIONS");
        titleLabel.getStyleClass().add("sidebar-title");

        header.getChildren().addAll(toggleBtn, titleLabel);

        // Navigation section
        navContainer = new VBox(5);
        VBox.setVgrow(navContainer, Priority.ALWAYS);

        // Footer section
        VBox footer = new VBox(5);

        refreshBtn = new Button(collapsed ? "" : "Refresh Plugins");
        refreshBtn.setGraphic(new FontIcon(Material2MZ.REFRESH));
        refreshBtn.getStyleClass().add("refresh-button");
        refreshBtn.setMaxWidth(Double.MAX_VALUE);
        refreshBtn.setOnAction(e -> PluginService.getInstance().reload());
        if (collapsed)
            Tooltip.install(refreshBtn, new Tooltip("Refresh Plugins"));

        manageBtn = new Button(collapsed ? "" : "Manage Plugins");
        manageBtn.setGraphic(new FontIcon(Material2MZ.SETTINGS));
        manageBtn.getStyleClass().add("manage-button");
        manageBtn.setMaxWidth(Double.MAX_VALUE);
        manageBtn.setOnAction(e -> onSelection.accept("MANAGE"));
        if (collapsed)
            Tooltip.install(manageBtn, new Tooltip("Manage Plugins"));

        logoutBtn = new Button(collapsed ? "" : "Go Out");
        logoutBtn.setGraphic(new FontIcon(Material2AL.LOG_OUT));
        logoutBtn.getStyleClass().add("go-out-button");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setOnAction(e -> Platform.exit());
        if (collapsed)
            Tooltip.install(logoutBtn, new Tooltip("Go Out"));

        footer.getChildren().addAll(refreshBtn, manageBtn, logoutBtn);

        getChildren().addAll(header, navContainer, footer);

        initPluginObserver();
    }

    private void toggle() {
        collapsed = !collapsed;
        UserPreferenceService.getInstance().setBoolean(PREF_SIDEBAR_COLLAPSED, collapsed);

        animation.stop();
        animation.getKeyFrames().clear();

        double targetWidth = collapsed ? 80 : 250;

        // Instant visual changes before animation
        if (!collapsed) {
            getStyleClass().remove("collapsed");
            titleLabel.setText("APPLICATIONS");
            refreshBtn.setText("Refresh Plugins");
            manageBtn.setText("Manage Plugins");
            logoutBtn.setText("Go Out");
            toggleBtn.setGraphic(new FontIcon(Material2MZ.MENU));
            Tooltip.uninstall(refreshBtn, null);
            Tooltip.uninstall(manageBtn, null);
            updatePluginButtons();
        } else {
            toggleBtn.setGraphic(new FontIcon(Material2MZ.MENU_OPEN));
            Tooltip.install(refreshBtn, new Tooltip("Refresh Plugins"));
            Tooltip.install(manageBtn, new Tooltip("Manage Plugins"));
        }

        KeyValue widthValue = new KeyValue(prefWidthProperty(), targetWidth);
        KeyFrame frame = new KeyFrame(Duration.millis(300), widthValue);
        animation.getKeyFrames().add(frame);

        animation.setOnFinished(e -> {
            if (collapsed) {
                getStyleClass().add("collapsed");
                titleLabel.setText("");
                refreshBtn.setText("");
                manageBtn.setText("");
                logoutBtn.setText("");
                updatePluginButtons();
            }
        });

        animation.play();
    }

    private void initPluginObserver() {
        PluginService.getInstance().getExtensions().addListener((ListChangeListener<ViewExtension>) c -> {
            Platform.runLater(this::updatePluginButtons);
        });
        updatePluginButtons();
    }

    private void updatePluginButtons() {
        navContainer.getChildren().clear();
        for (ViewExtension ext : PluginService.getInstance().getExtensions()) {
            Button navButton = new Button();
            navButton.setGraphic(ext.getIcon());
            navButton.getStyleClass().add("nav-button");
            navButton.setMaxWidth(Double.MAX_VALUE);

            if (collapsed) {
                navButton.setText("");
                Tooltip.install(navButton, new Tooltip(ext.getMenuTitle()));
            } else {
                navButton.setText(ext.getMenuTitle());
                Tooltip.uninstall(navButton, null);
            }

            navButton.setOnAction(e -> onSelection.accept(ext));

            if (ext.equals(activeItem)) {
                navButton.getStyleClass().add("active");
            }

            navContainer.getChildren().add(navButton);
        }
    }

    public void setActiveItem(Object activeItem) {
        this.activeItem = activeItem;

        // Update manage button active state
        manageBtn.getStyleClass().remove("active");
        if ("MANAGE".equals(activeItem)) {
            manageBtn.getStyleClass().add("active");
        }

        updatePluginButtons();
    }
}
