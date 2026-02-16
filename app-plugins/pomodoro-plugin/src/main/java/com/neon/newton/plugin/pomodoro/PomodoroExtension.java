package com.neon.newton.plugin.pomodoro;

import com.neon.newton.api.ViewExtension;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2MZ;
import org.pf4j.Extension;

@Extension
public class PomodoroExtension implements ViewExtension {
    @Override
    public Node getView() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 30;");
        Label title = new Label("Focus Timer");
        title.setStyle("-fx-font-size: 24px;");
        Node content = new Button("Start Focus Session");
        root.getChildren().addAll(title, content);
        return root;
    }

    @Override
    public String getMenuTitle() {
        return "Focus Timer";
    }

    @Override
    public Node getIcon() {
        return new FontIcon(Material2MZ.TIMER);
    }

    @Override
    public String getCategory() {
        return "Productivity";
    }

    @Override
    public String getDescription() {
        return "25min timer";
    }

    @Override
    public String getKeywords() {
        return "Focus Timer, Productivity, plugin";
    }
}
