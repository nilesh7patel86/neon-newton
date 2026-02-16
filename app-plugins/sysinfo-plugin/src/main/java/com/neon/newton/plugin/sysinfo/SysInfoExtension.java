package com.neon.newton.plugin.sysinfo;

import com.neon.newton.api.ViewExtension;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.pf4j.Extension;

@Extension
public class SysInfoExtension implements ViewExtension {
    @Override
    public Node getView() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 30;");
        Label title = new Label("System Info");
        title.setStyle("-fx-font-size: 24px;");
        Node content = new Label(System.getProperty("os.name") + " - Java " + System.getProperty("java.version"));
        root.getChildren().addAll(title, content);
        return root;
    }

    @Override
    public String getMenuTitle() {
        return "System Info";
    }

    @Override
    public Node getIcon() {
        return new FontIcon(Material2AL.INFO);
    }

    @Override
    public String getCategory() {
        return "Utilities";
    }

    @Override
    public String getDescription() {
        return "OS and Java version";
    }

    @Override
    public String getKeywords() {
        return "System Info, Utilities, plugin";
    }
}
