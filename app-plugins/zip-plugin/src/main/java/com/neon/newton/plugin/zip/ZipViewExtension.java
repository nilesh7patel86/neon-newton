package com.neon.newton.plugin.zip;

import com.neon.newton.api.ViewExtension;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.StringUtils;
import org.pf4j.Extension;

@Extension
public class ZipViewExtension implements ViewExtension {
    @Override
    public String getMenuTitle() {
        return "ZIP Logic";
    }

    @Override
    public Node getView() {
        VBox container = new VBox(15);
        
        Label title = new Label("ZIP Structure Logic");
        title.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");
        
        // Use Commons Lang to manipulate some text
        String rawText = "   this text was cleaned using apache commons lang   ";
        String cleanedText = StringUtils.capitalize(StringUtils.trim(rawText));
        
        Label info = new Label("Cleaned with Commons Lang (in lib/):");
        info.setStyle("-fx-text-fill: #888;");
        
        Label result = new Label(cleanedText);
        result.setStyle("-fx-font-size: 16px; -fx-text-fill: #ff9900; -fx-font-style: italic;");

        Label structureDesc = new Label("This plugin is loaded from a ZIP file containing:\n/classes - internal classes\n/lib - external dependencies");
        structureDesc.setStyle("-fx-text-fill: #aaa; -fx-padding: 20 0 0 0;");
        
        container.getChildren().addAll(title, info, result, structureDesc);
        return container;
    }

    @Override
    public String getIconPath() {
        return null;
    }
}
