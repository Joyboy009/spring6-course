# WalletWise - Step-by-Step Run Guide

This guide will walk you through setting up and running the WalletWise application from scratch.

## 📋 Prerequisites Check

Before starting, ensure you have the following installed:

```bash
# Check Java version (should be 17+)
java -version

# Check Maven version
mvn -version

# Check if PostgreSQL is installed
psql --version

# Check if Redis is available
redis-cli --version

# Check if RabbitMQ is available
rabbitmq-diagnostics status
```

## 🚀 Step 1: Set Up Infrastructure

### 1.1 Start PostgreSQL

```bash
# Option 1: Using local PostgreSQL
sudo service postgresql start

# Option 2: Using Docker
docker run --name postgres-walletwise \
  -e POSTGRES_DB=walletwise_db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=password \
  -p 5432:5432 -d postgres:13

# Create database (if using local PostgreSQL)
createdb walletwise_db
```

### 1.2 Start Redis

```bash
# Option 1: Using local Redis
redis-server

# Option 2: Using Docker
docker run --name redis-walletwise -p 6379:6379 -d redis:latest

# Test Redis connection
redis-cli ping
# Should return: PONG
```

### 1.3 Start RabbitMQ

```bash
# Option 1: Using local RabbitMQ
sudo service rabbitmq-server start

# Option 2: Using Docker
docker run --name rabbitmq-walletwise \
  -p 5672:5672 -p 15672:15672 \
  -d rabbitmq:3-management

# Check RabbitMQ status
rabbitmq-diagnostics status

# Access RabbitMQ Management UI (optional)
# http://localhost:15672 (guest/guest)
```

## 🔧 Step 2: Configure Application

### 2.1 Update Database Configuration

Edit `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/walletwise_db
    username: postgres  # Update with your username
    password: password  # Update with your password
```

### 2.2 Verify Other Configurations

Ensure Redis and RabbitMQ configurations match your setup:

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
  
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
```

## 🏗 Step 3: Build and Run Application

### 3.1 Build the Project

```bash
# Navigate to project directory
cd walletwise

# Clean and compile
mvn clean compile

# Run tests (optional)
mvn test

# Package the application
mvn clean package -DskipTests
```

### 3.2 Run the Application

```bash
# Option 1: Using Maven
mvn spring-boot:run

# Option 2: Using JAR file
java -jar target/walletwise-1.0.0.jar

# Option 3: With custom profile
java -jar target/walletwise-1.0.0.jar --spring.profiles.active=dev
```

### 3.3 Verify Application Startup

Check the logs for successful startup:

```
Started WalletWiseApplication in X.XXX seconds
```

The application should be running on: `http://localhost:8080`

## 🧪 Step 4: Test the Application

### 4.1 Using cURL Commands

#### Register a User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "password123",
    "fullName": "John Doe",
    "phoneNumber": "+1234567890"
  }'
```

#### Login User
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "john_doe",
    "password": "password123"
  }'
```

**Save the JWT token from the response for subsequent requests.**

#### Check Wallet Balance
```bash
curl -X GET http://localhost:8080/api/wallet/balance \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### Add Money to Wallet
```bash
curl -X POST http://localhost:8080/api/wallet/add \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "amount": 100.50,
    "description": "Adding money to wallet"
  }'
```

### 4.2 Using Postman

1. Import the `WalletWise-Postman-Collection.json` file into Postman
2. Set the `baseUrl` variable to `http://localhost:8080`
3. Run the "Register User" request
4. Run the "Login User" request and copy the token
5. Set the `authToken` variable with the JWT token
6. Test other endpoints

### 4.3 Test OTP Functionality

#### Send OTP
```bash
curl -X POST http://localhost:8080/api/otp/send \
  -H "Content-Type: application/json" \
  -d '{
    "identifier": "+1234567890"
  }'
```

#### Verify OTP (use the OTP from the response)
```bash
curl -X POST http://localhost:8080/api/otp/verify \
  -H "Content-Type: application/json" \
  -d '{
    "identifier": "+1234567890",
    "otp": "123456"
  }'
```

## 🔍 Step 5: Verify System Components

### 5.1 Check Database Tables

```bash
# Connect to PostgreSQL
psql -d walletwise_db

# List tables
\dt

# Check users table
SELECT * FROM users;

# Check wallets table
SELECT * FROM wallets;

# Check transactions table
SELECT * FROM transactions;
```

### 5.2 Check Redis Data

```bash
# Connect to Redis
redis-cli

# List all keys
KEYS *

# Check OTP data (if any exists)
KEYS otp:*

# Get OTP value (replace with actual key)
GET otp:+1234567890
```

### 5.3 Check RabbitMQ Messages

1. Access RabbitMQ Management UI: `http://localhost:15672`
2. Login with `guest/guest`
3. Go to "Queues" tab
4. Check the `transaction.queue` for messages
5. Perform a wallet transaction and verify message appears

## 🧪 Step 6: Complete End-to-End Test

### 6.1 Create Two Users

```bash
# Register User 1
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice",
    "email": "alice@example.com",
    "password": "password123",
    "fullName": "Alice Smith",
    "phoneNumber": "+1111111111"
  }'

# Register User 2
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "bob",
    "email": "bob@example.com",
    "password": "password123",
    "fullName": "Bob Johnson",
    "phoneNumber": "+2222222222"
  }'
```

### 6.2 Test Money Transfer

```bash
# Login as Alice and get token
ALICE_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail": "alice", "password": "password123"}' \
  | jq -r '.data.token')

# Add money to Alice's wallet
curl -X POST http://localhost:8080/api/wallet/add \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ALICE_TOKEN" \
  -d '{
    "amount": 200.00,
    "description": "Initial deposit"
  }'

# Transfer money from Alice to Bob
curl -X POST http://localhost:8080/api/wallet/transfer \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ALICE_TOKEN" \
  -d '{
    "recipientIdentifier": "bob",
    "amount": 50.00,
    "description": "Payment to Bob"
  }'

# Check Alice's transaction history
curl -X GET http://localhost:8080/api/wallet/history \
  -H "Authorization: Bearer $ALICE_TOKEN"
```

## 🐛 Troubleshooting

### Common Issues and Solutions

#### 1. Database Connection Error
```
Error: Connection refused
```
**Solution:** Ensure PostgreSQL is running and credentials are correct.

#### 2. Redis Connection Error
```
Error: Unable to connect to Redis
```
**Solution:** Start Redis server and verify port 6379 is accessible.

#### 3. RabbitMQ Connection Error
```
Error: Connection refused to RabbitMQ
```
**Solution:** Start RabbitMQ server and check port 5672.

#### 4. JWT Token Issues
```
Error: Invalid JWT token
```
**Solution:** Ensure token is properly formatted and not expired.

#### 5. Port Already in Use
```
Error: Port 8080 is already in use
```
**Solution:** Kill the process using port 8080 or change the port in application.yml.

### Check Application Health

```bash
# Check if application is running
curl http://localhost:8080/actuator/health

# Check application logs
tail -f logs/walletwise.log
```

## 📊 Monitoring and Logs

### Application Logs
```bash
# View real-time logs
tail -f logs/walletwise.log

# Search for errors
grep ERROR logs/walletwise.log

# Search for specific user activity
grep "john_doe" logs/walletwise.log
```

### Database Monitoring
```sql
-- Check active connections
SELECT * FROM pg_stat_activity WHERE datname = 'walletwise_db';

-- Check table sizes
SELECT schemaname,tablename,attname,n_distinct,correlation FROM pg_stats;
```

## 🎉 Success Indicators

If everything is working correctly, you should see:

1. ✅ Application starts without errors
2. ✅ Database tables are created automatically
3. ✅ User registration and login work
4. ✅ Wallet operations (add money, transfer) work
5. ✅ Transaction history is recorded
6. ✅ OTP generation and verification work
7. ✅ RabbitMQ messages are sent and received
8. ✅ Redis stores and retrieves OTP data

## 📞 Need Help?

If you encounter issues:

1. Check the application logs
2. Verify all services (PostgreSQL, Redis, RabbitMQ) are running
3. Ensure all configurations are correct
4. Try restarting the application
5. Check the troubleshooting section above

---

**Congratulations! 🎉 Your WalletWise application is now running successfully!**