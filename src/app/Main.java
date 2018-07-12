package app;

import java.sql.SQLException;
import java.util.ArrayList;

public class Main {
    public static void main(String args[]) {
        System.out.println("SQL test program.");

        populateIngredients();
    }

    public static void populateIngredients(){
        Ingredient dummyIngredient = Ingredient.getDummy();
        ArrayList<Object> ingredientSource = dummyIngredient.loadFromFile(dummyIngredient.getTable()+".in");

        try{
            DBManager.getInstance().addObjectsFrom(ingredientSource);
        } catch (SQLException sex){
            sex.printStackTrace();
        } catch (ClassNotFoundException cex){
            cex.printStackTrace();
        }
    }
}
