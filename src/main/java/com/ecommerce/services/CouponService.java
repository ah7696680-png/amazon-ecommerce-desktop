package com.ecommerce.services;

import com.ecommerce.database.DatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Coupon Service - Coupon and discount management
 */
public class CouponService {
    private static final Logger logger = LoggerFactory.getLogger(CouponService.class);
    private static CouponService instance;

    private CouponService() {}

    public static synchronized CouponService getInstance() {
        if (instance == null) {
            instance = new CouponService();
        }
        return instance;
    }

    /**
     * Validate coupon
     */
    public java.util.Map<String, Object> validateCoupon(String code, double cartTotal) {
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("valid", false);
        result.put("discount", 0.0);

        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "SELECT * FROM coupons WHERE code = ? AND is_active = true AND valid_from <= NOW() AND valid_until >= NOW()";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Check usage limit
                int usageLimit = rs.getInt("usage_limit");
                int usedCount = rs.getInt("used_count");
                if (usedCount >= usageLimit && usageLimit > 0) {
                    result.put("message", "Coupon usage limit exceeded");
                    return result;
                }

                // Check minimum purchase
                double minPurchase = rs.getDouble("min_purchase_amount");
                if (cartTotal < minPurchase) {
                    result.put("message", "Minimum purchase amount not met");
                    return result;
                }

                // Calculate discount
                double discount = 0;
                String discountType = rs.getString("discount_type");
                double discountValue = rs.getDouble("discount_value");

                if ("PERCENTAGE".equals(discountType)) {
                    discount = cartTotal * (discountValue / 100);
                } else if ("FIXED_AMOUNT".equals(discountType)) {
                    discount = discountValue;
                }

                // Apply max discount limit
                double maxDiscount = rs.getDouble("max_discount_amount");
                if (maxDiscount > 0 && discount > maxDiscount) {
                    discount = maxDiscount;
                }

                result.put("valid", true);
                result.put("discount", discount);
                result.put("couponId", rs.getInt("coupon_id"));
                result.put("message", "Coupon applied successfully");
            } else {
                result.put("message", "Invalid or expired coupon");
            }
        } catch (SQLException e) {
            logger.error("Error validating coupon", e);
            result.put("message", "Error validating coupon");
        }
        return result;
    }

    /**
     * Create coupon
     */
    public boolean createCoupon(String code, String discountType, double discountValue, double minPurchase, double maxDiscount, int usageLimit) {
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "INSERT INTO coupons (code, discount_type, discount_value, min_purchase_amount, max_discount_amount, usage_limit, is_active, valid_from, valid_until) VALUES (?, ?, ?, ?, ?, ?, true, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY))";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, code.toUpperCase());
            stmt.setString(2, discountType);
            stmt.setDouble(3, discountValue);
            stmt.setDouble(4, minPurchase);
            stmt.setDouble(5, maxDiscount);
            stmt.setInt(6, usageLimit);
            int result = stmt.executeUpdate();
            logger.info("Coupon created: " + code);
            return result > 0;
        } catch (SQLException e) {
            logger.error("Error creating coupon", e);
        }
        return false;
    }

    /**
     * Increment coupon usage
     */
    public boolean incrementCouponUsage(int couponId) {
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "UPDATE coupons SET used_count = used_count + 1 WHERE coupon_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, couponId);
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            logger.error("Error incrementing coupon usage", e);
        }
        return false;
    }

    /**
     * Deactivate coupon
     */
    public boolean deactivateCoupon(int couponId) {
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "UPDATE coupons SET is_active = false WHERE coupon_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, couponId);
            int result = stmt.executeUpdate();
            logger.info("Coupon deactivated: " + couponId);
            return result > 0;
        } catch (SQLException e) {
            logger.error("Error deactivating coupon", e);
        }
        return false;
    }
}
