package com.ecommerce.ui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import com.ecommerce.models.Product;
import com.ecommerce.utils.ImageCacheManager;

/**
 * Product Card Component - For displaying products in grid
 */
public class ProductCard extends BorderPane {
    private Product product;

    public ProductCard(Product product) {
        this.product = product;
        createCard();
    }

    private void createCard() {
        // Card styling
        setStyle("-fx-border-color: #444444; -fx-border-width: 1; -fx-border-radius: 8; -fx-background-color: #2d2d2d; -fx-padding: 10;");
        setPrefSize(250, 350);

        // Image
        ImageView imageView = new ImageView();
        if (product.getMainImageUrl() != null && !product.getMainImageUrl().isEmpty()) {
            Image image = ImageCacheManager.getInstance().loadImageFromUrl(product.getMainImageUrl(), 230, 230);
            imageView.setImage(image);
        }
        imageView.setFitWidth(230);
        imageView.setFitHeight(230);
        imageView.setPreserveRatio(true);

        // Info section
        VBox infoBox = new VBox(8);
        infoBox.setPadding(new Insets(10, 0, 0, 0));

        // Product name
        Label nameLabel = new Label(product.getName());
        nameLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #ffffff; -fx-wrap-text: true;");
        nameLabel.setPrefWidth(230);

        // Price
        Label priceLabel = new Label(String.format("$%.2f", product.getFinalPrice()));
        priceLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #1e88e5;");

        // Rating
        Label ratingLabel = new Label(String.format("★ %.1f (%d)", product.getRating(), product.getReviewCount()));
        ratingLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #ffb300;");

        infoBox.getChildren().addAll(nameLabel, priceLabel, ratingLabel);

        setTop(imageView);
        setCenter(infoBox);

        // Hover effect
        setOnMouseEntered(e -> {
            setStyle("-fx-border-color: #1e88e5; -fx-border-width: 2; -fx-border-radius: 8; -fx-background-color: #353535; -fx-padding: 10;");
            setScaleX(1.05);
            setScaleY(1.05);
        });

        setOnMouseExited(e -> {
            setStyle("-fx-border-color: #444444; -fx-border-width: 1; -fx-border-radius: 8; -fx-background-color: #2d2d2d; -fx-padding: 10;");
            setScaleX(1);
            setScaleY(1);
        });
    }

    public Product getProduct() {
        return product;
    }
}
