package com.ecommerce;

import javafx.application.Application;
import javafx.stage.Stage;
import com.ecommerce.ui.scenes.MainScene;
import com.ecommerce.services.DatabaseService;
import com.ecommerce.themes.ThemeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point for E-Commerce Desktop Application
 * Amazon-style enterprise application with JavaFX and MySQL
 */
public class Main extends Application {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    @Override
    public void start(Stage primaryStage) {
        try {
            logger.info("Starting E-Commerce Application...");
            
            // Initialize database
            DatabaseService.getInstance().connect();
            logger.info("Database connected successfully");
            
            // Load theme
            ThemeManager.getInstance().loadTheme("dark");
            
            // Set application window properties
            primaryStage.setTitle("E-Commerce Desktop - Amazon Style");
            primaryStage.setWidth(1400);
            primaryStage.setHeight(900);
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(700);
            
            // Show login scene
            primaryStage.setScene(new MainScene(primaryStage).getLoginScene());
            primaryStage.show();
            
            logger.info("Application started successfully");
            
        } catch (Exception e) {
            logger.error("Failed to start application", e);
            System.exit(1);
        }
    }

    @Override
    public void stop() {
        try {
            DatabaseService.getInstance().disconnect();
            logger.info("Application closed successfully");
        } catch (Exception e) {
            logger.error("Error closing application", e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
