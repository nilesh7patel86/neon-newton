package com.neon.newton.plugin.currency;

import com.neon.newton.api.ViewExtension;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.pf4j.Extension;

@Extension
public class CurrencyExtension implements ViewExtension {
    @Override
    public Node getView() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 30;");
        Label title = new Label("Currency Converter");
        title.setStyle("-fx-font-size: 24px;");
        Node content = new Label("1 USD = 0.92 EUR");
        root.getChildren().addAll(title, content);
        return root;
    }

    @Override
    public String getMenuTitle() {
        return "Currency Converter";
    }

    @Override
    public Node getIcon() {
        return new FontIcon(Material2AL.ATTACH_MONEY);
    }

    @Override
    public String getCategory() {
        return "Utilities";
    }

    @Override
    public String getDescription() {
        return "USD to EUR";
    }

    @Override
    public String getKeywords() {
        return "Currency Converter, Utilities, plugin";
    }
}
