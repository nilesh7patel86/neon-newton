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
public class ColorViewExtension implements ViewExtension {

    @Override
    public Node getView() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 40;");

        Label title = new Label("Color Picker");
        title.setStyle("-fx-font-size: 24px; -fx-text-fill: -color-fg-default;");

        ColorPicker colorPicker = new ColorPicker(Color.TEAL);
        colorPicker.setStyle("-fx-font-size: 14px;");

        Label hexLabel = new Label("HEX: #008080");
        hexLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: -color-fg-default; -fx-font-family: monospace;");

        colorPicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            String hex = String.format("#%02X%02X%02X",
                    (int) (newVal.getRed() * 255),
                    (int) (newVal.getGreen() * 255),
                    (int) (newVal.getBlue() * 255));
            hexLabel.setText("HEX: " + hex);
        });

        root.getChildren().addAll(title, colorPicker, hexLabel);
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
        return "Pick and copy colors in HEX format";
    }

    @Override
    public String getKeywords() {
        return "color, picker, hex, rgb, design, palette";
    }
}
