-- List

SELECT * FROM "ProductTypes";
SELECT * FROM "Products";
SELECT  * FROM "Ingredients";
SELECT * FROM "Recipes";


SELECT name FROM sqlite_master WHERE type = 'table';
SELECT * FROM sqlite_master;


-- Count

SELECT count(*) FROM "ProductTypes";
SELECT count(*) FROM "Products";
SELECT  count(*) FROM  "Ingredients";
SELECT count(*) FROM "Recipes";

-- Clear

DELETE FROM "ProductTypes";
DELETE FROM "Products";
DELETE FROM  "Ingredients";
DELETE FROM "Recipes";

-- Other

select last_insert_rowid();

SELECT ROWID from "ProductTypes";
