package com.neon.newton.plugin.ip;

import com.neon.newton.api.ViewExtension;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2MZ;
import org.pf4j.Extension;

@Extension
public class IpExtension implements ViewExtension {
    @Override
    public Node getView() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 30;");
        Label title = new Label("My IP Address");
        title.setStyle("-fx-font-size: 24px;");
        Node content = new Label("192.168.1.105");
        root.getChildren().addAll(title, content);
        return root;
    }

    @Override
    public String getMenuTitle() {
        return "My IP Address";
    }

    @Override
    public Node getIcon() {
        return new FontIcon(Material2MZ.PUBLIC);
    }

    @Override
    public String getCategory() {
        return "Network";
    }

    @Override
    public String getDescription() {
        return "Show public IP";
    }

    @Override
    public String getKeywords() {
        return "My IP Address, Network, plugin";
    }
}
