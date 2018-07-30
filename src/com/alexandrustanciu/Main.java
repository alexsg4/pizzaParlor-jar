package com.alexandrustanciu;

import com.alexandrustanciu.DB.DBManager;
import com.alexandrustanciu.DB.DBObject;
import com.alexandrustanciu.Orders.OrderSize;
import com.alexandrustanciu.Products.Ingredient;
import com.alexandrustanciu.Products.Pizza;
import com.alexandrustanciu.UControls.ScreenController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class Main extends Application {
    private static final String DIR_IN = "inputFiles";
    private static final String DIR_SQL = "sql";
    private static final String DIR_RES = "resources";
    private static final String DIR_VIEW = "UViews";
    private static final String DIR_CTRL = "UControllers";
    private static final String DB = "main.db";

    private static final String APP_TITLE = "Pizza Parlor - Java Edition";

    public static void main(String args[]){
        System.out.println("Welcome to " + APP_TITLE + "!");

        loadDBItems();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource(DIR_VIEW + "/FMain.fxml"));
        root.getStylesheets().add("/css/mainControls.css");

        primaryStage.setTitle(APP_TITLE);
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(400);
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();

        //TODO find and load all screens
        ScreenController controller = ScreenController.getInstance();

        controller.loadScreen("Overview", "../" + DIR_VIEW + "/FOverview.fxml");
        controller.loadScreen("Ingredients", "../" + DIR_VIEW + "/FIngredients.fxml");
        controller.loadScreen("Menu", "../" + DIR_VIEW + "/FMenu.fxml");
        controller.loadScreen("Orders", "../" + DIR_VIEW + "/FOrders.fxml");
    }

    private static void loadPizzaIngredients(DBManager manager){
        Ingredient dummyIngredient = Ingredient.getGeneric();
        String path = DIR_IN + "/PizzaIngredients.in";
        Pizza dummyPizza = Pizza.getGeneric();
        int typeId = dummyPizza.getType();

        //TODO add ingredient type in file
        ArrayList<DBObject> ingredientSource = dummyIngredient.loadFromFile(path);

        for(int i=0; i<ingredientSource.size(); i++){
            Ingredient ingToAdd = (Ingredient)ingredientSource.get(i);
            ingToAdd.setProductType(typeId);

            try{
                manager.addDBObject(ingToAdd);
            } catch (SQLException | ClassNotFoundException e){
                e.printStackTrace();
            }

        }
    }

    private static void loadPizzas(DBManager manager){
        Pizza dummyPizza = Pizza.getGeneric();
        String path = DIR_IN + "/" + dummyPizza.getTypeName() + ".in";
        ArrayList<DBObject> pizzaSource = dummyPizza.loadFromFile(path);

        try{
            manager.addDBObjectsFrom(pizzaSource);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void loadSizes(DBManager manager){
        OrderSize dummy = OrderSize.getGeneric();
        String path = DIR_IN + "/" + dummy.getTable() + ".in";
        ArrayList<DBObject> sizesSource = dummy.loadFromFile(path);

        try{
            manager.addDBObjectsFrom(sizesSource);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void loadDBItems() {
        try {
            DBManager manager = DBManager.getInstance();
            loadPizzaIngredients(manager);
            loadPizzas(manager);
            loadSizes(manager);

        } catch (SQLException  e) {
            e.printStackTrace();
        }
    }

}
