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
public class MonitorViewExtension implements ViewExtension {

    @Override
    public Node getView() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 40;");

        Label title = new Label("System Monitor");
        title.setStyle("-fx-font-size: 24px; -fx-text-fill: -color-fg-default;");

        Label cpuLabel = new Label("CPU Usage: 15%");
        cpuLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: -color-fg-default;");

        Label ramLabel = new Label("Memory Usage: 45%");
        ramLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: -color-fg-default;");
        
        Label note = new Label("(Mock Data)");
        note.setStyle("-fx-text-fill: -color-fg-muted;");

        root.getChildren().addAll(title, cpuLabel, ramLabel, note);
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
        return "Shows system CPU and Memory usage";
    }

    @Override
    public String getKeywords() {
        return "cpu, memory, system, monitor, performance";
    }
}
