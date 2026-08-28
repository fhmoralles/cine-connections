ALTER TABLE credit
    ADD COLUMN credit_type VARCHAR(20) NOT NULL DEFAULT 'CAST';

ALTER TABLE credit
    ADD CONSTRAINT chk_credit_type
    CHECK (credit_type IN ('CAST', 'CREW'));
