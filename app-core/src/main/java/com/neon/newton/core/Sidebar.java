package com.neon.newton.core;

import com.neon.newton.api.ViewExtension;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2MZ;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Sidebar extends VBox {
    private static final String PREF_SIDEBAR_COLLAPSED = "sidebar-collapsed";
    private final Consumer<Object> onSelection; // Object can be ViewExtension or "MANAGE" or "SETTINGS"
    private boolean collapsed;
    private final VBox navContainer;
    private final Button toggleBtn;
    private final Button quickLaunchBtn;
    private final Button manageBtn;
    private final Button refreshBtn;
    private final Button settingsBtn;
    private final Button logoutBtn;
    private Object activeItem; // ViewExtension or String
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

        // Quick Launch Button
        quickLaunchBtn = new Button(collapsed ? "" : "Quick Launch (Ctrl+K)");
        quickLaunchBtn.setGraphic(new FontIcon(Material2MZ.SEARCH));
        quickLaunchBtn.getStyleClass().add("action-button");
        quickLaunchBtn.setStyle("-fx-background-color: -color-accent-subtle; -fx-text-fill: -color-accent-fg;");
        quickLaunchBtn.setMaxWidth(Double.MAX_VALUE);
        quickLaunchBtn.setOnAction(e -> {
            // Trigger Ctrl+K logic via robot since we can't access private method
            if (getScene() != null) {
                javafx.scene.robot.Robot robot = new javafx.scene.robot.Robot();
                robot.keyPress(javafx.scene.input.KeyCode.CONTROL);
                robot.keyType(javafx.scene.input.KeyCode.K);
                robot.keyRelease(javafx.scene.input.KeyCode.CONTROL);
            }
        });
        if (collapsed)
            Tooltip.install(quickLaunchBtn, new Tooltip("Quick Launch (Ctrl+K)"));

        // Navigation section (Accordion or Flat List)
        navContainer = new VBox(5);
        VBox.setVgrow(navContainer, Priority.ALWAYS);

        // Footer section
        VBox footer = new VBox(5);

        refreshBtn = createFooterButton("Refresh Plugins", Material2MZ.REFRESH, "refresh-button",
                e -> PluginService.getInstance().reload());
        settingsBtn = createFooterButton("Settings", Material2MZ.SETTINGS, "settings-button",
                e -> onSelection.accept("SETTINGS"));
        manageBtn = createFooterButton("Manage Plugins", Material2MZ.WIDGETS, "manage-button",
                e -> onSelection.accept("MANAGE"));
        logoutBtn = createFooterButton("Go Out", Material2AL.LOG_OUT, "go-out-button", e -> Platform.exit());

        footer.getChildren().addAll(refreshBtn, settingsBtn, manageBtn, logoutBtn);

        getChildren().addAll(header, quickLaunchBtn, navContainer, footer);

        initPluginObserver();
    }

    // Helper to create footer buttons to reduce boilerplate
    private Button createFooterButton(String text, org.kordamp.ikonli.Ikon icon, String styleClass,
            javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        Button btn = new Button(collapsed ? "" : text);
        btn.setGraphic(new FontIcon(icon));
        btn.getStyleClass().add(styleClass);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(action);
        if (collapsed)
            Tooltip.install(btn, new Tooltip(text));
        return btn;
    }

    public void toggle() {
        collapsed = !collapsed;
        UserPreferenceService.getInstance().setBoolean(PREF_SIDEBAR_COLLAPSED, collapsed);

        animation.stop();
        animation.getKeyFrames().clear();

        double targetWidth = collapsed ? 80 : 250;

        // Instant visual changes
        if (!collapsed) {
            getStyleClass().remove("collapsed");
            titleLabel.setText("APPLICATIONS");
            quickLaunchBtn.setText("Quick Launch (Ctrl+K)");
            updateFooterButtonsText(false);
            toggleBtn.setGraphic(new FontIcon(Material2MZ.MENU));
        } else {
            toggleBtn.setGraphic(new FontIcon(Material2MZ.MENU_OPEN));
            quickLaunchBtn.setText("");
        }

        // Visual updates that depend on state
        updatePluginButtons();
        updateFooterTooltips();

        KeyValue widthValue = new KeyValue(prefWidthProperty(), targetWidth);
        KeyFrame frame = new KeyFrame(Duration.millis(300), widthValue);
        animation.getKeyFrames().add(frame);

        animation.setOnFinished(e -> {
            if (collapsed) {
                getStyleClass().add("collapsed");
                titleLabel.setText("");
                updateFooterButtonsText(true);
            }
        });

        animation.play();
    }

    private void updateFooterButtonsText(boolean isCollapsed) {
        if (isCollapsed) {
            refreshBtn.setText("");
            settingsBtn.setText("");
            manageBtn.setText("");
            logoutBtn.setText("");
        } else {
            refreshBtn.setText("Refresh Plugins");
            settingsBtn.setText("Settings");
            manageBtn.setText("Manage Plugins");
            logoutBtn.setText("Go Out");
        }
    }

    private void updateFooterTooltips() {
        if (collapsed) {
            Tooltip.install(quickLaunchBtn, new Tooltip("Quick Launch (Ctrl+K)"));
            Tooltip.install(refreshBtn, new Tooltip("Refresh Plugins"));
            Tooltip.install(settingsBtn, new Tooltip("Settings"));
            Tooltip.install(manageBtn, new Tooltip("Manage Plugins"));
            Tooltip.install(logoutBtn, new Tooltip("Go Out"));
        } else {
            Tooltip.uninstall(quickLaunchBtn, null);
            Tooltip.uninstall(refreshBtn, null);
            Tooltip.uninstall(settingsBtn, null);
            Tooltip.uninstall(manageBtn, null);
            Tooltip.uninstall(logoutBtn, null);
        }
    }

    public boolean isCollapsed() {
        return collapsed;
    }

    private void initPluginObserver() {
        PluginService.getInstance().getExtensions()
                .addListener((ListChangeListener<ViewExtension>) c -> Platform.runLater(this::updatePluginButtons));
        updatePluginButtons();
    }

    private void updatePluginButtons() {
        navContainer.getChildren().clear();

        if (collapsed) {
            // Flat list of icons when collapsed
            for (ViewExtension ext : PluginService.getInstance().getExtensions()) {
                navContainer.getChildren().add(createNavButton(ext));
            }
        } else {
            // Categorized Accordion when expanded
            Accordion accordion = new Accordion();
            Map<String, List<ViewExtension>> pluginsByCategory = PluginService.getInstance().getPluginsByCategory();

            for (String category : PluginService.getInstance().getAllCategories()) {
                VBox categoryContent = new VBox(5);
                categoryContent.setPadding(new Insets(10, 0, 10, 0)); // Padding inside pane

                List<ViewExtension> plugins = pluginsByCategory.get(category);
                if (plugins != null) {
                    for (ViewExtension ext : plugins) {
                        categoryContent.getChildren().add(createNavButton(ext));
                    }
                }

                TitledPane pane = new TitledPane(category, categoryContent);
                pane.setAnimated(true);
                accordion.getPanes().add(pane);

                // If active item is in this category, expand it
                if (activeItem instanceof ViewExtension active) {
                    if (category.equals(active.getCategory())) {
                        accordion.setExpandedPane(pane);
                    }
                }
            }

            // Expand first pane if nothing active
            if (accordion.getExpandedPane() == null && !accordion.getPanes().isEmpty()) {
                accordion.setExpandedPane(accordion.getPanes().getFirst());
            }

            navContainer.getChildren().add(accordion);
        }
    }

    private Button createNavButton(ViewExtension ext) {
        Button navButton = new Button();
        navButton.setGraphic(ext.getIcon());
        navButton.getStyleClass().add("nav-button");
        navButton.setMaxWidth(Double.MAX_VALUE);
        navButton.setUserData(ext);

        if (collapsed) {
            navButton.setText("");
            Tooltip.install(navButton, new Tooltip(ext.getMenuTitle() + "\n(" + ext.getCategory() + ")"));
        } else {
            navButton.setText(ext.getMenuTitle());
            // No tooltip needed when expanded, usually
        }

        navButton.setOnAction(e -> onSelection.accept(ext));

        if (ext.equals(activeItem)) {
            navButton.getStyleClass().add("active");
        }

        return navButton;
    }

    public void setActiveItem(Object item) {
        this.activeItem = item;

        // Visual update is tricky with Accordion regeneration
        // easiest is to re-render or find button.
        // Re-rendering is safer to ensure correct expansion state.
        updatePluginButtons();

        // Footer buttons active state
        refreshBtn.getStyleClass().remove("active");
        settingsBtn.getStyleClass().remove("active");
        manageBtn.getStyleClass().remove("active");
        logoutBtn.getStyleClass().remove("active");

        if (item instanceof String itemStr) {
            if (itemStr.equals("SETTINGS"))
                settingsBtn.getStyleClass().add("active");
            else if (itemStr.equals("MANAGE"))
                manageBtn.getStyleClass().add("active");
        }
    }
}
