package com.neon.newton.api;

import javafx.scene.Node;
import org.pf4j.ExtensionPoint;

public interface ViewExtension extends ExtensionPoint {
    /**
     * Returns the title to be displayed in the sidebar menu for this plugin.
     *
     * @return menu title
     */
    String getMenuTitle();

    /**
     * Returns the JavaFX Node that represents the main content of this plugin.
     * This will be displayed in the main area when the plugin is selected.
     *
     * @return main content Node
     */
    Node getView();

    /**
     * Returns an optional icon to be displayed next to the menu title in the sidebar.
     * This can be a small image or graphic representing the plugin.
     *
     * @return icon Node (can be null if no icon is desired)
     */
    Node getIcon();

    /**
     * Returns the category this plugin belongs to.
     * Used for grouping plugins in the sidebar.
     *
     * @return category name (default: "Uncategorized")
     */
    default String getCategory() {
        return "Uncategorized";
    }

    /**
     * Returns a brief description of this plugin.
     * Used in the plugin launcher and search results.
     *
     * @return plugin description (default: empty string)
     */
    default String getDescription() {
        return "";
    }

    /**
     * Returns search keywords for this plugin.
     * Used for enhanced fuzzy search in the plugin launcher.
     *
     * @return comma-separated keywords (default: empty string)
     */
    default String getKeywords() {
        return "";
    }
}
