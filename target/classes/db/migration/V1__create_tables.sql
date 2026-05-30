-- Таблица пользователей
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    login VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Таблица конфигурации OTP
CREATE TABLE IF NOT EXISTS otp_config (
    id BIGSERIAL PRIMARY KEY,
    code_length INT NOT NULL DEFAULT 6,
    code_expiry_minutes INT NOT NULL DEFAULT 5,
    max_attempts INT NOT NULL DEFAULT 3,
    is_active BOOLEAN DEFAULT TRUE,
    updated_at TIMESTAMP
);

-- Таблица OTP-кодов
CREATE TABLE IF NOT EXISTS otp_codes (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    operation_id VARCHAR(100) NOT NULL,
    code VARCHAR(10) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    attempts_left INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    confirmed_at TIMESTAMP,
    confirmed_by_ip VARCHAR(45)
);

-- Индексы
CREATE INDEX idx_operation_id ON otp_codes(operation_id);
CREATE INDEX idx_user_id ON otp_codes(user_id);
CREATE INDEX idx_code ON otp_codes(code);
CREATE INDEX idx_expires_at ON otp_codes(expires_at);