package com.neon.newton.plugin.translator;

import com.neon.newton.api.ViewExtension;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2MZ;
import org.pf4j.Extension;

@Extension
public class TranslatorExtension implements ViewExtension {
    @Override
    public Node getView() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 30;");
        Label title = new Label("Translator");
        title.setStyle("-fx-font-size: 24px;");
        Node content = new Label("Hello -> Hola");
        root.getChildren().addAll(title, content);
        return root;
    }

    @Override
    public String getMenuTitle() {
        return "Translator";
    }

    @Override
    public Node getIcon() {
        return new FontIcon(Material2MZ.TRANSLATE);
    }

    @Override
    public String getCategory() {
        return "Utilities";
    }

    @Override
    public String getDescription() {
        return "Simple translations";
    }

    @Override
    public String getKeywords() {
        return "Translator, Utilities, plugin";
    }
}
