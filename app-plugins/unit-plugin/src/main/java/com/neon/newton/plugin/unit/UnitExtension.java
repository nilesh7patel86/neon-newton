package com.neon.newton.plugin.unit;

import com.neon.newton.api.ViewExtension;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2MZ;
import org.pf4j.Extension;

@Extension
public class UnitExtension implements ViewExtension {
    @Override
    public Node getView() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 30;");
        Label title = new Label("Unit Converter");
        title.setStyle("-fx-font-size: 24px;");
        Node content = new Label("100km = 62.1 miles");
        root.getChildren().addAll(title, content);
        return root;
    }

    @Override
    public String getMenuTitle() {
        return "Unit Converter";
    }

    @Override
    public Node getIcon() {
        return new FontIcon(Material2MZ.STRAIGHTEN);
    }

    @Override
    public String getCategory() {
        return "Utilities";
    }

    @Override
    public String getDescription() {
        return "Metric to Imperial";
    }

    @Override
    public String getKeywords() {
        return "Unit Converter, Utilities, plugin";
    }
}
