-- Setup Guide for E-Commerce Application

## Prerequisites
- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.8 or higher
- JavaFX SDK 21 (optional if using OpenJFX Maven plugin)

## Installation Steps

### 1. Create Database
```bash
mysql -u root -p < sql/schema.sql
```

### 2. Insert Sample Data
```bash
mysql -u root -p ecommerce < sql/sample_data.sql
```

### 3. Update Database Configuration
Edit `config.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/ecommerce
db.user=root
db.password=your_password
```

### 4. Build with Maven
```bash
mvn clean package
```

### 5. Run Application
```bash
mvn javafx:run
```

Or run JAR:
```bash
java -jar target/ecommerce-app.jar
```

## Default Login Credentials

### Admin Account
- Email: admin@ecommerce.com
- Password: Password@123
- Role: Admin

### Seller Account
- Email: seller@ecommerce.com
- Password: Password@123
- Role: Seller

### Customer Account
- Email: customer@ecommerce.com
- Password: Password@123
- Role: Customer

## Project Structure

```
ecommerce-app/
├── src/main/java/com/ecommerce/
│   ├── Main.java                 # Application entry point
│   ├── auth/                     # Authentication module
│   ├── models/                   # Data models
│   ├── controllers/              # Business logic controllers
│   ├── services/                 # Service layer
│   ├── database/                 # Database operations
│   ├── admin/                    # Admin management
│   ├── virtualtryon/             # Virtual try-on feature
│   ├── analytics/                # Analytics service
│   ├── themes/                   # Theme management
│   ├── ui/                       # UI components and scenes
│   └── utils/                    # Utility classes
├── src/main/resources/
│   ├── css/                      # Stylesheets
│   ├── assets/                   # Images and resources
│   └── fxml/                     # JavaFX FXML files
├── sql/
│   ├── schema.sql                # Database schema
│   └── sample_data.sql           # Sample data
├── pom.xml                       # Maven configuration
└── config.properties             # Application config
```

## Key Features

### Authentication
- Secure login/signup/password recovery
- BCrypt password hashing
- Session management
- Role-based access control (Admin, Seller, Customer)

### Product Management
- Browse and search products
- View detailed product information
- Category-based filtering
- High-resolution product images
- Product ratings and reviews

### Shopping
- Add/remove items from cart
- Wishlist functionality
- Coupon/discount application
- Multiple payment methods

### Orders
- Order placement and tracking
- Shipment status updates
- Order history
- Order cancellation/returns

### Admin Dashboard
- User management
- Product management and verification
- Order management
- Analytics and reporting
- System configuration
- Database backup/restore

### Virtual Try-On
- Clothing fit visualization
- Glasses/accessories overlay
- Real-time webcam integration
- Image upload support

### Analytics
- Sales metrics
- Revenue tracking
- User statistics
- Product performance
- Custom reports

## Database Schema

The application uses the following main tables:
- `users` - User accounts (customers, sellers, admins)
- `products` - Product catalog
- `categories` - Product categories
- `orders` - Purchase orders
- `cart` - Shopping carts
- `reviews` - Product reviews
- `coupons` - Discount codes
- `notifications` - User notifications

## Troubleshooting

### Connection Failed
- Verify MySQL is running
- Check database credentials in config.properties
- Ensure database is created and tables exist

### OpenCV Issues
- Virtual try-on requires OpenCV
- Install via: `mvn install:install-file -Dfile=opencv-java.jar ...`
- Or disable virtual try-on features if not needed

### JavaFX Issues
- Ensure JavaFX SDK is installed
- Update JavaFX_HOME environment variable
- Use javafx-maven-plugin for automatic setup

## Support and Contributing

For issues, feature requests, or contributions, please visit the GitHub repository.

## License

MIT License - See LICENSE file for details
