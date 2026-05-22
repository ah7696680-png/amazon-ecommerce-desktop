package com.ecommerce.ui.components;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Dashboard Widget Component - For analytics and stats
 */
public class DashboardWidget extends VBox {
    private String title;
    private String value;
    private String subtitle;
    private Color color;

    public DashboardWidget(String title, String value, String subtitle, Color color) {
        this.title = title;
        this.value = value;
        this.subtitle = subtitle;
        this.color = color;
        createWidget();
    }

    private void createWidget() {
        setPrefSize(250, 150);
        setPadding(new Insets(20));
        setStyle("-fx-border-color: #444444; -fx-border-width: 1; -fx-border-radius: 8; -fx-background-color: #2d2d2d;");
        setSpacing(10);

        // Color indicator
        Rectangle colorBar = new Rectangle(10, 50);
        colorBar.setFill(color);
        colorBar.setArcWidth(5);
        colorBar.setArcHeight(5);

        // Title
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #999999; -fx-font-weight: bold;");

        // Value
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #ffffff;");

        // Subtitle
        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #666666;");

        HBox topBox = new HBox(10);
        topBox.getChildren().addAll(colorBar, titleLabel);

        getChildren().addAll(topBox, valueLabel, subtitleLabel);
    }
}
