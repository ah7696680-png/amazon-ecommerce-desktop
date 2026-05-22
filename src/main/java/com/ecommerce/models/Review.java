package com.ecommerce.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Review Model for product reviews and ratings
 */
public class Review {
    private int reviewId;
    private int productId;
    private int userId;
    private int orderId;
    private int rating;
    private String title;
    private String comment;
    private int helpfulCount;
    private int unhelpfulCount;
    private boolean verifiedPurchase;
    private ReviewStatus status;
    private List<String> imageUrls;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum ReviewStatus {
        PENDING, APPROVED, REJECTED
    }

    // Constructors
    public Review() {
        this.imageUrls = new ArrayList<>();
        this.status = ReviewStatus.PENDING;
        this.verifiedPurchase = false;
    }

    public Review(int productId, int userId, int rating) {
        this();
        this.productId = productId;
        this.userId = userId;
        this.rating = rating;
    }

    // Getters and Setters
    public int getReviewId() { return reviewId; }
    public void setReviewId(int reviewId) { this.reviewId = reviewId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = Math.max(1, Math.min(5, rating)); }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public int getHelpfulCount() { return helpfulCount; }
    public void setHelpfulCount(int helpfulCount) { this.helpfulCount = helpfulCount; }

    public int getUnhelpfulCount() { return unhelpfulCount; }
    public void setUnhelpfulCount(int unhelpfulCount) { this.unhelpfulCount = unhelpfulCount; }

    public boolean isVerifiedPurchase() { return verifiedPurchase; }
    public void setVerifiedPurchase(boolean verifiedPurchase) { this.verifiedPurchase = verifiedPurchase; }

    public ReviewStatus getStatus() { return status; }
    public void setStatus(ReviewStatus status) { this.status = status; }

    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Review{" +
                "reviewId=" + reviewId +
                ", productId=" + productId +
                ", rating=" + rating +
                ", status=" + status +
                '}';
    }
}
