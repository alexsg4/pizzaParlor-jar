package com.alexandrustanciu.Orders;

import com.alexandrustanciu.DB.DBObject;
import com.alexandrustanciu.Products.Pizza;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderItem implements DBObject {

    private static final String SIZE_TABLE = "OrderSizes";

    private int id = ID_UNUSED;
    private int orderID = ID_UNUSED;
    private int productID = ID_UNUSED;
    private int sizeID = ID_UNUSED;

    @Override
    public String getTable() { return "OrderList"; }

    public boolean canAdd(Connection con) throws SQLException{

        //Check if there is a valid product for this order.
        if(con!=null){
            String productTable = Pizza.getGeneric().getTable();
            String orderTable = Order.getGeneric().getTable();

            PreparedStatement ps = con.prepareStatement(
                    "SELECT COUNT(*) FROM " + productTable + " WHERE id = ?;"
            );
            ps.setInt(1, productID);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return (rs.getInt(1) == 0);
                }
            }
        }
        return false;
    }

    @Override
    public int getDBID(Connection connection) {
        return ID_UNUSED;
    }

    @Override
    public void addToDB(Connection connection) throws SQLException {
        if(canAdd(connection)){
            //DO the stuff
        }
    }

    @Override
    public DBObject buildFromID(Connection con, int id) {
        DBObject toBuild = null;



        return toBuild;
    }

    public void setOrderID(int orderID){
        if(orderID > ID_UNUSED){
            this.orderID = orderID;
        }
    }

    private double getProductPrice(Connection con) throws SQLException{
        double priceToGet = 0.;
        String table = Pizza.getGeneric().getTable();

        PreparedStatement ps = con.prepareStatement("SELECT price FROM " + table + " WHERE id = ?;");
        ps.setInt(1, this.productID);

        try (ResultSet rs = ps.executeQuery()) {
            if(rs.next()){
                priceToGet = rs.getDouble(1);
            }
        }

        return priceToGet;
    }

    private double getSizeMultiplier(Connection con) throws SQLException{
        double sizeMulti = 0.;
        String table = SIZE_TABLE;

        PreparedStatement ps = con.prepareStatement("SELECT priceMod FROM " + table + " WHERE id = ?;");
        ps.setInt(1, this.sizeID);

        try (ResultSet rs = ps.executeQuery()) {
            if(rs.next()){
                sizeMulti = rs.getDouble(1);
            }
        }

        return sizeMulti;
    }


}
