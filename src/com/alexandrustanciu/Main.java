package com.alexandrustanciu;

import com.alexandrustanciu.DB.DBManager;
import com.alexandrustanciu.DB.DBObject;
import com.alexandrustanciu.Orders.OrderItem;
import com.alexandrustanciu.Orders.OrderManager;
import com.alexandrustanciu.Orders.OrderSize;
import com.alexandrustanciu.Products.Ingredient;
import com.alexandrustanciu.Products.Pizza;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class Main {
    private static final String DIR_IN = "inputFiles";
    private static final String DIR_SQL = "sql";
    private static final String DB = "main.db";

    public static void main(String args[]){
        System.out.println("Welcome to Pizza Parlor - Java Edition!");

        loadDBItems();

        //TODO remove after testing START
        try{
            Connection con = DBManager.getInstance().getConnection();
            OrderManager.clearAllOrders(con);
        } catch (SQLException ex){
            ex.printStackTrace();
        }

        OrderManager.initOrder();
        OrderManager.addOrderItem(new OrderItem(3,4));
        OrderManager.addOrderItem(new OrderItem(11,2));
        OrderManager.addOrderItem(new OrderItem(2,1));
        OrderManager.addOrderItem(new OrderItem(3,2));
        OrderManager.addOrderItem(new OrderItem(3,2));

        try{
            Connection con = DBManager.getInstance().getConnection();
            OrderManager.submitOrder(con);
        } catch (SQLException ex){
            ex.printStackTrace();
        }

        OrderManager.initOrder();
        OrderManager.addOrderItem(new OrderItem(3,2));
        OrderManager.addOrderItem(new OrderItem(3,2));
        try{
            Connection con = DBManager.getInstance().getConnection();
            OrderManager.submitOrder(con);
        } catch (SQLException ex){
            ex.printStackTrace();
        }

        //TODO remove after testing END
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
