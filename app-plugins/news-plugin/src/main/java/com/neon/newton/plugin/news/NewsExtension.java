package com.neon.newton.plugin.news;

import com.neon.newton.api.ViewExtension;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2MZ;
import org.pf4j.Extension;

@Extension
public class NewsExtension implements ViewExtension {
    @Override
    public Node getView() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 30;");
        Label title = new Label("Tech News");
        title.setStyle("-fx-font-size: 24px;");
        Node content = new ListView<>(javafx.collections.FXCollections.observableArrayList("Java 25 Released", "AI takes over coding"));
        root.getChildren().addAll(title, content);
        return root;
    }

    @Override
    public String getMenuTitle() {
        return "Tech News";
    }

    @Override
    public Node getIcon() {
        return new FontIcon(Material2MZ.RSS_FEED);
    }

    @Override
    public String getCategory() {
        return "News";
    }

    @Override
    public String getDescription() {
        return "Latest headlines";
    }

    @Override
    public String getKeywords() {
        return "Tech News, News, plugin";
    }
}
