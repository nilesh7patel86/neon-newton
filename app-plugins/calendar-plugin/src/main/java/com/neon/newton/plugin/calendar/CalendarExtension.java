package com.neon.newton.plugin.calendar;

import com.neon.newton.api.ViewExtension;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.pf4j.Extension;

@Extension
public class CalendarExtension implements ViewExtension {
    @Override
    public Node getView() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 30;");
        Label title = new Label("Calendar");
        title.setStyle("-fx-font-size: 24px;");
        Node content = new DatePicker(java.time.LocalDate.now());
        root.getChildren().addAll(title, content);
        return root;
    }

    @Override
    public String getMenuTitle() {
        return "Calendar";
    }

    @Override
    public Node getIcon() {
        return new FontIcon(Material2AL.DATE_RANGE);
    }

    @Override
    public String getCategory() {
        return "Productivity";
    }

    @Override
    public String getDescription() {
        return "View dates";
    }

    @Override
    public String getKeywords() {
        return "Calendar, Productivity, plugin";
    }
}
