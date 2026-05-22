package com.ecommerce.ui.components;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import com.ecommerce.models.Order;

/**
 * Order Card Component - For displaying orders
 */
public class OrderCard extends VBox {
    private Order order;

    public OrderCard(Order order) {
        this.order = order;
        createCard();
    }

    private void createCard() {
        setPrefWidth(400);
        setPadding(new Insets(15));
        setStyle("-fx-border-color: #444444; -fx-border-width: 1; -fx-border-radius: 8; -fx-background-color: #2d2d2d;");
        setSpacing(10);

        // Order number
        Label orderNumLabel = new Label("Order #" + order.getOrderNumber());
        orderNumLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #ffffff;");

        // Order status
        Label statusLabel = new Label("Status: " + order.getOrderStatus());
        statusLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #1e88e5;");

        // Total amount
        Label totalLabel = new Label(String.format("Total: $%.2f", order.getTotalAmount()));
        totalLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #ffb300; -fx-font-weight: bold;");

        // Created date
        Label dateLabel = new Label("Ordered: " + order.getCreatedAt());
        dateLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #666666;");

        // Action buttons
        HBox actionBox = new HBox(10);
        Button viewBtn = new Button("View Details");
        viewBtn.setStyle("-fx-padding: 8 15; -fx-background-color: #1e88e5; -fx-text-fill: #ffffff; -fx-font-size: 11;");
        Button trackBtn = new Button("Track");
        trackBtn.setStyle("-fx-padding: 8 15; -fx-background-color: #4caf50; -fx-text-fill: #ffffff; -fx-font-size: 11;");
        actionBox.getChildren().addAll(viewBtn, trackBtn);

        getChildren().addAll(orderNumLabel, statusLabel, totalLabel, dateLabel, actionBox);
    }

    public Order getOrder() {
        return order;
    }
}
