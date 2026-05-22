package com.ecommerce.ui.scenes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.ecommerce.auth.AuthenticationService;
import com.ecommerce.models.User;
import com.ecommerce.utils.UIUtils;
import com.ecommerce.utils.AnimationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main Scene Controller - Login and navigation
 */
public class MainScene {
    private static final Logger logger = LoggerFactory.getLogger(MainScene.class);
    private Stage primaryStage;
    private Scene loginScene;
    private Scene dashboardScene;

    public MainScene(Stage primaryStage) {
        this.primaryStage = primaryStage;
        createLoginScene();
    }

    /**
     * Create login scene
     */
    private void createLoginScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1e1e1e;");

        // Center login form
        VBox loginForm = createLoginForm();
        root.setCenter(loginForm);

        loginScene = new Scene(root, 1400, 900);
    }

    /**
     * Create login form
     */
    private VBox createLoginForm() {
        VBox loginBox = new VBox(15);
        loginBox.setAlignment(Pos.CENTER);
        loginBox.setPadding(new Insets(50));
        loginBox.setStyle("-fx-background-color: #2d2d2d; -fx-border-radius: 10; -fx-padding: 40;");
        loginBox.setPrefWidth(400);
        loginBox.setMaxWidth(400);

        // Title
        Label titleLabel = new Label("E-Commerce Login");
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #ffffff;");

        // Email field
        Label emailLabel = new Label("Email:");
        emailLabel.setStyle("-fx-text-fill: #cccccc;");
        TextField emailField = new TextField();
        emailField.setPromptText("Enter your email");
        emailField.setPrefHeight(40);
        emailField.setStyle("-fx-font-size: 12; -fx-padding: 10; -fx-control-inner-background: #3d3d3d; -fx-text-fill: #ffffff;");

        // Password field
        Label passwordLabel = new Label("Password:");
        passwordLabel.setStyle("-fx-text-fill: #cccccc;");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setPrefHeight(40);
        passwordField.setStyle("-fx-font-size: 12; -fx-padding: 10; -fx-control-inner-background: #3d3d3d; -fx-text-fill: #ffffff;");

        // Remember me checkbox
        CheckBox rememberMe = new CheckBox("Remember me");
        rememberMe.setStyle("-fx-text-fill: #cccccc;");

        // Login button
        Button loginButton = new Button("Login");
        loginButton.setPrefHeight(45);
        loginButton.setPrefWidth(150);
        loginButton.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-background-color: #1e88e5; -fx-text-fill: #ffffff; -fx-border-radius: 5;");

        loginButton.setOnAction(e -> {
            String email = emailField.getText();
            String password = passwordField.getText();

            if (email.isEmpty() || password.isEmpty()) {
                UIUtils.showError("Validation Error", "Please enter email and password");
                return;
            }

            AuthenticationService authService = AuthenticationService.getInstance();
            User user = authService.authenticate(email, password);

            if (user != null) {
                logger.info("User logged in: " + user.getEmail());
                showDashboard(user);
            } else {
                UIUtils.showError("Login Failed", "Invalid email or password");
            }
        });

        // Sign up link
        HBox signupBox = new HBox();
        signupBox.setAlignment(Pos.CENTER);
        Label signupLabel = new Label("Don't have an account? ");
        signupLabel.setStyle("-fx-text-fill: #cccccc;");
        Hyperlink signupLink = new Hyperlink("Sign up here");
        signupLink.setStyle("-fx-text-fill: #1e88e5; -fx-font-size: 12;");
        signupLink.setOnAction(e -> showSignupScene());
        signupBox.getChildren().addAll(signupLabel, signupLink);

        loginBox.getChildren().addAll(
                titleLabel,
                emailLabel,
                emailField,
                passwordLabel,
                passwordField,
                rememberMe,
                loginButton,
                signupBox
        );

        VBox centerBox = new VBox();
        centerBox.setAlignment(Pos.CENTER);
        centerBox.getChildren().add(loginBox);

        AnimationUtils.fadeIn(centerBox, javafx.util.Duration.millis(500));
        return centerBox;
    }

    /**
     * Show signup scene
     */
    private void showSignupScene() {
        UIUtils.showSuccess("Signup", "Signup functionality to be implemented");
    }

    /**
     * Show dashboard after login
     */
    private void showDashboard(User user) {
        BorderPane dashboardRoot = new BorderPane();
        dashboardRoot.setStyle("-fx-background-color: #1e1e1e;");

        // Top navbar
        HBox navbar = createNavbar(user);
        dashboardRoot.setTop(navbar);

        // Left sidebar
        VBox sidebar = createSidebar(user);
        dashboardRoot.setLeft(sidebar);

        // Center content
        VBox mainContent = createMainContent(user);
        dashboardRoot.setCenter(mainContent);

        dashboardScene = new Scene(dashboardRoot, 1400, 900);
        primaryStage.setScene(dashboardScene);
    }

    /**
     * Create navigation bar
     */
    private HBox createNavbar(User user) {
        HBox navbar = new HBox();
        navbar.setStyle("-fx-background-color: #2d2d2d; -fx-padding: 10 20; -fx-border-color: #444444; -fx-border-width: 0 0 1 0;");
        navbar.setAlignment(Pos.CENTER_LEFT);
        navbar.setSpacing(20);

        Label appTitle = new Label("Amazon E-Commerce");
        appTitle.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #1e88e5;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Label userLabel = new Label(user.getFullName());
        userLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 12;");

        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-padding: 8 15; -fx-background-color: #ff5252; -fx-text-fill: #ffffff; -fx-font-size: 12;");
        logoutButton.setOnAction(e -> logout());

        navbar.getChildren().addAll(appTitle, spacer, userLabel, logoutButton);
        return navbar;
    }

    /**
     * Create sidebar
     */
    private VBox createSidebar(User user) {
        VBox sidebar = new VBox();
        sidebar.setStyle("-fx-background-color: #252525; -fx-padding: 20; -fx-border-color: #444444; -fx-border-width: 0 1 0 0;");
        sidebar.setPrefWidth(200);
        sidebar.setSpacing(10);

        // Navigation buttons
        Button dashboardBtn = createNavButton("Dashboard", e -> {});
        Button productsBtn = createNavButton("Products", e -> {});
        Button ordersBtn = createNavButton("Orders", e -> {});
        Button wishlistBtn = createNavButton("Wishlist", e -> {});
        Button settingsBtn = createNavButton("Settings", e -> {});

        if (user.getRole() == User.UserRole.ADMIN) {
            Button adminBtn = createNavButton("Admin Panel", e -> {});
            sidebar.getChildren().add(adminBtn);
        }

        sidebar.getChildren().addAll(dashboardBtn, productsBtn, ordersBtn, wishlistBtn, settingsBtn);
        return sidebar;
    }

    /**
     * Create navigation button
     */
    private Button createNavButton(String text, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button btn = new Button(text);
        btn.setPrefWidth(160);
        btn.setStyle("-fx-padding: 10 15; -fx-background-color: #3d3d3d; -fx-text-fill: #cccccc; -fx-font-size: 12;");
        btn.setOnAction(handler);
        return btn;
    }

    /**
     * Create main content area
     */
    private VBox createMainContent(User user) {
        VBox content = new VBox();
        content.setStyle("-fx-background-color: #1e1e1e; -fx-padding: 20;");

        Label welcomeLabel = new Label("Welcome, " + user.getFullName() + "!");
        welcomeLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #ffffff;");

        Label descLabel = new Label("This is the main dashboard. More features coming soon!");
        descLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 14;");

        content.getChildren().addAll(welcomeLabel, descLabel);
        AnimationUtils.fadeIn(content, javafx.util.Duration.millis(500));
        return content;
    }

    /**
     * Logout
     */
    private void logout() {
        AuthenticationService.getInstance().logout();
        primaryStage.setScene(loginScene);
    }

    public Scene getLoginScene() {
        return loginScene;
    }
}
