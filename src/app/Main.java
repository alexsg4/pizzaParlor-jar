package app;

import java.sql.SQLException;
import java.util.ArrayList;

public class Main {
    public static void main(String args[]){
        System.out.println("SQL test program.");

        try {
            DBManager.getInstance().clearDB();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        populateIngredients();
        populatePizzas();
    }

    private static void populateIngredients(){
        Ingredient dummyIngredient = Ingredient.getGeneric();
        ArrayList<Object> ingredientSource = dummyIngredient.loadFromFile(dummyIngredient.getTable()+".in");

        try{
            DBManager.getInstance().addObjectsFrom(ingredientSource);
        } catch (SQLException sex){
            sex.printStackTrace();
        } catch (ClassNotFoundException cex){
            cex.printStackTrace();
        }
    }

    private static void populatePizzas(){
        Pizza dummyPizza = Pizza.getGeneric();
        ArrayList<Object> pizzaSource = dummyPizza.loadFromFile(dummyPizza.getTable()+".in");

        try{
            DBManager.getInstance().addObjectsFrom(pizzaSource);
        } catch (SQLException sex){
            sex.printStackTrace();
        } catch (ClassNotFoundException cex){
            cex.printStackTrace();
        }
    }

}
