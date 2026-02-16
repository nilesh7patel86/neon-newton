package com.neon.newton.plugin.dictionary;

import com.neon.newton.api.ViewExtension;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2MZ;
import org.pf4j.Extension;

@Extension
public class DictionaryExtension implements ViewExtension {
    @Override
    public Node getView() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 30;");
        Label title = new Label("Dictionary");
        title.setStyle("-fx-font-size: 24px;");
        Node content = new Label("Serendipity: The occurrence of events by chance in a happy way.");
        root.getChildren().addAll(title, content);
        return root;
    }

    @Override
    public String getMenuTitle() {
        return "Dictionary";
    }

    @Override
    public Node getIcon() {
        return new FontIcon(Material2MZ.MENU_BOOK);
    }

    @Override
    public String getCategory() {
        return "Education";
    }

    @Override
    public String getDescription() {
        return "Word of the Day";
    }

    @Override
    public String getKeywords() {
        return "Dictionary, Education, plugin";
    }
}
