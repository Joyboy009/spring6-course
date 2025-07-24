-- WalletWise Database Schema
-- PostgreSQL Database Schema

-- Create database (run this separately)
-- CREATE DATABASE walletwise_db;

-- Connect to walletwise_db and run the following:

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(15),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

-- Wallets table
CREATE TABLE IF NOT EXISTS wallets (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    balance DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    wallet_number VARCHAR(50) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

-- Transactions table
CREATE TABLE IF NOT EXISTS transactions (
    id BIGSERIAL PRIMARY KEY,
    sender_wallet_id BIGINT REFERENCES wallets(id) ON DELETE SET NULL,
    receiver_wallet_id BIGINT REFERENCES wallets(id) ON DELETE SET NULL,
    amount DECIMAL(19,2) NOT NULL,
    transaction_type VARCHAR(20) NOT NULL CHECK (transaction_type IN ('CREDIT', 'DEBIT', 'TRANSFER')),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED', 'CANCELLED')),
    transaction_reference VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for better performance
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_wallets_user_id ON wallets(user_id);
CREATE INDEX IF NOT EXISTS idx_wallets_wallet_number ON wallets(wallet_number);
CREATE INDEX IF NOT EXISTS idx_transactions_sender_wallet_id ON transactions(sender_wallet_id);
CREATE INDEX IF NOT EXISTS idx_transactions_receiver_wallet_id ON transactions(receiver_wallet_id);
CREATE INDEX IF NOT EXISTS idx_transactions_reference ON transactions(transaction_reference);
CREATE INDEX IF NOT EXISTS idx_transactions_created_at ON transactions(created_at);

-- Sample data (optional)
-- INSERT INTO users (username, email, password, full_name, phone_number) VALUES
-- ('john_doe', 'john@example.com', '$2a$10$example_hashed_password', 'John Doe', '+1234567890'),
-- ('jane_smith', 'jane@example.com', '$2a$10$example_hashed_password', 'Jane Smith', '+1234567891');

-- Note: The application will automatically create wallets when users register