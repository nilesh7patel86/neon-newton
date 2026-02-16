package com.neon.newton.plugin.speed;

import com.neon.newton.api.ViewExtension;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2MZ;
import org.pf4j.Extension;

@Extension
public class SpeedExtension implements ViewExtension {
    @Override
    public Node getView() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 30;");
        Label title = new Label("Network Speed");
        title.setStyle("-fx-font-size: 24px;");
        Node content = new Label("Down: 450 Mbps | Up: 25 Mbps");
        root.getChildren().addAll(title, content);
        return root;
    }

    @Override
    public String getMenuTitle() {
        return "Network Speed";
    }

    @Override
    public Node getIcon() {
        return new FontIcon(Material2MZ.NETWORK_CHECK);
    }

    @Override
    public String getCategory() {
        return "Network";
    }

    @Override
    public String getDescription() {
        return "Upload/Download";
    }

    @Override
    public String getKeywords() {
        return "Network Speed, Network, plugin";
    }
}
