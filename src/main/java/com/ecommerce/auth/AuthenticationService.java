package com.ecommerce.auth;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.ecommerce.models.User;
import com.ecommerce.database.DatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Authentication Service - User login, registration, password management
 */
public class AuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private static AuthenticationService instance;
    private User currentUser;
    private String sessionToken;

    private AuthenticationService() {}

    public static synchronized AuthenticationService getInstance() {
        if (instance == null) {
            instance = new AuthenticationService();
        }
        return instance;
    }

    /**
     * Authenticate user with email and password
     */
    public User authenticate(String email, String password) {
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "SELECT * FROM users WHERE email = ? AND is_active = true";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String passwordHash = rs.getString("password_hash");
                if (BCrypt.verifyer().verify(password.toCharArray(), passwordHash).verified) {
                    User user = mapResultSetToUser(rs);
                    this.currentUser = user;
                    this.sessionToken = generateSessionToken();
                    updateLastLogin(user.getUserId());
                    logger.info("User authenticated: " + email);
                    return user;
                }
            }
        } catch (SQLException e) {
            logger.error("Authentication error", e);
        }
        return null;
    }

    /**
     * Register new user
     */
    public boolean register(User user, String password) {
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            // Check if user exists
            if (userExists(user.getEmail())) {
                logger.warn("User already exists: " + user.getEmail());
                return false;
            }

            String passwordHash = BCrypt.withDefaults().hashToString(12, password.toCharArray());
            String query = "INSERT INTO users (email, username, password_hash, first_name, last_name, role, is_active, created_at) VALUES (?, ?, ?, ?, ?, ?, true, NOW())";
            PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, passwordHash);
            stmt.setString(4, user.getFirstName());
            stmt.setString(5, user.getLastName());
            stmt.setString(6, user.getRole().toString());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    user.setUserId(generatedKeys.getInt(1));
                }
                logger.info("User registered successfully: " + user.getEmail());
                return true;
            }
        } catch (SQLException e) {
            logger.error("Registration error", e);
        }
        return false;
    }

    /**
     * Change user password
     */
    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            // Verify old password
            String query = "SELECT password_hash FROM users WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String passwordHash = rs.getString("password_hash");
                if (!BCrypt.verifyer().verify(oldPassword.toCharArray(), passwordHash).verified) {
                    logger.warn("Old password verification failed for user: " + userId);
                    return false;
                }
            }

            // Update password
            String newHash = BCrypt.withDefaults().hashToString(12, newPassword.toCharArray());
            String updateQuery = "UPDATE users SET password_hash = ?, updated_at = NOW() WHERE user_id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
            updateStmt.setString(1, newHash);
            updateStmt.setInt(2, userId);
            updateStmt.executeUpdate();

            logger.info("Password changed for user: " + userId);
            return true;
        } catch (SQLException e) {
            logger.error("Password change error", e);
        }
        return false;
    }

    /**
     * Reset password with token
     */
    public boolean resetPassword(String email, String newPassword) {
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String newHash = BCrypt.withDefaults().hashToString(12, newPassword.toCharArray());
            String query = "UPDATE users SET password_hash = ?, updated_at = NOW() WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, newHash);
            stmt.setString(2, email);
            int result = stmt.executeUpdate();

            logger.info("Password reset for user: " + email);
            return result > 0;
        } catch (SQLException e) {
            logger.error("Password reset error", e);
        }
        return false;
    }

    /**
     * Logout current user
     */
    public void logout() {
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            if (currentUser != null) {
                String query = "DELETE FROM sessions WHERE user_id = ? AND token = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, currentUser.getUserId());
                stmt.setString(2, sessionToken);
                stmt.executeUpdate();
            }
            currentUser = null;
            sessionToken = null;
            logger.info("User logged out");
        } catch (SQLException e) {
            logger.error("Logout error", e);
        }
    }

    /**
     * Get current authenticated user
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Get current session token
     */
    public String getSessionToken() {
        return sessionToken;
    }

    /**
     * Check if user is authenticated
     */
    public boolean isAuthenticated() {
        return currentUser != null && sessionToken != null;
    }

    // Private helper methods
    private boolean userExists(String email) throws SQLException {
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "SELECT COUNT(*) FROM users WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    private String generateSessionToken() {
        return UUID.randomUUID().toString();
    }

    private void updateLastLogin(int userId) {
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "UPDATE users SET last_login = NOW() WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error updating last login", e);
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setPhone(rs.getString("phone"));
        user.setRole(User.UserRole.valueOf(rs.getString("role")));
        user.setAvatarUrl(rs.getString("avatar_url"));
        user.setActive(rs.getBoolean("is_active"));
        user.setVerified(rs.getBoolean("is_verified"));
        return user;
    }
}
