package com.ecommerce.models;

/**
 * OrderItem Model representing items in an order
 */
public class OrderItem {
    private int orderItemId;
    private int orderId;
    private int productId;
    private int quantity;
    private double unitPrice;
    private double totalPrice;
    private ReturnStatus returnStatus;

    public enum ReturnStatus {
        NONE, REQUESTED, APPROVED, RETURNED
    }

    // Constructors
    public OrderItem() {
        this.returnStatus = ReturnStatus.NONE;
    }

    public OrderItem(int productId, int quantity, double unitPrice) {
        this();
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = quantity * unitPrice;
    }

    // Getters and Setters
    public int getOrderItemId() { return orderItemId; }
    public void setOrderItemId(int orderItemId) { this.orderItemId = orderItemId; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public ReturnStatus getReturnStatus() { return returnStatus; }
    public void setReturnStatus(ReturnStatus returnStatus) { this.returnStatus = returnStatus; }

    public void calculateTotal() {
        this.totalPrice = quantity * unitPrice;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "orderItemId=" + orderItemId +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", totalPrice=" + totalPrice +
                '}';
    }
}
