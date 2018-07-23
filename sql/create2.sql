CREATE TABLE IF NOT EXISTS ProductTypes(
    id INTEGER PRIMARY KEY,
    name VARCHAR(25) UNIQUE NOT NULL,
    isComposite BOOLEAN NOT NULL DEFAULT FALSE,
    canSell BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS Products(
    id INTEGER PRIMARY KEY,
    name VARCHAR(25) NOT NULL,
    type INTEGER NOT NULL CHECK(type > 0),
    price REAL CHECK(price > 0.0),
    isVeg BOOLEAN NOT NULL DEFAULT TRUE,

    FOREIGN KEY(type) REFERENCES ProductTypes(id)

);

CREATE TABLE  IF NOT EXISTS  Ingredients(
	id INTEGER PRIMARY KEY,
	name VARCHAR (50) UNIQUE NOT NULL,
	productType INTEGER NOT NULL CHECK (productType > 0),
	unitPrice REAL CHECK (unitPrice > 0.0),
	isVeg	BOOLEAN NOT NULL DEFAULT TRUE,
    unit VARCHAR(25) DEFAULT 'g',

    FOREIGN KEY(productType) REFERENCES ProductTypes(id)
);

CREATE TABLE  IF NOT EXISTS Recipes(
	productID INTEGER CHECK (productID > 0),
	ingredientID INTEGER CHECK (ingredientID > 0),
	qty INTEGER DEFAULT 1 CHECK (qty > 0),

	PRIMARY KEY(productID, ingredientID),
	FOREIGN KEY(productID) REFERENCES Products(id),
	FOREIGN KEY(ingredientID)  REFERENCES Ingredients(id)
);

CREATE TABLE IF NOT EXISTS OrderSizes(
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL,
	priceMod REAL DEFAULT 1.0 CHECK (priceMod >= 1.0)
);

CREATE TABLE IF NOT EXISTS Orders(
    id INTEGER PRIMARY KEY,
	value REAL DEFAULT 0 CHECK (value >= 0.0)
);

CREATE TABLE  IF NOT EXISTS OrderList(
    id INTEGER PRIMARY KEY,
    orderID INTEGER NOT NULL CHECK(orderID > 0),
    productID INTEGER NOT NULL CHECK(productID > 0),
	size INTEGER NOT NULL CHECK(size > 0),

	FOREIGN KEY(orderID) REFERENCES Orders(id),
	FOREIGN KEY(productID) REFERENCES Products(id),
    FOREIGN KEY(size) REFERENCES Sizes(id)
);


