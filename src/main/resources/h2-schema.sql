CREATE TABLE IF NOT EXISTS "UserTable"(
    "uuid" uuid NOT NULL PRIMARY KEY,
    "name" VARCHAR(255),
    "age" int
);


CREATE TABLE IF NOT EXISTS "TransactionTable"(
     "uuid" uuid NOT NULL PRIMARY KEY,
     "src" VARCHAR(255),
     "dst" VARCHAR(255),
     "amount" int
    );