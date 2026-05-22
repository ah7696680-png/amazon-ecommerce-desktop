package com.ecommerce.services;

import com.ecommerce.models.Cart;
import com.ecommerce.models.CartItem;
import com.ecommerce.database.DatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;

/**
 * Cart Service - Shopping cart operations
 */
public class CartService {
    private static final Logger logger = LoggerFactory.getLogger(CartService.class);
    private static CartService instance;

    private CartService() {}

    public static synchronized CartService getInstance() {
        if (instance == null) {
            instance = new CartService();
        }
        return instance;
    }

    /**
     * Get user's cart
     */
    public Cart getCart(int userId) {
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "SELECT * FROM cart WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            Cart cart = null;
            if (rs.next()) {
                cart = new Cart(userId);
                cart.setCartId(rs.getInt("cart_id"));
                cart.setTotalItems(rs.getInt("total_items"));
                cart.setSubtotal(rs.getDouble("subtotal"));
            } else {
                // Create new cart
                cart = createCart(userId);
            }

            // Load cart items
            loadCartItems(cart);
            return cart;
        } catch (SQLException e) {
            logger.error("Error fetching cart", e);
        }
        return null;
    }

    /**
     * Add item to cart
     */
    public boolean addToCart(int userId, int productId, int quantity, double price) {
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            // Get or create cart
            Cart cart = getCart(userId);
            if (cart == null) {
                cart = createCart(userId);
            }

            // Check if item already exists
            String checkQuery = "SELECT * FROM cart_items WHERE cart_id = ? AND product_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, cart.getCartId());
            checkStmt.setInt(2, productId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // Update quantity
                int newQuantity = rs.getInt("quantity") + quantity;
                return updateCartItemQuantity(cart.getCartId(), productId, newQuantity);
            } else {
                // Insert new item
                String insertQuery = "INSERT INTO cart_items (cart_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                insertStmt.setInt(1, cart.getCartId());
                insertStmt.setInt(2, productId);
                insertStmt.setInt(3, quantity);
                insertStmt.setDouble(4, price);
                int result = insertStmt.executeUpdate();

                if (result > 0) {
                    updateCartTotals(cart.getCartId());
                    logger.info("Item added to cart for user: " + userId);
                    return true;
                }
            }
        } catch (SQLException e) {
            logger.error("Error adding to cart", e);
        }
        return false;
    }

    /**
     * Remove item from cart
     */
    public boolean removeFromCart(int cartId, int productId) {
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "DELETE FROM cart_items WHERE cart_id = ? AND product_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, cartId);
            stmt.setInt(2, productId);
            int result = stmt.executeUpdate();

            if (result > 0) {
                updateCartTotals(cartId);
                logger.info("Item removed from cart");
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error removing from cart", e);
        }
        return false;
    }

    /**
     * Clear cart
     */
    public boolean clearCart(int cartId) {
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "DELETE FROM cart_items WHERE cart_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, cartId);
            int result = stmt.executeUpdate();

            if (result > 0) {
                // Reset cart totals
                String updateQuery = "UPDATE cart SET total_items = 0, subtotal = 0, updated_at = NOW() WHERE cart_id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setInt(1, cartId);
                updateStmt.executeUpdate();
                logger.info("Cart cleared");
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error clearing cart", e);
        }
        return false;
    }

    // Private helper methods
    private Cart createCart(int userId) {
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "INSERT INTO cart (user_id, total_items, subtotal) VALUES (?, 0, 0)";
            PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, userId);
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                Cart cart = new Cart(userId);
                cart.setCartId(keys.getInt(1));
                logger.info("Cart created for user: " + userId);
                return cart;
            }
        } catch (SQLException e) {
            logger.error("Error creating cart", e);
        }
        return null;
    }

    private void loadCartItems(Cart cart) {
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "SELECT * FROM cart_items WHERE cart_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, cart.getCartId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                CartItem item = new CartItem();
                item.setCartItemId(rs.getInt("cart_item_id"));
                item.setCartId(rs.getInt("cart_id"));
                item.setProductId(rs.getInt("product_id"));
                item.setQuantity(rs.getInt("quantity"));
                item.setPrice(rs.getDouble("price"));
                cart.addItem(item);
            }
        } catch (SQLException e) {
            logger.error("Error loading cart items", e);
        }
    }

    private boolean updateCartItemQuantity(int cartId, int productId, int quantity) {
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            if (quantity <= 0) {
                return removeFromCart(cartId, productId);
            }

            String query = "UPDATE cart_items SET quantity = ?, updated_at = NOW() WHERE cart_id = ? AND product_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, quantity);
            stmt.setInt(2, cartId);
            stmt.setInt(3, productId);
            int result = stmt.executeUpdate();

            if (result > 0) {
                updateCartTotals(cartId);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error updating cart item quantity", e);
        }
        return false;
    }

    private void updateCartTotals(int cartId) {
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "SELECT SUM(quantity) as total_items, SUM(price * quantity) as subtotal FROM cart_items WHERE cart_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, cartId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int totalItems = rs.getInt("total_items");
                double subtotal = rs.getDouble("subtotal");

                String updateQuery = "UPDATE cart SET total_items = ?, subtotal = ?, updated_at = NOW() WHERE cart_id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setInt(1, totalItems);
                updateStmt.setDouble(2, subtotal);
                updateStmt.setInt(3, cartId);
                updateStmt.executeUpdate();
            }
        } catch (SQLException e) {
            logger.error("Error updating cart totals", e);
        }
    }
}
