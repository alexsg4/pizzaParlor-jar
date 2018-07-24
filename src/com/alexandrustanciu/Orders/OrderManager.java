package com.alexandrustanciu.Orders;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OrderManager {

    private static Order currentOrder = Order.getGeneric();

    private static OrderManager ourInstance = new OrderManager();

    private OrderManager() { }

    //TODO remove
   /*public static OrderManager getInstance() {
        return ourInstance;
    }*/

    public static void initOrder() { currentOrder = Order.getGeneric(); }

    public static void addOrderItem(OrderItem toAdd){
        currentOrder.addOrderItem(toAdd);
    }

    public static void submitOrder(Connection con){
        try{
            currentOrder.addToDB(con);
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    public static void clearOrder() {
        if (currentOrder != null) {
            currentOrder.clear();
        }
    }

    public static void clearAllOrders(Connection con){
        String orderListTable = OrderItem.getGeneric().getTable();
        String orderTable = Order.getGeneric().getTable();

        try{
            PreparedStatement deleteOrders = con.prepareStatement("DELETE FROM " + orderTable + ";");
            deleteOrders.execute();
        } catch (SQLException ex){ ex.printStackTrace(); }

        try{
            PreparedStatement deleteList = con.prepareStatement("DELETE FROM " + orderListTable + ";");
            deleteList.execute();
        } catch (SQLException ex){ ex.printStackTrace(); }
    }
}
