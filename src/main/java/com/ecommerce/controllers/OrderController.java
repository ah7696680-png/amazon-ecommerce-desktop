package com.ecommerce.controllers;

import com.ecommerce.models.Order;
import com.ecommerce.models.Cart;
import com.ecommerce.services.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

/**
 * Order Controller - Business logic for order operations
 */
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private OrderService orderService;

    public OrderController() {
        this.orderService = OrderService.getInstance();
    }

    /**
     * Create order from cart
     */
    public Order createOrder(int userId, Cart cart, String shippingAddress, Order.PaymentMethod paymentMethod) {
        logger.info("Creating order for user: " + userId);
        return orderService.createOrder(userId, cart, shippingAddress, paymentMethod);
    }

    /**
     * Get user's orders
     */
    public List<Order> getUserOrders(int userId) {
        logger.info("Fetching orders for user: " + userId);
        return orderService.getUserOrders(userId);
    }

    /**
     * Get order details
     */
    public Order getOrderDetails(int orderId) {
        logger.info("Fetching order details: " + orderId);
        return orderService.getOrderById(orderId);
    }

    /**
     * Update order status
     */
    public boolean updateOrderStatus(int orderId, Order.OrderStatus status) {
        logger.info("Updating order status - OrderID: " + orderId + ", Status: " + status);
        return orderService.updateOrderStatus(orderId, status);
    }

    /**
     * Update payment status
     */
    public boolean updatePaymentStatus(int orderId, Order.PaymentStatus status) {
        logger.info("Updating payment status - OrderID: " + orderId + ", Status: " + status);
        return orderService.updatePaymentStatus(orderId, status);
    }
}
