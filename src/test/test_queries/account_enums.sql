ALTER TABLE accounts
    ADD CONSTRAINT account_type_check
        CHECK (account_type IN ('SAVINGS', 'INVESTMENTS', 'CURRENT', 'RECURRING', 'FIXED_DEPOSIT', 'NRI'));

CREATE TYPE account_type_enum AS ENUM (
    'SAVINGS',
    'INVESTMENTS',
    'CURRENT',
    'RECURRING',
    'FIXED_DEPOSIT',
    'NRI'
    );

ALTER TABLE accounts
    ADD COLUMN account_type account_type_enum NOT NULL DEFAULT 'SAVINGS';

drop table accounts;


