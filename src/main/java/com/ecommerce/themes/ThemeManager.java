package com.ecommerce.themes;

import javafx.scene.Scene;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Theme Manager - Handles dark and light themes
 */
public class ThemeManager {
    private static final Logger logger = LoggerFactory.getLogger(ThemeManager.class);
    private static ThemeManager instance;
    private String currentTheme;
    private String cssPath;

    private ThemeManager() {
        this.currentTheme = "dark";
    }

    public static synchronized ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }

    /**
     * Load theme by name
     */
    public void loadTheme(String themeName) {
        try {
            this.currentTheme = themeName;
            if ("dark".equals(themeName)) {
                this.cssPath = getClass().getResource("/css/dark-theme.css").toExternalForm();
            } else if ("light".equals(themeName)) {
                this.cssPath = getClass().getResource("/css/light-theme.css").toExternalForm();
            }
            logger.info("Theme loaded: " + themeName);
        } catch (Exception e) {
            logger.error("Error loading theme", e);
        }
    }

    /**
     * Apply theme to scene
     */
    public void applyTheme(Scene scene) {
        try {
            if (cssPath != null) {
                scene.getStylesheets().clear();
                scene.getStylesheets().add(cssPath);
            }
        } catch (Exception e) {
            logger.error("Error applying theme", e);
        }
    }

    /**
     * Toggle between themes
     */
    public void toggleTheme() {
        if ("dark".equals(currentTheme)) {
            loadTheme("light");
        } else {
            loadTheme("dark");
        }
    }

    public String getCurrentTheme() {
        return currentTheme;
    }

    public String getCssPath() {
        return cssPath;
    }
}
