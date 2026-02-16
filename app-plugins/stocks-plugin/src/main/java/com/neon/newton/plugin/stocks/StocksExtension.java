package com.neon.newton.plugin.stocks;

import com.neon.newton.api.ViewExtension;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2MZ;
import org.pf4j.Extension;

@Extension
public class StocksExtension implements ViewExtension {
    @Override
    public Node getView() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 30;");
        Label title = new Label("Stock Ticker");
        title.setStyle("-fx-font-size: 24px;");
        Node content = new Label("AAPL: $150.25 (+1.2%)");
        root.getChildren().addAll(title, content);
        return root;
    }

    @Override
    public String getMenuTitle() {
        return "Stock Ticker";
    }

    @Override
    public Node getIcon() {
        return new FontIcon(Material2MZ.SHOW_CHART);
    }

    @Override
    public String getCategory() {
        return "Finance";
    }

    @Override
    public String getDescription() {
        return "Live market data";
    }

    @Override
    public String getKeywords() {
        return "Stock Ticker, Finance, plugin";
    }
}
