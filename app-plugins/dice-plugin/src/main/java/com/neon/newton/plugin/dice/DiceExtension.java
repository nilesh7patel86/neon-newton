package com.neon.newton.plugin.dice;

import com.neon.newton.api.ViewExtension;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.pf4j.Extension;

@Extension
public class DiceExtension implements ViewExtension {
    @Override
    public Node getView() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 30;");
        Label title = new Label("Dice Roller");
        title.setStyle("-fx-font-size: 24px;");
        Node content = new Button("Roll: 5");
        root.getChildren().addAll(title, content);
        return root;
    }

    @Override
    public String getMenuTitle() {
        return "Dice Roller";
    }

    @Override
    public Node getIcon() {
        return new FontIcon(Material2AL.CASINO);
    }

    @Override
    public String getCategory() {
        return "Games";
    }

    @Override
    public String getDescription() {
        return "Roll a d6";
    }

    @Override
    public String getKeywords() {
        return "Dice Roller, Games, plugin";
    }
}
