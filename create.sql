CREATE TABLE  IF NOT EXISTS   Pizza(
	pizzaID INTEGER PRIMARY KEY,
	name VARCHAR(50),
	price REAL,
	recID INTEGER NOT NULL,
	isVeg BOOLEAN,
	CHECK (price > 0.0)
);

CREATE TABLE  IF NOT EXISTS  Ingredients(
	ingID	 INTEGER PRIMARY KEY,
	name VARCHAR (50) UNIQUE NOT NULL,
	unitPrice REAL,
	isVeg	BOOLEAN,
	CHECK (unitPrice > 0.0)
);

CREATE TABLE  IF NOT EXISTS  Recipe(
	recID INTEGER PRIMARY KEY,
	pizzaID INTEGER NOT NULL,
	ingID	 INTEGER NOT NULL,
	qty INTEGER DEFAULT 0,
	CHECK (qty > 0),
	FOREIGN KEY(pizzaID)  REFERENCES Pizza(pizzaID),
	FOREIGN KEY(ingID)  REFERENCES Ingredients(ingID)
);

--testing only

select * from "Ingredients";

delete from "Ingredients";

select count(*) from "Ingredients";


