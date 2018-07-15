package app;

import java.sql.SQLException;
import java.util.ArrayList;

public class Main {

    public static void main(String args[]){
        System.out.println("Welcome to Pizza Parlor - Java Edition.");

        DBManager manager;

        try {
            manager = DBManager.getInstance();
            manager.clearDB();
            populateIngredients(manager);
            populatePizzas(manager);

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void populateIngredients(DBManager manager){
        Ingredient dummyIngredient = Ingredient.getGeneric();
        ArrayList<DBObject> ingredientSource = dummyIngredient.loadFromFile(dummyIngredient.getTable()+".in");

        try{
            manager.addDBObjectsFrom(ingredientSource);
        } catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    private static void populatePizzas(DBManager manager){
        Pizza dummyPizza = Pizza.getGeneric();
        ArrayList<DBObject> pizzaSource = dummyPizza.loadFromFile(dummyPizza.getTable()+".in");

        try{
            manager.addDBObjectsFrom(pizzaSource);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
