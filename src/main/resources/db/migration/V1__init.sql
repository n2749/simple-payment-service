CREATE TABLE clients (
    client_id VARCHAR(64) PRIMARY KEY,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE payments (
    payment_id UUID PRIMARY KEY,
    amount NUMERIC(19, 2) NOT NULL CHECK (amount > 0),
    currency VARCHAR(3) NOT NULL CHECK (currency IN ('KZT', 'USD', 'EUR', 'RUB', 'CNY')),
    description VARCHAR(512),
    client_id VARCHAR(64) NOT NULL REFERENCES clients (client_id),
    status VARCHAR(16) NOT NULL CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELED')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_payments_client_id ON payments (client_id);
