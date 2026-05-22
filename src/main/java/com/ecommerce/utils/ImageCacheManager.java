package com.ecommerce.utils;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Image Cache Manager - Caching and loading product images
 */
public class ImageCacheManager {
    private static final Logger logger = LoggerFactory.getLogger(ImageCacheManager.class);
    private static ImageCacheManager instance;
    private Map<String, Image> imageCache;
    private final long MAX_CACHE_SIZE = 104857600; // 100MB
    private long currentCacheSize = 0;

    private ImageCacheManager() {
        this.imageCache = new HashMap<>();
    }

    public static synchronized ImageCacheManager getInstance() {
        if (instance == null) {
            instance = new ImageCacheManager();
        }
        return instance;
    }

    /**
     * Load image from file path
     */
    public Image loadImage(String filePath, double width, double height) {
        try {
            if (imageCache.containsKey(filePath)) {
                return imageCache.get(filePath);
            }

            File imageFile = new File(filePath);
            if (!imageFile.exists()) {
                logger.warn("Image file not found: " + filePath);
                return getDefaultImage();
            }

            FileInputStream fis = new FileInputStream(imageFile);
            Image image = new Image(fis, width, height, true, true);
            fis.close();

            // Cache the image if space available
            if (currentCacheSize + imageFile.length() < MAX_CACHE_SIZE) {
                imageCache.put(filePath, image);
                currentCacheSize += imageFile.length();
            }

            return image;
        } catch (IOException e) {
            logger.error("Error loading image: " + filePath, e);
            return getDefaultImage();
        }
    }

    /**
     * Load image from URL
     */
    public Image loadImageFromUrl(String imageUrl, double width, double height) {
        try {
            if (imageCache.containsKey(imageUrl)) {
                return imageCache.get(imageUrl);
            }

            Image image = new Image(imageUrl, width, height, true, true);
            imageCache.put(imageUrl, image);
            return image;
        } catch (Exception e) {
            logger.error("Error loading image from URL: " + imageUrl, e);
            return getDefaultImage();
        }
    }

    /**
     * Get cached image
     */
    public Image getCachedImage(String path) {
        return imageCache.getOrDefault(path, getDefaultImage());
    }

    /**
     * Clear cache
     */
    public void clearCache() {
        imageCache.clear();
        currentCacheSize = 0;
        logger.info("Image cache cleared");
    }

    /**
     * Get default placeholder image
     */
    private Image getDefaultImage() {
        try {
            return new Image(getClass().getResourceAsStream("/assets/placeholder.png"));
        } catch (Exception e) {
            logger.error("Error loading default image", e);
            return null;
        }
    }
}
