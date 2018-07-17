-- List

SELECT * FROM "ProductTypes";
SELECT * FROM "Products";
SELECT  * FROM "Ingredients";
SELECT * FROM "Recipes";


SELECT name FROM sqlite_master WHERE type = 'table';
SELECT * FROM sqlite_master;


-- Clear

SELECT count(*) FROM "ProductTypes";
SELECT count(*) FROM "Products";
SELECT  count(*) FROM  "Ingredients";
SELECT count(*) FROM "Recipes";

SELECT ROWID, * FROM "Recipes";