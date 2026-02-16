package com.neon.newton.plugin.complex;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.neon.newton.api.ViewExtension;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.pf4j.Extension;

import java.util.Map;

@Extension
public class ComplexViewExtension implements ViewExtension {
    @Override
    public String getMenuTitle() {
        return "JSON Viewer";
    }

    @Override
    public Node getView() {
        VBox container = new VBox(15);

        Label title = new Label("JSON Data (via Gson)");
        title.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");

        // Use Gson to format some data
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Map<String, String> data = Map.of(
                "status", "active",
                "library", "Gson",
                "version", "2.10.1",
                "plugin", "Complex Plugin");
        String json = gson.toJson(data);

        TextArea textArea = new TextArea(json);
        textArea.setEditable(false);
        textArea.setStyle(
                "-fx-control-inner-background: #1e1e1e; -fx-text-fill: #00ffcc; -fx-font-family: 'Consolas';");
        textArea.setPrefHeight(300);

        container.getChildren().addAll(title, textArea);
        return container;
    }

    @Override
    public Node getIcon() {
        return FontIcon.of(Material2AL.EXTENSION);
    }

    @Override
    public String getCategory() {
        return "Development";
    }

    @Override
    public String getDescription() {
        return "JSON viewer using Gson library";
    }

    @Override
    public String getKeywords() {
        return "json,gson,viewer,data,development";
    }
}
