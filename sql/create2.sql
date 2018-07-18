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
