CREATE TABLE  IF NOT EXISTS Pizza(
	pizzaID INTEGER PRIMARY KEY,
	name VARCHAR(50),
	price REAL,
	isVeg BOOLEAN,
	CHECK (price > 0.0)
);

CREATE TABLE  IF NOT EXISTS  Ingredients(
	ingID	 INTEGER PRIMARY KEY,
	name VARCHAR (50) UNIQUE NOT NULL,
	unitPrice REAL,
	isVeg	BOOLEAN,
    unit VARCHAR(25),
	CHECK (unitPrice > 0.0)
);

CREATE TABLE  IF NOT EXISTS  Recipe(
	recID INTEGER PRIMARY KEY,
	pizzaID INTEGER NOT NULL,
	ingID	 INTEGER NOT NULL,
	qty INTEGER DEFAULT 1,
	CHECK (qty > 0),
	FOREIGN KEY(pizzaID)  REFERENCES Pizza(pizzaID),
	FOREIGN KEY(ingID)  REFERENCES Ingredients(ingID)
);

--testing only

-- List

select * from "Ingredients";
select * from "Pizza";
select * from "Recipe";

-- Clear

select count(*) from "Ingredients";
select count(*) from "Pizza";
select count(*) from "Recipe";

-- Count

delete from "Ingredients";
delete from "Pizza";
delete from "Recipe";

select * from sqlite_master;

