CREATE DATABASE IF NOT EXISTS products_db;
USE products_db;

CREATE TABLE IF NOT EXISTS products (
    id    INT          AUTO_INCREMENT PRIMARY KEY,
    name  VARCHAR(255) NOT NULL,
    price DOUBLE       NOT NULL
);

INSERT INTO products (name, price) VALUES ('Laptop', 999.99);
INSERT INTO products (name, price) VALUES ('Mouse',   29.99);
INSERT INTO products (name, price) VALUES ('Keyboard', 49.99);
