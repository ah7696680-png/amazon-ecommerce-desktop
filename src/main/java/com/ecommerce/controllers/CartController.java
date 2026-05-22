package com.ecommerce.controllers;

import com.ecommerce.models.Cart;
import com.ecommerce.services.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cart Controller - Business logic for cart operations
 */
public class CartController {
    private static final Logger logger = LoggerFactory.getLogger(CartController.class);
    private CartService cartService;

    public CartController() {
        this.cartService = CartService.getInstance();
    }

    /**
     * Get user's cart
     */
    public Cart getCart(int userId) {
        logger.info("Fetching cart for user: " + userId);
        return cartService.getCart(userId);
    }

    /**
     * Add item to cart
     */
    public boolean addToCart(int userId, int productId, int quantity, double price) {
        logger.info("Adding item to cart - User: " + userId + ", Product: " + productId + ", Qty: " + quantity);
        return cartService.addToCart(userId, productId, quantity, price);
    }

    /**
     * Remove item from cart
     */
    public boolean removeFromCart(int cartId, int productId) {
        logger.info("Removing item from cart - CartID: " + cartId + ", ProductID: " + productId);
        return cartService.removeFromCart(cartId, productId);
    }

    /**
     * Clear cart
     */
    public boolean clearCart(int cartId) {
        logger.info("Clearing cart: " + cartId);
        return cartService.clearCart(cartId);
    }
}
