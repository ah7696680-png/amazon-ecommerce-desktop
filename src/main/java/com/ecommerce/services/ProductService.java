package com.ecommerce.services;

import com.ecommerce.models.Product;
import com.ecommerce.database.DatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Product Service - CRUD operations for products
 */
public class ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private static ProductService instance;

    private ProductService() {}

    public static synchronized ProductService getInstance() {
        if (instance == null) {
            instance = new ProductService();
        }
        return instance;
    }

    /**
     * Get all products
     */
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "SELECT * FROM products WHERE status = 'ACTIVE' ORDER BY created_at DESC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching products", e);
        }
        return products;
    }

    /**
     * Get products by category
     */
    public List<Product> getProductsByCategory(int categoryId) {
        List<Product> products = new ArrayList<>();
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "SELECT * FROM products WHERE category_id = ? AND status = 'ACTIVE' ORDER BY created_at DESC";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, categoryId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching products by category", e);
        }
        return products;
    }

    /**
     * Get product by ID
     */
    public Product getProductById(int productId) {
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "SELECT * FROM products WHERE product_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Product product = mapResultSetToProduct(rs);
                // Load images
                product.setImageUrls(getProductImages(productId));
                return product;
            }
        } catch (SQLException e) {
            logger.error("Error fetching product", e);
        }
        return null;
    }

    /**
     * Search products
     */
    public List<Product> searchProducts(String keyword) {
        List<Product> products = new ArrayList<>();
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "SELECT * FROM products WHERE MATCH(name, description) AGAINST(? IN BOOLEAN MODE) AND status = 'ACTIVE'";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, keyword);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            logger.error("Error searching products", e);
        }
        return products;
    }

    /**
     * Get featured products
     */
    public List<Product> getFeaturedProducts() {
        List<Product> products = new ArrayList<>();
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "SELECT * FROM products WHERE is_featured = true AND status = 'ACTIVE' ORDER BY rating DESC LIMIT 10";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching featured products", e);
        }
        return products;
    }

    /**
     * Get product images
     */
    public List<String> getProductImages(int productId) {
        List<String> images = new ArrayList<>();
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "SELECT image_url FROM product_images WHERE product_id = ? ORDER BY display_order";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                images.add(rs.getString("image_url"));
            }
        } catch (SQLException e) {
            logger.error("Error fetching product images", e);
        }
        return images;
    }

    /**
     * Create new product
     */
    public boolean createProduct(Product product) {
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "INSERT INTO products (seller_id, category_id, name, slug, description, price, cost_price, discount_percent, sku, quantity_in_stock, main_image_url, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, product.getSellerId());
            stmt.setInt(2, product.getCategoryId());
            stmt.setString(3, product.getName());
            stmt.setString(4, product.getSlug());
            stmt.setString(5, product.getDescription());
            stmt.setDouble(6, product.getPrice());
            stmt.setDouble(7, product.getCostPrice());
            stmt.setDouble(8, product.getDiscountPercent());
            stmt.setString(9, product.getSku());
            stmt.setInt(10, product.getQuantityInStock());
            stmt.setString(11, product.getMainImageUrl());
            stmt.setString(12, product.getStatus().toString());

            int result = stmt.executeUpdate();
            if (result > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    product.setProductId(keys.getInt(1));
                }
                logger.info("Product created: " + product.getName());
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error creating product", e);
        }
        return false;
    }

    /**
     * Update product
     */
    public boolean updateProduct(Product product) {
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "UPDATE products SET name = ?, description = ?, price = ?, discount_percent = ?, quantity_in_stock = ?, status = ?, updated_at = NOW() WHERE product_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getDescription());
            stmt.setDouble(3, product.getPrice());
            stmt.setDouble(4, product.getDiscountPercent());
            stmt.setInt(5, product.getQuantityInStock());
            stmt.setString(6, product.getStatus().toString());
            stmt.setInt(7, product.getProductId());

            int result = stmt.executeUpdate();
            logger.info("Product updated: " + product.getName());
            return result > 0;
        } catch (SQLException e) {
            logger.error("Error updating product", e);
        }
        return false;
    }

    /**
     * Delete product
     */
    public boolean deleteProduct(int productId) {
        try (Connection conn = DatabaseService.getInstance().getConnection()) {
            String query = "UPDATE products SET status = 'DISCONTINUED' WHERE product_id = ?";
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

    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setProductId(rs.getInt("product_id"));
        product.setSellerId(rs.getInt("seller_id"));
        product.setCategoryId(rs.getInt("category_id"));
        product.setName(rs.getString("name"));
        product.setSlug(rs.getString("slug"));
        product.setDescription(rs.getString("description"));
        product.setPrice(rs.getDouble("price"));
        product.setCostPrice(rs.getDouble("cost_price"));
        product.setDiscountPercent(rs.getDouble("discount_percent"));
        product.setFinalPrice(rs.getDouble("final_price"));
        product.setSku(rs.getString("sku"));
        product.setQuantityInStock(rs.getInt("quantity_in_stock"));
        product.setRating(rs.getDouble("rating"));
        product.setReviewCount(rs.getInt("review_count"));
        product.setStatus(Product.ProductStatus.valueOf(rs.getString("status")));
        product.setFeatured(rs.getBoolean("is_featured"));
        product.setVerified(rs.getBoolean("is_verified"));
        product.setMainImageUrl(rs.getString("main_image_url"));
        product.setViewsCount(rs.getInt("views_count"));
        return product;
    }
}
