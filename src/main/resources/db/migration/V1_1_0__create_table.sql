CREATE TABLE IF NOT EXISTS month_amount
(
    user_id        UUID         NOT NULL,
    given_name      varchar(255) NOT NULL,
    picture        varchar(255) NOT NULL,
    email          varchar(255) NOT NULL UNIQUE,
    PRIMARY KEY (user_id)
);



CREATE TABLE IF NOT EXISTS financial_model
(
    transaction_id   UUID         NOT NULL,
    title            varchar(255) NOT NULL,
    amount           DECIMAL      NOT NULL,
    transaction_type varchar(255),
    locale_date      DATE         NOT NULL,
    user_id          UUID,
    FOREIGN KEY (user_id) REFERENCES month_amount (user_id) ON DELETE CASCADE
);



