package app;

import java.sql.SQLException;

public class Main {
    public static void main(String args[]) {
        System.out.println("SQL test program.");

        DBManager manager = null;
        try {
            manager = DBManager.getInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            manager.loadIngredientsFromFile( Ingredient.getDummy().getTable() + ".in");

        } catch (SQLException sex) {
            sex.printStackTrace();
        } catch (ClassNotFoundException cex) {
            cex.printStackTrace();
        }
    }
}
