package com.neon.newton.plugin.text;

import com.neon.newton.api.ViewExtension;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2MZ;
import org.pf4j.Extension;

@Extension
public class TextExtension implements ViewExtension {
    @Override
    public Node getView() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 30;");
        Label title = new Label("Quick Notes");
        title.setStyle("-fx-font-size: 24px;");
        Node content = new TextArea("Draft your ideas...");
        root.getChildren().addAll(title, content);
        return root;
    }

    @Override
    public String getMenuTitle() {
        return "Quick Notes";
    }

    @Override
    public Node getIcon() {
        return new FontIcon(Material2MZ.NOTE_ADD);
    }

    @Override
    public String getCategory() {
        return "Productivity";
    }

    @Override
    public String getDescription() {
        return "Simple scratchpad";
    }

    @Override
    public String getKeywords() {
        return "Quick Notes, Productivity, plugin";
    }
}
