package com.ecommerce.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Cart Model for shopping cart functionality
 */
public class Cart {
    private int cartId;
    private int userId;
    private List<CartItem> items;
    private int totalItems;
    private double subtotal;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public Cart() {
        this.items = new ArrayList<>();
        this.totalItems = 0;
        this.subtotal = 0;
    }

    public Cart(int userId) {
        this();
        this.userId = userId;
    }

    // Getters and Setters
    public int getCartId() { return cartId; }
    public void setCartId(int cartId) { this.cartId = cartId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }

    public int getTotalItems() { return totalItems; }
    public void setTotalItems(int totalItems) { this.totalItems = totalItems; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Business logic methods
    public void addItem(CartItem item) {
        CartItem existing = items.stream()
                .filter(i -> i.getProductId() == item.getProductId())
                .findFirst()
                .orElse(null);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + item.getQuantity());
        } else {
            items.add(item);
        }
        calculateTotals();
    }

    public void removeItem(int productId) {
        items.removeIf(item -> item.getProductId() == productId);
        calculateTotals();
    }

    public void updateQuantity(int productId, int quantity) {
        items.stream()
                .filter(item -> item.getProductId() == productId)
                .forEach(item -> item.setQuantity(quantity));
        calculateTotals();
    }

    public void clear() {
        items.clear();
        calculateTotals();
    }

    public void calculateTotals() {
        this.totalItems = items.stream().mapToInt(CartItem::getQuantity).sum();
        this.subtotal = items.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public String toString() {
        return "Cart{" +
                "cartId=" + cartId +
                ", userId=" + userId +
                ", totalItems=" + totalItems +
                ", subtotal=" + subtotal +
                '}';
    }
}
