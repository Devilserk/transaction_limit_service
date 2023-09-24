CREATE TABLE transactions (
	id BIGSERIAL PRIMARY KEY,
	account_from BIGINT NOT NULL,
    account_to BIGINT NOT NULL,
	currency_shortname VARCHAR(3) NOT NULL,
	sum DECIMAL(10, 2) NOT NULL,
	expense_category VARCHAR(255) NOT NULL,
	datetime TIMESTAMPTZ NOT NULL,
	CONSTRAINT check_account_from_length CHECK (LENGTH(CAST(account_from AS TEXT)) = 10),
	CONSTRAINT check_account_to_length CHECK (LENGTH(CAST(account_to AS TEXT)) = 10)
);

CREATE TABLE limits (
    id BIGSERIAL PRIMARY KEY,
    account_number BIGINT NOT NULL,
    limit_sum DECIMAL(10, 2) NOT NULL,
    limit_datetime TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    limit_currency_shortname VARCHAR(3) NOT NULL,
    remainder DECIMAL(10, 2) NOT NULL,
    limit_exceeded BOOLEAN DEFAULT FALSE,
    expense_category VARCHAR(255) NOT NULL,
    version BIGINT DEFAULT 0,
    CONSTRAINT check_account_number_length CHECK (LENGTH(CAST(account_number AS TEXT)) = 10)
);
