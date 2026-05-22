package com.ecommerce.services;

import com.ecommerce.models.Review;
import com.ecommerce.database.DatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Review Service - Product reviews and ratings
 */
public class ReviewService {
    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);
    private static ReviewService instance;

    private ReviewService() {}

    public static synchronized ReviewService getInstance() {
        if (instance == null) {
            instance = new ReviewService();
        }
        return instance;
    }

    /**
     * Get reviews for product
     */
    public List<Review> getProductReviews(int productId) {
        List<Review> reviews = new ArrayList<>();
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "SELECT * FROM reviews WHERE product_id = ? AND status = 'APPROVED' ORDER BY created_at DESC";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                reviews.add(mapResultSetToReview(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching product reviews", e);
        }
        return reviews;
    }

    /**
     * Create review
     */
    public boolean createReview(Review review) {
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "INSERT INTO reviews (product_id, user_id, order_id, rating, title, comment, is_verified_purchase) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, review.getProductId());
            stmt.setInt(2, review.getUserId());
            stmt.setInt(3, review.getOrderId());
            stmt.setInt(4, review.getRating());
            stmt.setString(5, review.getTitle());
            stmt.setString(6, review.getComment());
            stmt.setBoolean(7, review.isVerifiedPurchase());
            int result = stmt.executeUpdate();
            logger.info("Review created for product: " + review.getProductId());
            return result > 0;
        } catch (SQLException e) {
            logger.error("Error creating review", e);
        }
        return false;
    }

    /**
     * Approve review
     */
    public boolean approveReview(int reviewId) {
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "UPDATE reviews SET status = 'APPROVED', updated_at = NOW() WHERE review_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, reviewId);
            int result = stmt.executeUpdate();
            logger.info("Review approved: " + reviewId);
            return result > 0;
        } catch (SQLException e) {
            logger.error("Error approving review", e);
        }
        return false;
    }

    /**
     * Delete review
     */
    public boolean deleteReview(int reviewId) {
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "DELETE FROM reviews WHERE review_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, reviewId);
            int result = stmt.executeUpdate();
            logger.info("Review deleted: " + reviewId);
            return result > 0;
        } catch (SQLException e) {
            logger.error("Error deleting review", e);
        }
        return false;
    }

    /**
     * Get average rating for product
     */
    public double getAverageRating(int productId) {
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "SELECT AVG(rating) FROM reviews WHERE product_id = ? AND status = 'APPROVED'";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            logger.error("Error fetching average rating", e);
        }
        return 0;
    }

    private Review mapResultSetToReview(ResultSet rs) throws SQLException {
        Review review = new Review();
        review.setReviewId(rs.getInt("review_id"));
        review.setProductId(rs.getInt("product_id"));
        review.setUserId(rs.getInt("user_id"));
        review.setOrderId(rs.getInt("order_id"));
        review.setRating(rs.getInt("rating"));
        review.setTitle(rs.getString("title"));
        review.setComment(rs.getString("comment"));
        review.setVerifiedPurchase(rs.getBoolean("is_verified_purchase"));
        return review;
    }
}
