ALTER TABLE payment_sessions
ADD COLUMN IF NOT EXISTS payment_method VARCHAR(40) NULL AFTER reference_code;

ALTER TABLE payment_sessions
ADD COLUMN IF NOT EXISTS payment_detail_summary VARCHAR(120) NULL AFTER payment_method;

ALTER TABLE payment_sessions
MODIFY COLUMN expires_at DATETIME NOT NULL;
