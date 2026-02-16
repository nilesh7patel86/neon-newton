package com.neon.newton.plugin.text;

import com.neon.newton.api.ViewExtension;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2MZ;
import org.pf4j.Extension;

@Extension
public class TextViewExtension implements ViewExtension {

    @Override
    public Node getView() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.TOP_LEFT);
        root.setStyle("-fx-padding: 30;");

        Label title = new Label("Quick Notes");
        title.setStyle("-fx-font-size: 24px; -fx-text-fill: -color-fg-default;");

        TextArea textArea = new TextArea();
        textArea.setPromptText("Type your notes here...");
        textArea.setStyle("-fx-font-family: monospace;");
        VBox.setVgrow(textArea, Priority.ALWAYS);

        root.getChildren().addAll(title, textArea);
        return root;
    }

    @Override
    public String getMenuTitle() {
        return "Quick Notes";
    }
    
    @Override
    public Node getIcon() {
        return new FontIcon(Material2MZ.NOTE_ADD);
    }
    
    @Override
    public String getCategory() {
        return "Productivity";
    }

    @Override
    public String getDescription() {
        return "Simple text scratchpad for notes";
    }

    @Override
    public String getKeywords() {
        return "text, edit, note, scratchpad, write";
    }
}
