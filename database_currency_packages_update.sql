SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS payment_sessions;
DROP TABLE IF EXISTS purchase_requests;
DROP TABLE IF EXISTS currency_packages;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE currency_packages (
    id INT AUTO_INCREMENT PRIMARY KEY,
    game_id INT NOT NULL,
    package_name VARCHAR(100) NOT NULL,
    currency_name VARCHAR(50) NOT NULL,
    currency_amount INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_currency_package_game FOREIGN KEY (game_id) REFERENCES games(id)
);

CREATE TABLE purchase_requests (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    currency_package_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    request_status ENUM('PENDING', 'CONFIRMED', 'CANCELLED', 'EXPIRED') NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_purchase_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_purchase_package FOREIGN KEY (currency_package_id) REFERENCES currency_packages(id)
);

CREATE TABLE payment_sessions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    purchase_request_id INT NOT NULL UNIQUE,
    reference_code VARCHAR(20) NOT NULL UNIQUE,
    session_status ENUM('PENDING', 'CONFIRMED', 'CANCELLED', 'EXPIRED') NOT NULL DEFAULT 'PENDING',
    expires_at TIMESTAMP NOT NULL,
    confirmed_at TIMESTAMP NULL,
    cancelled_at TIMESTAMP NULL,
    expired_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment_purchase FOREIGN KEY (purchase_request_id) REFERENCES purchase_requests(id)
);

CREATE TABLE transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    payment_session_id INT NOT NULL UNIQUE,
    transaction_code VARCHAR(30) NOT NULL UNIQUE,
    amount DECIMAL(10,2) NOT NULL,
    transaction_status ENUM('PENDING', 'CONFIRMED', 'CANCELLED', 'EXPIRED') NOT NULL DEFAULT 'PENDING',
    processed_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transaction_payment FOREIGN KEY (payment_session_id) REFERENCES payment_sessions(id)
);

INSERT INTO currency_packages (game_id, package_name, currency_name, currency_amount, price, status)
VALUES
((SELECT id FROM games WHERE title = 'Marvel Rivals'), 'Starter Coins Pack', 'Coins', 500, 4.99, 'ACTIVE'),
((SELECT id FROM games WHERE title = 'Marvel Rivals'), 'Battle Coins Pack', 'Coins', 1200, 9.99, 'ACTIVE'),
((SELECT id FROM games WHERE title = 'Warframe'), 'Tenno Starter Pack', 'Platinum', 170, 9.99, 'ACTIVE'),
((SELECT id FROM games WHERE title = 'Warframe'), 'Tenno Supporter Pack', 'Platinum', 370, 19.99, 'ACTIVE');

INSERT INTO audit_logs (user_id, action_type, description, ip_address, target_type, target_id)
VALUES
(NULL, 'MODEL_UPDATE', 'Purchase flow updated to use in-game currency packages.', '127.0.0.1', 'SYSTEM', NULL);
