package com.neon.newton.plugin.weather;

import com.neon.newton.api.ViewExtension;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.pf4j.Extension;

@Extension
public class WeatherExtension implements ViewExtension {
    @Override
    public Node getView() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 30;");
        Label title = new Label("Weather Widget");
        title.setStyle("-fx-font-size: 24px;");
        Node content = new Label("Los Angeles: 72F Sunny");
        root.getChildren().addAll(title, content);
        return root;
    }

    @Override
    public String getMenuTitle() {
        return "Weather Widget";
    }

    @Override
    public Node getIcon() {
        return new FontIcon(Material2AL.CLOUD);
    }

    @Override
    public String getCategory() {
        return "Utilities";
    }

    @Override
    public String getDescription() {
        return "Current LA Weather";
    }

    @Override
    public String getKeywords() {
        return "Weather Widget, Utilities, plugin";
    }
}
