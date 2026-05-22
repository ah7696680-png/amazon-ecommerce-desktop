package com.ecommerce.controllers;

import com.ecommerce.models.Product;
import com.ecommerce.services.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

/**
 * Product Controller - Business logic for product operations
 */
public class ProductController {
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private ProductService productService;

    public ProductController() {
        this.productService = ProductService.getInstance();
    }

    /**
     * Get all products
     */
    public List<Product> getAllProducts() {
        logger.info("Fetching all products");
        return productService.getAllProducts();
    }

    /**
     * Get products by category
     */
    public List<Product> getProductsByCategory(int categoryId) {
        logger.info("Fetching products for category: " + categoryId);
        return productService.getProductsByCategory(categoryId);
    }

    /**
     * Search products
     */
    public List<Product> searchProducts(String keyword) {
        logger.info("Searching products with keyword: " + keyword);
        return productService.searchProducts(keyword);
    }

    /**
     * Get featured products
     */
    public List<Product> getFeaturedProducts() {
        logger.info("Fetching featured products");
        return productService.getFeaturedProducts();
    }

    /**
     * Get product details
     */
    public Product getProductDetails(int productId) {
        logger.info("Fetching product details: " + productId);
        return productService.getProductById(productId);
    }

    /**
     * Create new product
     */
    public boolean createProduct(Product product) {
        logger.info("Creating new product: " + product.getName());
        return productService.createProduct(product);
    }

    /**
     * Update product
     */
    public boolean updateProduct(Product product) {
        logger.info("Updating product: " + product.getName());
        return productService.updateProduct(product);
    }

    /**
     * Delete product
     */
    public boolean deleteProduct(int productId) {
        logger.info("Deleting product: " + productId);
        return productService.deleteProduct(productId);
    }
}
