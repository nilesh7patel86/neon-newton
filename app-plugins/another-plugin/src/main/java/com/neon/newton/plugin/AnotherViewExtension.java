package com.neon.newton.plugin;

import com.neon.newton.api.ViewExtension;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.pf4j.Extension;

@Extension
public class AnotherViewExtension implements ViewExtension {
    @Override
    public String getMenuTitle() {
        return "Another";
    }

    @Override
    public Node getView() {
        VBox container = new VBox(20);
        Label title = new Label("Welcome to the Modular App!");
        title.setStyle("-fx-font-size: 32px; -fx-text-fill: white;");

        Label description = new Label("This view is provided by the Another Plugin.");
        description.setStyle("-fx-font-size: 16px; -fx-text-fill: #aaa;");

        container.getChildren().addAll(title, description);
        return container;
    }

    @Override
    public String getIconPath() {
        return null;
    }
}
