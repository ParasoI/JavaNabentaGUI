USE nabenta;

CREATE TABLE Grocery(
	grocery_barcode BIGINT AUTO_INCREMENT,
    grocery_itemName VARCHAR(255) UNIQUE,
    grocery_price INT NOT NULL,
    grocery_category VARCHAR(255) DEFAULT 'general',
    grocery_description VARCHAR(255),
    grocery_stocksLeft INT DEFAULT 0,
    PRIMARY KEY(grocery_barcode)
    );
    
CREATE TABLE Receipt(
	receipt_id BIGINT AUTO_INCREMENT,
    receipt_date TIMESTAMP DEFAULT (NOW()),
    receipt_discount DOUBLE DEFAULT 0.00,
    receipt_total DOUBLE NOT NULL,
    receipt_cashTendered INT NOT NULL,
    receipt_cashier INT,
    PRIMARY KEY(receipt_id)
    );
    
CREATE TABLE Employee(
	employee_id INT AUTO_INCREMENT,
    employee_firstName VARCHAR(255) NOT NULL,
    employee_lastName VARCHAR(255) NOT NULL,
    employee_userName VARCHAR(255) NOT NULL,
    employee_userPass VARCHAR(255) NOT NULL,
    employee_type VARCHAR(255) DEFAULT 'cashier',
    employee_birthDate DATE NOT NULL,
    employee_sex VARCHAR(255),
    employee_email VARCHAR(255),
    employee_address VARCHAR(255) NOT NULL,
    employee_picture BLOB,
    PRIMARY KEY(employee_id)
	);

ALTER TABLE Receipt
ADD FOREIGN KEY(receipt_cashier) REFERENCES Employee(employee_id);

CREATE TABLE Supply(
	supply_barcode BIGINT,
    supply_date TIMESTAMP DEFAULT (NOW()),
    supply_name VARCHAR(255),
    supply_quantity INT NOT NULL,
    supply_admin INT NOT NULL,
    PRIMARY KEY(supply_barcode,supply_date),
    FOREIGN KEY(supply_barcode) REFERENCES Grocery(grocery_barcode),
    FOREIGN KEY(supply_admin) REFERENCES Employee(employee_id)
);

CREATE TABLE Orders(
	orders_id BIGINT AUTO_INCREMENT,
    orders_barcode BIGINT NOT NULL,
    orders_quantity INT NOT NULL,
    PRIMARY KEY(orders_id,orders_barcode),
    FOREIGN KEY(orders_barcode) REFERENCES Grocery(grocery_barcode)
);
