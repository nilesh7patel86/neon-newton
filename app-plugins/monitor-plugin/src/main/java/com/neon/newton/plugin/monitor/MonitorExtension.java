package com.neon.newton.plugin.monitor;

import com.neon.newton.api.ViewExtension;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2MZ;
import org.pf4j.Extension;

@Extension
public class MonitorExtension implements ViewExtension {
    @Override
    public Node getView() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 30;");
        Label title = new Label("System Monitor");
        title.setStyle("-fx-font-size: 24px;");
        Node content = new Label("CPU: 15% | RAM: 45%");
        root.getChildren().addAll(title, content);
        return root;
    }

    @Override
    public String getMenuTitle() {
        return "System Monitor";
    }

    @Override
    public Node getIcon() {
        return new FontIcon(Material2MZ.SPEED);
    }

    @Override
    public String getCategory() {
        return "Utilities";
    }

    @Override
    public String getDescription() {
        return "Shows CPU/RAM usage";
    }

    @Override
    public String getKeywords() {
        return "System Monitor, Utilities, plugin";
    }
}
