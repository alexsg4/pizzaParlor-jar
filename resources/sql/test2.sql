-- List

SELECT * FROM "ProductTypes";
SELECT * FROM "Products";
SELECT  * FROM "Ingredients";
SELECT * FROM "Recipes";
SELECT * FROM "Orders";
SELECT * FROM "OrderList";
SELECT * FROM "OrderSizes";

SELECT name FROM sqlite_master WHERE type = 'table';
SELECT * FROM sqlite_master;


-- Count

SELECT count(*) FROM "ProductTypes";
SELECT count(*) FROM "Products";
SELECT count(*) FROM  "Ingredients";
SELECT count(*) FROM "Recipes";

SELECT count(*) FROM "Orders";
SELECT count(*) FROM "OrderList";
SELECT count(*) FROM "OrderSizes";



-- Clear

DELETE FROM "ProductTypes";
DELETE FROM "Products";
DELETE FROM  "Ingredients";
DELETE FROM "Recipes";
DELETE FROM "Orders";
DELETE FROM  "OrderList";
DELETE FROM "OrderSizes";


-- Other

select last_insert_rowid();

