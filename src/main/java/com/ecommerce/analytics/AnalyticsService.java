package com.ecommerce.analytics;

import com.ecommerce.database.DatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.time.LocalDate;

/**
 * Analytics Service - Sales metrics and reporting
 */
public class AnalyticsService {
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsService.class);
    private static AnalyticsService instance;

    private AnalyticsService() {}

    public static synchronized AnalyticsService getInstance() {
        if (instance == null) {
            instance = new AnalyticsService();
        }
        return instance;
    }

    /**
     * Get total revenue
     */
    public double getTotalRevenue() {
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "SELECT SUM(total_amount) FROM orders WHERE payment_status = 'COMPLETED'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            logger.error("Error fetching total revenue", e);
        }
        return 0;
    }

    /**
     * Get total orders
     */
    public int getTotalOrders() {
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "SELECT COUNT(*) FROM orders";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error fetching total orders", e);
        }
        return 0;
    }

    /**
     * Get total users
     */
    public int getTotalUsers() {
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "SELECT COUNT(*) FROM users WHERE role = 'CUSTOMER'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error fetching total users", e);
        }
        return 0;
    }

    /**
     * Get total products
     */
    public int getTotalProducts() {
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "SELECT COUNT(*) FROM products WHERE status = 'ACTIVE'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error fetching total products", e);
        }
        return 0;
    }

    /**
     * Get average order value
     */
    public double getAverageOrderValue() {
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "SELECT AVG(total_amount) FROM orders WHERE payment_status = 'COMPLETED'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            logger.error("Error fetching average order value", e);
        }
        return 0;
    }

    /**
     * Get daily revenue for chart
     */
    public java.util.List<Double> getDailyRevenue(int days) {
        java.util.List<Double> revenues = new java.util.ArrayList<>();
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "SELECT SUM(total_amount) FROM orders WHERE payment_status = 'COMPLETED' AND created_at >= DATE_SUB(NOW(), INTERVAL ? DAY) GROUP BY DATE(created_at) ORDER BY created_at";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, days);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                revenues.add(rs.getDouble(1));
            }
        } catch (SQLException e) {
            logger.error("Error fetching daily revenue", e);
        }
        return revenues;
    }

    /**
     * Get top products
     */
    public java.util.List<String> getTopProducts(int limit) {
        java.util.List<String> products = new java.util.ArrayList<>();
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "SELECT p.name FROM products p LEFT JOIN order_items oi ON p.product_id = oi.product_id GROUP BY p.product_id ORDER BY SUM(oi.quantity) DESC LIMIT ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                products.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            logger.error("Error fetching top products", e);
        }
        return products;
    }

    /**
     * Get conversion rate
     */
    public double getConversionRate() {
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "SELECT (COUNT(DISTINCT user_id) FROM orders WHERE payment_status = 'COMPLETED') / COUNT(DISTINCT user_id) FROM users WHERE role = 'CUSTOMER'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                return rs.getDouble(1) * 100;
            }
        } catch (SQLException e) {
            logger.error("Error fetching conversion rate", e);
        }
        return 0;
    }

    /**
     * Log analytics event
     */
    public boolean logAnalytics(LocalDate date, int totalUsers, int totalOrders, double totalRevenue, int totalProducts, int totalSellers) {
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "INSERT INTO analytics (date_recorded, total_users, total_orders, total_revenue, total_products, total_sellers) VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE total_users = ?, total_orders = ?, total_revenue = ?, total_products = ?, total_sellers = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setDate(1, java.sql.Date.valueOf(date));
            stmt.setInt(2, totalUsers);
            stmt.setInt(3, totalOrders);
            stmt.setDouble(4, totalRevenue);
            stmt.setInt(5, totalProducts);
            stmt.setInt(6, totalSellers);
            stmt.setInt(7, totalUsers);
            stmt.setInt(8, totalOrders);
            stmt.setDouble(9, totalRevenue);
            stmt.setInt(10, totalProducts);
            stmt.setInt(11, totalSellers);
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            logger.error("Error logging analytics", e);
        }
        return false;
    }
}
