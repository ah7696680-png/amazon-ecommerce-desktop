package com.ecommerce.services;

import com.ecommerce.models.Order;
import com.ecommerce.models.OrderItem;
import com.ecommerce.models.Cart;
import com.ecommerce.database.DatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Order Service - Order creation, management, and tracking
 */
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private static OrderService instance;

    private OrderService() {}

    public static synchronized OrderService getInstance() {
        if (instance == null) {
            instance = new OrderService();
        }
        return instance;
    }

    /**
     * Create order from cart
     */
    public Order createOrder(int userId, Cart cart, String shippingAddress, Order.PaymentMethod paymentMethod) {
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Create order
                String orderNumber = generateOrderNumber();
                String orderQuery = "INSERT INTO orders (order_number, user_id, subtotal, tax, shipping_cost, discount_amount, total_amount, payment_method, order_status, shipping_address, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";
                PreparedStatement orderStmt = conn.prepareStatement(orderQuery, Statement.RETURN_GENERATED_KEYS);
                orderStmt.setString(1, orderNumber);
                orderStmt.setInt(2, userId);

                double subtotal = cart.getSubtotal();
                double tax = subtotal * 0.1; // 10% tax
                double shippingCost = 5.99;
                double totalAmount = subtotal + tax + shippingCost;

                orderStmt.setDouble(3, subtotal);
                orderStmt.setDouble(4, tax);
                orderStmt.setDouble(5, shippingCost);
                orderStmt.setDouble(6, 0); // discount
                orderStmt.setDouble(7, totalAmount);
                orderStmt.setString(8, paymentMethod.toString());
                orderStmt.setString(9, Order.OrderStatus.PENDING.toString());
                orderStmt.setString(10, shippingAddress);

                orderStmt.executeUpdate();
                ResultSet orderKeys = orderStmt.getGeneratedKeys();

                int orderId = 0;
                if (orderKeys.next()) {
                    orderId = orderKeys.getInt(1);
                }

                // Add order items
                String itemQuery = "INSERT INTO order_items (order_id, product_id, quantity, unit_price, total_price) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement itemStmt = conn.prepareStatement(itemQuery);

                for (CartItem item : cart.getItems()) {
                    itemStmt.setInt(1, orderId);
                    itemStmt.setInt(2, item.getProductId());
                    itemStmt.setInt(3, item.getQuantity());
                    itemStmt.setDouble(4, item.getPrice());
                    itemStmt.setDouble(5, item.getTotalPrice());
                    itemStmt.addBatch();
                }
                itemStmt.executeBatch();

                // Clear cart
                CartService.getInstance().clearCart(cart.getCartId());

                conn.commit();
                logger.info("Order created: " + orderNumber);

                Order order = new Order();
                order.setOrderId(orderId);
                order.setOrderNumber(orderNumber);
                order.setUserId(userId);
                return order;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            logger.error("Error creating order", e);
        }
        return null;
    }

    /**
     * Get user's orders
     */
    public List<Order> getUserOrders(int userId) {
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "SELECT * FROM orders WHERE user_id = ? ORDER BY created_at DESC";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                loadOrderItems(order);
                orders.add(order);
            }
        } catch (SQLException e) {
            logger.error("Error fetching user orders", e);
        }
        return orders;
    }

    /**
     * Get order by ID
     */
    public Order getOrderById(int orderId) {
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "SELECT * FROM orders WHERE order_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                loadOrderItems(order);
                return order;
            }
        } catch (SQLException e) {
            logger.error("Error fetching order", e);
        }
        return null;
    }

    /**
     * Update order status
     */
    public boolean updateOrderStatus(int orderId, Order.OrderStatus status) {
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "UPDATE orders SET order_status = ?, updated_at = NOW() WHERE order_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, status.toString());
            stmt.setInt(2, orderId);
            int result = stmt.executeUpdate();
            logger.info("Order status updated: " + orderId + " -> " + status);
            return result > 0;
        } catch (SQLException e) {
            logger.error("Error updating order status", e);
        }
        return false;
    }

    /**
     * Update payment status
     */
    public boolean updatePaymentStatus(int orderId, Order.PaymentStatus status) {
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "UPDATE orders SET payment_status = ?, updated_at = NOW() WHERE order_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, status.toString());
            stmt.setInt(2, orderId);
            int result = stmt.executeUpdate();
            logger.info("Payment status updated: " + orderId + " -> " + status);
            return result > 0;
        } catch (SQLException e) {
            logger.error("Error updating payment status", e);
        }
        return false;
    }

    // Private helper methods
    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private void loadOrderItems(Order order) {
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "SELECT * FROM order_items WHERE order_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, order.getOrderId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                OrderItem item = new OrderItem();
                item.setOrderItemId(rs.getInt("order_item_id"));
                item.setOrderId(rs.getInt("order_id"));
                item.setProductId(rs.getInt("product_id"));
                item.setQuantity(rs.getInt("quantity"));
                item.setUnitPrice(rs.getDouble("unit_price"));
                item.setTotalPrice(rs.getDouble("total_price"));
                order.addItem(item);
            }
        } catch (SQLException e) {
            logger.error("Error loading order items", e);
        }
    }

    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setOrderId(rs.getInt("order_id"));
        order.setOrderNumber(rs.getString("order_number"));
        order.setUserId(rs.getInt("user_id"));
        order.setSubtotal(rs.getDouble("subtotal"));
        order.setTax(rs.getDouble("tax"));
        order.setShippingCost(rs.getDouble("shipping_cost"));
        order.setDiscountAmount(rs.getDouble("discount_amount"));
        order.setTotalAmount(rs.getDouble("total_amount"));
        order.setCouponCode(rs.getString("coupon_code"));
        order.setPaymentMethod(Order.PaymentMethod.valueOf(rs.getString("payment_method")));
        order.setPaymentStatus(Order.PaymentStatus.valueOf(rs.getString("payment_status")));
        order.setOrderStatus(Order.OrderStatus.valueOf(rs.getString("order_status")));
        order.setShippingAddress(rs.getString("shipping_address"));
        order.setBillingAddress(rs.getString("billing_address"));
        order.setNotes(rs.getString("notes"));
        return order;
    }
}
