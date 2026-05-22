package com.ecommerce.admin;

import com.ecommerce.database.DatabaseService;
import com.ecommerce.models.User;
import com.ecommerce.models.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Admin Service - Administrative operations and management
 */
public class AdminService {
    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);
    private static AdminService instance;

    private AdminService() {}

    public static synchronized AdminService getInstance() {
        if (instance == null) {
            instance = new AdminService();
        }
        return instance;
    }

    /**
     * Get all users
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "SELECT * FROM users ORDER BY created_at DESC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching all users", e);
        }
        return users;
    }

    /**
     * Get users by role
     */
    public List<User> getUsersByRole(User.UserRole role) {
        List<User> users = new ArrayList<>();
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "SELECT * FROM users WHERE role = ? ORDER BY created_at DESC";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, role.toString());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching users by role", e);
        }
        return users;
    }

    /**
     * Suspend user
     */
    public boolean suspendUser(int userId) {
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "UPDATE users SET is_active = false, updated_at = NOW() WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            int result = stmt.executeUpdate();
            logger.info("User suspended: " + userId);
            return result > 0;
        } catch (SQLException e) {
            logger.error("Error suspending user", e);
        }
        return false;
    }

    /**
     * Activate user
     */
    public boolean activateUser(int userId) {
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "UPDATE users SET is_active = true, updated_at = NOW() WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            int result = stmt.executeUpdate();
            logger.info("User activated: " + userId);
            return result > 0;
        } catch (SQLException e) {
            logger.error("Error activating user", e);
        }
        return false;
    }

    /**
     * Get all products
     */
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "SELECT * FROM products ORDER BY created_at DESC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching all products", e);
        }
        return products;
    }

    /**
     * Verify product
     */
    public boolean verifyProduct(int productId) {
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "UPDATE products SET is_verified = true, updated_at = NOW() WHERE product_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, productId);
            int result = stmt.executeUpdate();
            logger.info("Product verified: " + productId);
            return result > 0;
        } catch (SQLException e) {
            logger.error("Error verifying product", e);
        }
        return false;
    }

    /**
     * Delete product
     */
    public boolean deleteProduct(int productId) {
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "DELETE FROM products WHERE product_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, productId);
            int result = stmt.executeUpdate();
            logger.info("Product deleted: " + productId);
            return result > 0;
        } catch (SQLException e) {
            logger.error("Error deleting product", e);
        }
        return false;
    }

    /**
     * Log admin activity
     */
    public boolean logActivity(int adminId, String action, String entityType, Integer entityId, String description) {
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "INSERT INTO admin_activities (admin_id, action, entity_type, entity_id, description) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, adminId);
            stmt.setString(2, action);
            stmt.setString(3, entityType);
            if (entityId != null) {
                stmt.setInt(4, entityId);
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }
            stmt.setString(5, description);
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            logger.error("Error logging admin activity", e);
        }
        return false;
    }

    /**
     * Get system statistics
     */
    public java.util.Map<String, Object> getSystemStatistics() {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            // Total users
            Statement stmt1 = conn.createStatement();
            ResultSet rs1 = stmt1.executeQuery("SELECT COUNT(*) FROM users");
            if (rs1.next()) stats.put("totalUsers", rs1.getInt(1));

            // Total products
            Statement stmt2 = conn.createStatement();
            ResultSet rs2 = stmt2.executeQuery("SELECT COUNT(*) FROM products");
            if (rs2.next()) stats.put("totalProducts", rs2.getInt(1));

            // Total orders
            Statement stmt3 = conn.createStatement();
            ResultSet rs3 = stmt3.executeQuery("SELECT COUNT(*) FROM orders");
            if (rs3.next()) stats.put("totalOrders", rs3.getInt(1));

            // Total revenue
            Statement stmt4 = conn.createStatement();
            ResultSet rs4 = stmt4.executeQuery("SELECT SUM(total_amount) FROM orders WHERE payment_status = 'COMPLETED'");
            if (rs4.next()) stats.put("totalRevenue", rs4.getDouble(1));

        } catch (SQLException e) {
            logger.error("Error fetching system statistics", e);
        }
        return stats;
    }

    /**
     * Backup database
     */
    public boolean backupDatabase(String backupPath) {
        try {
            String command = "mysqldump -u root -proot ecommerce > " + backupPath;
            Process process = Runtime.getRuntime().exec(command);
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                logger.info("Database backup created: " + backupPath);
                return true;
            }
        } catch (Exception e) {
            logger.error("Error backing up database", e);
        }
        return false;
    }

    /**
     * Restore database
     */
    public boolean restoreDatabase(String backupPath) {
        try {
            String command = "mysql -u root -proot ecommerce < " + backupPath;
            Process process = Runtime.getRuntime().exec(command);
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                logger.info("Database restored from: " + backupPath);
                return true;
            }
        } catch (Exception e) {
            logger.error("Error restoring database", e);
        }
        return false;
    }

    // Private helper methods
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setPhone(rs.getString("phone"));
        user.setRole(User.UserRole.valueOf(rs.getString("role")));
        user.setActive(rs.getBoolean("is_active"));
        user.setVerified(rs.getBoolean("is_verified"));
        return user;
    }

    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setProductId(rs.getInt("product_id"));
        product.setSellerId(rs.getInt("seller_id"));
        product.setCategoryId(rs.getInt("category_id"));
        product.setName(rs.getString("name"));
        product.setPrice(rs.getDouble("price"));
        product.setFinalPrice(rs.getDouble("final_price"));
        product.setQuantityInStock(rs.getInt("quantity_in_stock"));
        product.setStatus(Product.ProductStatus.valueOf(rs.getString("status")));
        product.setFeatured(rs.getBoolean("is_featured"));
        product.setVerified(rs.getBoolean("is_verified"));
        return product;
    }
}
