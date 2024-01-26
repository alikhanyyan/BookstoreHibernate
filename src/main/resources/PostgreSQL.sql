CREATE DATABASE bookstore_db;

\c bookstore_db;

CREATE TABLE books (
	book_id SERIAL PRIMARY KEY,
	title TEXT NOT NULL,
	author VARCHAR(40) NOT NULL,
	genre VARCHAR(30) NOT NULL,
	price REAL NOT NULL CHECK(price > 0),
	quantity_in_stock INTEGER NOT NULL CHECK(quantity_in_stock >= 0)
);

INSERT INTO books(title, author, genre, price, quantity_in_stock)
    VALUES ('Don Quixote', 'Miguel de Cervantes', 'Action and Adventure', 3.50, 20),
           ('On the Road', 'Jack Kerouac', 'Autobiographical', 4.0, 15),
           ('Alice''s Adventures in Wonderland', 'Lewis Carroll', 'Fantasy', 3.80, 8),
           ('A Tale of Two Cities', 'Charles Dickens', 'Historical', 5.0, 11),
           ('Frankenstein, or, the Modern Prometheus', 'Mary Shelley', 'Horror', 2.90, 10),
           ('The Adventures of Tom Sawyer', 'Mark Twain', 'Action and Adventure', 3.60, 23),
           ('The Complete Sherlock Holmes', 'Arthur Conan Doyle', 'Mystery/Detective', 4.90, 4),
           ('The Hobbit, or, There and Back Again', 'J.R.R. Tolkien', 'Fantasy', 3.0, 9),
           ('The Three Musketeers', 'Alexandre Dumas', 'Historical', 4.50, 17),
           ('Robinson Crusoe', 'Daniel Defoe', 'Action and Adventure', 2.90, 3);

CREATE TABLE customers (
	customer_id SERIAL PRIMARY KEY,
	name VARCHAR(20) NOT NULL,
	email VARCHAR(60) UNIQUE,
	phone VARCHAR(20) NOT NULL
);

INSERT INTO customers(name, email, phone)
    VALUES ('Cian Gross', 'ciangross@gmail.com', '+1287569430'),
           ('Danyal Cochran', 'danyalcochran@gmail.com', '+7812694587'),
           ('Annabella Blaese', 'annabellablaese@gmail.com', '+37491256848'),
           ('Nicola Simmons', 'nicolasimmons@gmail.com', '+3185236974512'),
           ('Mia Bailey', 'miabailey@gmail.com', '+34975066468');

CREATE TABLE sales (
	sale_id SERIAL PRIMARY KEY,
	book_id INTEGER NOT NULL,
	customer_id INTEGER NOT NULL,
	date_of_sale DATE,
	quantity_sold INTEGER NOT NULL CHECK(quantity_sold >= 0),
    total_price REAL NOT NULL CHECK(total_price >= 0),
    CONSTRAINT fk_book FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE SET NULL,
    CONSTRAINT fk_customer FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE SET NULL
);

INSERT INTO sales(book_id, customer_id, date_of_sale, quantity_sold, total_price)
    VALUES (1, 4, '2023-01-01', 1, 3.50),
           (9, 3, '2023-08-19', 2, 9.0),
           (5, 2, '2022-11-30', 1, 2.90),
           (7, 1, '2022-12-21', 1, 4.90),
           (2, 3, '2023-10-05', 2, 8.0),
           (10, 3, '2023-10-05', 1, 2.90);

CREATE OR REPLACE FUNCTION update_quantity_in_stock()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE books
    SET quantity_in_stock = quantity_in_stock - NEW.quantity_sold
    WHERE book_id = NEW.book_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_quantity_trigger
AFTER INSERT ON sales
FOR EACH ROW
EXECUTE FUNCTION update_quantity_in_stock();