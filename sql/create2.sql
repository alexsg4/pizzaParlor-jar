CREATE TABLE IF NOT EXISTS ProductTypes(
    id INTEGER,
    name VARCHAR(25),
    isComposite BOOLEAN NOT NULL DEFAULT FALSE,
    canSell BOOLEAN NOT NULL DEFAULT TRUE,

    PRIMARY KEY(id, name)
);

CREATE TABLE IF NOT EXISTS Products(
    id INTEGER PRIMARY KEY,
    name VARCHAR(25) NOT NULL,
    type INTEGER NOT NULL,
    price REAL CHECK(price > 0.0),
    isVeg BOOLEAN NOT NULL DEFAULT TRUE,

    FOREIGN KEY(type) REFERENCES ProductTypes(id)

);

CREATE TABLE  IF NOT EXISTS  Ingredients(
	id INTEGER PRIMARY KEY,
	name VARCHAR (50) UNIQUE NOT NULL,
	productType INTEGER NOT NULL,
	unitPrice REAL CHECK (unitPrice > 0.0),
	isVeg	BOOLEAN NOT NULL DEFAULT TRUE,
    unit VARCHAR(25),

    FOREIGN KEY(productType) REFERENCES ProductTypes(id)
);

CREATE TABLE  IF NOT EXISTS Recipes(
	productID INTEGER,
	ingredientID INTEGER,
	qty INTEGER DEFAULT 1 CHECK (qty > 0),

	PRIMARY KEY(productID, ingredientID),
	FOREIGN KEY(productID) REFERENCES Products(id),
	FOREIGN KEY(ingredientID)  REFERENCES Ingredients(id)
);
