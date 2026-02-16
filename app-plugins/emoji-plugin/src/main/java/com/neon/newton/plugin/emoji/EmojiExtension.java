package com.neon.newton.plugin.emoji;

import com.neon.newton.api.ViewExtension;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.pf4j.Extension;

@Extension
public class EmojiExtension implements ViewExtension {
    @Override
    public Node getView() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 30;");
        Label title = new Label("Emoji Board");
        title.setStyle("-fx-font-size: 24px;");
        Node content = new Label("Happy Cool Rocket Laptop Rainbow");
        root.getChildren().addAll(title, content);
        return root;
    }

    @Override
    public String getMenuTitle() {
        return "Emoji Board";
    }

    @Override
    public Node getIcon() {
        return new FontIcon(Material2AL.INSERT_EMOTICON);
    }

    @Override
    public String getCategory() {
        return "Utilities";
    }

    @Override
    public String getDescription() {
        return "Copy emojis";
    }

    @Override
    public String getKeywords() {
        return "Emoji Board, Utilities, plugin";
    }
}
