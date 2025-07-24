# WalletWise - Digital Wallet Application

A comprehensive digital wallet application built with Spring Boot 3.x, featuring user authentication, wallet management, money transfers, OTP verification, and real-time notifications.

## 🚀 Features

### Core Features
- **User Authentication & Management**
  - User registration with automatic wallet creation
  - JWT-based authentication
  - Password encryption using BCrypt
  - User profile management

- **Wallet Operations**
  - Check wallet balance
  - Add money to wallet
  - Transfer money between users
  - Transaction history

- **OTP System**
  - Generate and send OTP with Redis TTL
  - Verify OTP with automatic expiry
  - Secure OTP storage

- **Messaging & Notifications**
  - RabbitMQ integration for async notifications
  - Transaction event publishing
  - Real-time notification processing

## 🛠 Tech Stack

- **Backend**: Spring Boot 3.2.0 (Java 17+)
- **Database**: PostgreSQL
- **Cache**: Redis
- **Message Queue**: RabbitMQ
- **Security**: Spring Security + JWT
- **Build Tool**: Maven
- **Additional**: Lombok, JPA/Hibernate

## 📋 Prerequisites

Before running the application, ensure you have the following installed:

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+
- Redis 6+
- RabbitMQ 3.8+

## ⚙️ Setup Instructions

### 1. Database Setup

#### PostgreSQL Setup
```bash
# Create database
createdb walletwise_db

# Run the schema (optional - Spring Boot will auto-create tables)
psql -d walletwise_db -f database-schema.sql
```

#### Redis Setup
```bash
# Start Redis server
redis-server

# Or using Docker
docker run -d -p 6379:6379 redis:latest
```

#### RabbitMQ Setup
```bash
# Start RabbitMQ server
rabbitmq-server

# Or using Docker
docker run -d -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

### 2. Application Configuration

Update `src/main/resources/application.yml` with your database credentials:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/walletwise_db
    username: your_username
    password: your_password
  
  data:
    redis:
      host: localhost
      port: 6379
      password: # your_redis_password if any
  
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
```

### 3. Build and Run

```bash
# Clone the repository
git clone <repository-url>
cd walletwise

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run

# Or run the JAR file
java -jar target/walletwise-1.0.0.jar
```

The application will start on `http://localhost:8080`

## 📚 API Documentation

### Authentication Endpoints

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123",
  "fullName": "John Doe",
  "phoneNumber": "+1234567890"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "usernameOrEmail": "john_doe",
  "password": "password123"
}
```

### Wallet Endpoints (Requires JWT Token)

#### Get Balance
```http
GET /api/wallet/balance
Authorization: Bearer <jwt_token>
```

#### Add Money
```http
POST /api/wallet/add
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
  "amount": 100.50,
  "description": "Adding money to wallet"
}
```

#### Transfer Money
```http
POST /api/wallet/transfer
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
  "recipientIdentifier": "jane_doe",
  "amount": 50.00,
  "description": "Payment for services"
}
```

#### Transaction History
```http
GET /api/wallet/history
Authorization: Bearer <jwt_token>
```

### OTP Endpoints

#### Send OTP
```http
POST /api/otp/send
Content-Type: application/json

{
  "identifier": "+1234567890"
}
```

#### Verify OTP
```http
POST /api/otp/verify
Content-Type: application/json

{
  "identifier": "+1234567890",
  "otp": "123456"
}
```

## 🔧 Configuration Options

### JWT Configuration
- **Secret Key**: Configure in `jwt.secret`
- **Expiration**: Set token expiry in `jwt.expiration` (milliseconds)

### OTP Configuration
- **Length**: Set OTP length in `otp.length`
- **Expiration**: Set OTP expiry in `otp.expiration` (seconds)

### RabbitMQ Configuration
- **Queue**: Configure queue name in `rabbitmq.queue.transaction`
- **Exchange**: Set exchange name in `rabbitmq.exchange.transaction`
- **Routing Key**: Configure routing key in `rabbitmq.routing-key.transaction`

## 🏗 Project Structure

```
src/main/java/com/lee/walletwise/
├── config/          # Configuration classes
│   ├── SecurityConfig.java
│   ├── RedisConfig.java
│   └── RabbitMQConfig.java
├── controller/      # REST Controllers
│   ├── AuthController.java
│   ├── WalletController.java
│   ├── TransactionController.java
│   └── OtpController.java
├── dto/             # Data Transfer Objects
│   ├── AuthRequest.java
│   ├── AuthResponse.java
│   ├── RegisterRequest.java
│   ├── AddMoneyRequest.java
│   ├── TransferRequest.java
│   └── ...
├── entity/          # JPA Entities
│   ├── User.java
│   ├── Wallet.java
│   └── Transaction.java
├── repository/      # JPA Repositories
│   ├── UserRepository.java
│   ├── WalletRepository.java
│   └── TransactionRepository.java
├── service/         # Business Logic
│   ├── UserService.java
│   ├── WalletService.java
│   ├── TransactionService.java
│   ├── OtpService.java
│   ├── JwtService.java
│   └── RabbitProducerService.java
├── filter/          # Security Filters
│   └── JwtAuthFilter.java
└── WalletWiseApplication.java
```

## 🔒 Security Features

- **JWT Authentication**: Secure token-based authentication
- **Password Encryption**: BCrypt hashing for passwords
- **Input Validation**: Comprehensive request validation
- **CORS Support**: Cross-origin resource sharing enabled
- **Rate Limiting**: Built-in protection against abuse

## 📊 Database Schema

The application uses three main tables:

1. **users** - User account information
2. **wallets** - Wallet details and balances
3. **transactions** - Transaction history and details

## 🚀 Deployment

### Docker Deployment (Optional)

Create a `Dockerfile`:

```dockerfile
FROM openjdk:17-jdk-slim
COPY target/walletwise-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

Build and run:
```bash
docker build -t walletwise .
docker run -p 8080:8080 walletwise
```

### Production Considerations

1. **Environment Variables**: Use environment variables for sensitive configuration
2. **Database Connection Pooling**: Configure HikariCP for production
3. **Logging**: Configure appropriate log levels and file appenders
4. **Monitoring**: Add health checks and metrics endpoints
5. **SSL/TLS**: Enable HTTPS in production

## 🧪 Testing

```bash
# Run all tests
mvn test

# Run with coverage
mvn test jacoco:report
```

## 📝 API Response Format

All API responses follow a consistent format:

```json
{
  "success": true,
  "message": "Operation successful",
  "data": {
    // Response data here
  }
}
```

## 🐛 Troubleshooting

### Common Issues

1. **Database Connection**: Ensure PostgreSQL is running and credentials are correct
2. **Redis Connection**: Verify Redis server is accessible
3. **RabbitMQ Connection**: Check RabbitMQ server status and credentials
4. **JWT Issues**: Verify secret key configuration and token format

### Logs

Check application logs for detailed error information:
```bash
tail -f logs/walletwise.log
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📞 Support

For support and questions:
- Create an issue in the repository
- Email: support@walletwise.com

---

**WalletWise** - Secure, Fast, and Reliable Digital Wallet Solution