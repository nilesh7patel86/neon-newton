package com.neon.newton.plugin.todo;

import com.neon.newton.api.ViewExtension;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.pf4j.Extension;

@Extension
public class TodoExtension implements ViewExtension {
    @Override
    public Node getView() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 30;");
        Label title = new Label("Todo List");
        title.setStyle("-fx-font-size: 24px;");
        Node content = new ListView<String>(javafx.collections.FXCollections.observableArrayList("Buy Milk", "Walk Dog"));
        root.getChildren().addAll(title, content);
        return root;
    }

    @Override
    public String getMenuTitle() {
        return "Todo List";
    }

    @Override
    public Node getIcon() {
        return new FontIcon(Material2AL.CHECK_BOX);
    }

    @Override
    public String getCategory() {
        return "Productivity";
    }

    @Override
    public String getDescription() {
        return "Track tasks";
    }

    @Override
    public String getKeywords() {
        return "Todo List, Productivity, plugin";
    }
}
