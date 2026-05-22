package com.ecommerce.services;

import com.ecommerce.database.DatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Notification Service - User notifications and alerts
 */
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private static NotificationService instance;

    private NotificationService() {}

    public static synchronized NotificationService getInstance() {
        if (instance == null) {
            instance = new NotificationService();
        }
        return instance;
    }

    /**
     * Send notification to user
     */
    public boolean sendNotification(int userId, String title, String message, String notificationType, Integer relatedId) {
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "INSERT INTO notifications (user_id, title, message, notification_type, related_id, is_read) VALUES (?, ?, ?, ?, ?, false)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            stmt.setString(2, title);
            stmt.setString(3, message);
            stmt.setString(4, notificationType);
            if (relatedId != null) {
                stmt.setInt(5, relatedId);
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }
            int result = stmt.executeUpdate();
            logger.info("Notification sent to user: " + userId);
            return result > 0;
        } catch (SQLException e) {
            logger.error("Error sending notification", e);
        }
        return false;
    }

    /**
     * Get user notifications
     */
    public List<java.util.Map<String, Object>> getUserNotifications(int userId) {
        List<java.util.Map<String, Object>> notifications = new ArrayList<>();
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC LIMIT 50";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                java.util.Map<String, Object> notif = new java.util.HashMap<>();
                notif.put("notificationId", rs.getInt("notification_id"));
                notif.put("title", rs.getString("title"));
                notif.put("message", rs.getString("message"));
                notif.put("type", rs.getString("notification_type"));
                notif.put("isRead", rs.getBoolean("is_read"));
                notif.put("createdAt", rs.getTimestamp("created_at"));
                notifications.add(notif);
            }
        } catch (SQLException e) {
            logger.error("Error fetching user notifications", e);
        }
        return notifications;
    }

    /**
     * Mark notification as read
     */
    public boolean markAsRead(int notificationId) {
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "UPDATE notifications SET is_read = true, read_at = NOW() WHERE notification_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, notificationId);
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            logger.error("Error marking notification as read", e);
        }
        return false;
    }

    /**
     * Get unread notification count
     */
    public int getUnreadCount(int userId) {
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND is_read = false";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error fetching unread count", e);
        }
        return 0;
    }
}
