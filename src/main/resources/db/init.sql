-- BookXchange Database Initialization Script
-- This script will be executed when the application starts if using JPA DDL generation

-- If you prefer manual initialization, run these commands:

-- Create ENUM types (for PostgreSQL)
CREATE TYPE user_role_enum AS ENUM ('ADMIN', 'SELLER', 'BUYER');

-- Create Users table
CREATE TABLE IF NOT EXISTS users (
    uid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(255) UNIQUE NOT NULL,
    user_email VARCHAR(255) UNIQUE NOT NULL,
    profile_pic TEXT,
    password_hash VARCHAR(255) NOT NULL,
    user_role user_role_enum NOT NULL DEFAULT 'BUYER',
    date_joined TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Books table
CREATE TABLE IF NOT EXISTS books (
    book_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    book_name VARCHAR(255) NOT NULL,
    book_cover_pic TEXT,
    book_author VARCHAR(255) NOT NULL,
    book_short_preview TEXT,
    book_price NUMERIC(10, 2) NOT NULL,
    quantity_left INTEGER NOT NULL,
    seller_id UUID NOT NULL REFERENCES users(uid) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create BookRequests table
CREATE TABLE IF NOT EXISTS book_requests (
    req_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    req_subject VARCHAR(255) NOT NULL,
    req_description TEXT NOT NULL,
    buyer_id UUID NOT NULL REFERENCES users(uid) ON DELETE CASCADE,
    req_fulfiller_id UUID REFERENCES users(uid) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create SoldBooks table
CREATE TABLE IF NOT EXISTS sold_books (
    sold_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    buyer_id UUID NOT NULL REFERENCES users(uid) ON DELETE CASCADE,
    book_id UUID NOT NULL REFERENCES books(book_id) ON DELETE CASCADE
);

-- Create Indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_books_seller_id ON books(seller_id);
CREATE INDEX IF NOT EXISTS idx_book_requests_buyer_id ON book_requests(buyer_id);
CREATE INDEX IF NOT EXISTS idx_book_requests_fulfiller_id ON book_requests(req_fulfiller_id);
CREATE INDEX IF NOT EXISTS idx_sold_books_buyer_id ON sold_books(buyer_id);
CREATE INDEX IF NOT EXISTS idx_sold_books_book_id ON sold_books(book_id);
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(user_email);

-- Create a default admin user (password: admin123)
-- Note: In production, you should change this password!
INSERT INTO users (uid, username, user_email, profile_pic, password_hash, user_role, date_joined)
VALUES (
    gen_random_uuid(),
    'admin',
    'admin@bookxchange.com',
    NULL,
    '$2a$10$JZLRvw8S7aM6VYY9.JWjZepKo9w9OJ3v5P.w0M8N5kz0Kq5m8JZUq',
    'ADMIN',
    CURRENT_TIMESTAMP
) ON CONFLICT DO NOTHING;

-- Optional: Create a test seller and buyer for development
INSERT INTO users (uid, username, user_email, profile_pic, password_hash, user_role, date_joined)
VALUES (
    gen_random_uuid(),
    'test_seller',
    'seller@test.com',
    NULL,
    '$2a$10$JZLRvw8S7aM6VYY9.JWjZepKo9w9OJ3v5P.w0M8N5kz0Kq5m8JZUq',
    'SELLER',
    CURRENT_TIMESTAMP
) ON CONFLICT DO NOTHING;

INSERT INTO users (uid, username, user_email, profile_pic, password_hash, user_role, date_joined)
VALUES (
    gen_random_uuid(),
    'test_buyer',
    'buyer@test.com',
    NULL,
    '$2a$10$JZLRvw8S7aM6VYY9.JWjZepKo9w9OJ3v5P.w0M8N5kz0Kq5m8JZUq',
    'BUYER',
    CURRENT_TIMESTAMP
) ON CONFLICT DO NOTHING;
