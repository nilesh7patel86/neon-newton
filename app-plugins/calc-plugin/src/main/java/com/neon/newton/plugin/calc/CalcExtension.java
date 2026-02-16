package com.neon.newton.plugin.calc;

import com.neon.newton.api.ViewExtension;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.pf4j.Extension;

@Extension
public class CalcExtension implements ViewExtension {
    @Override
    public Node getView() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 30;");
        Label title = new Label("Calculator");
        title.setStyle("-fx-font-size: 24px;");
        Node content = new Label("Calculator Widget (Mock)");
        root.getChildren().addAll(title, content);
        return root;
    }

    @Override
    public String getMenuTitle() {
        return "Calculator";
    }

    @Override
    public Node getIcon() {
        return new FontIcon(Material2AL.CALCULATE);
    }

    @Override
    public String getCategory() {
        return "Utilities";
    }

    @Override
    public String getDescription() {
        return "Basic Math";
    }

    @Override
    public String getKeywords() {
        return "Calculator, Utilities, plugin";
    }
}
