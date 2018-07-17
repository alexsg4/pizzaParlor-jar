package app;

import java.sql.SQLException;
import java.util.ArrayList;

public class Main {
    private static final String DIR_IN = "inputFiles";
    private static final String DIR_SQL = "sql";
    private static final String DB = "main.db";

    public static void main(String args[]){
        System.out.println("Welcome to Pizza Parlor - Java Edition.");

        try {
            DBManager manager = DBManager.getInstance();
            manager.clearDB();
            loadPizzaIngredients(manager);
            loadPizzas(manager);

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void loadPizzaIngredients(DBManager manager){
        Ingredient dummyIngredient = Ingredient.getGeneric();
        String path = DIR_IN + "/" + dummyIngredient.getTable() + ".in";
        ArrayList<DBObject> ingredientSource = dummyIngredient.loadFromFile(path);

        try{
            manager.addDBObjectsFrom(ingredientSource);
        } catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    private static void loadPizzas(DBManager manager){
        Pizza dummyPizza = Pizza.getGeneric();
        String path = DIR_IN + "/" + dummyPizza.getTable() + ".in";
        ArrayList<DBObject> pizzaSource = dummyPizza.loadFromFile(path);

        try{
            manager.addDBObjectsFrom(pizzaSource);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
