package com.neon.newton.api;

import javafx.scene.Node;
import org.pf4j.ExtensionPoint;

public interface ViewExtension extends ExtensionPoint {
    String getMenuTitle();

    Node getView();

    Node getIcon(); // Optional icon for the menu
}
