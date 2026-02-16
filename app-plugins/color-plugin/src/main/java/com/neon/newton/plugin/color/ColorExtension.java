package com.neon.newton.plugin.color;

import com.neon.newton.api.ViewExtension;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2MZ;
import org.pf4j.Extension;

@Extension
public class ColorExtension implements ViewExtension {
    @Override
    public Node getView() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 30;");
        Label title = new Label("Color Picker");
        title.setStyle("-fx-font-size: 24px;");
        Node content = new ColorPicker(Color.TEAL);
        root.getChildren().addAll(title, content);
        return root;
    }

    @Override
    public String getMenuTitle() {
        return "Color Picker";
    }

    @Override
    public Node getIcon() {
        return new FontIcon(Material2MZ.PALETTE);
    }

    @Override
    public String getCategory() {
        return "Design";
    }

    @Override
    public String getDescription() {
        return "HEX Color tool";
    }

    @Override
    public String getKeywords() {
        return "Color Picker, Design, plugin";
    }
}
