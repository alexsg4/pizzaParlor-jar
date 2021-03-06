package com.alexandrustanciu.Orders;

import com.alexandrustanciu.DB.DBObject;
import com.alexandrustanciu.Products.Pizza;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderItem implements DBObject {

    static final String SIZE_TABLE = "OrderSizes";

    private int id = ID_UNUSED;
    private int orderID = ID_UNUSED;
    private int productID = ID_UNUSED;
    private int sizeID = ID_UNUSED;

    @Override
    public String getTable() { return "OrderList"; }

    OrderItem(){ }

    public OrderItem(int productID, int sizeID){
        setProductID(productID);
        setSizeID(sizeID);
    }

    OrderItem(int id, int orderID, int productID, int sizeID){
        setID(id);
        setOrderID(orderID);
        setProductID(productID);
        setSizeID(sizeID);
    }

    static OrderItem getGeneric(){ return new OrderItem(); }

    public boolean canAdd(Connection con) throws SQLException{

        //Check if there is a valid product for this order.
        if(con!=null){

            //check product exists
            String productTable = Pizza.getGeneric().getTable();
            PreparedStatement checkProductID = con.prepareStatement(
                    "SELECT COUNT(*) FROM " + productTable + " WHERE id = ?;"
            );
            checkProductID.setInt(1, productID);
            try(ResultSet rs = checkProductID.executeQuery()){
                if(rs.next()){
                    if(rs.getInt(1) == 0){
                        return false;
                    }
                }
            }

            //check order exists
            String orderTable = Order.getGeneric().getTable();
            PreparedStatement checkOrderID = con.prepareStatement(
                    "SELECT COUNT(*) FROM " + orderTable + " WHERE id = ?;"
            );
            checkOrderID.setInt(1, orderID);
            try(ResultSet rs = checkOrderID.executeQuery()){
                if(rs.next()){
                    if(rs.getInt(1) == 0){
                        return false;
                    }
                }
            }

            String sizeTable = SIZE_TABLE;
            PreparedStatement checkSize = con.prepareStatement(
                    "SELECT COUNT(*) FROM " + sizeTable + " WHERE id = ?;"
            );
            checkSize.setInt(1, sizeID);
            try(ResultSet rs = checkSize.executeQuery()){
                if(rs.next()){
                    if(rs.getInt(1) == 0){
                        return false;
                    }
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public int getIDfromDB(Connection connection) throws SQLException {
        int IDtoGet = ID_UNUSED;
        String table = this.getTable();
        PreparedStatement getID = connection.prepareStatement(
          "SELECT id FROM " + table + " WHERE orderID = ? AND productID = ? AND size = ?;"
        );
        getID.setInt(1, this.orderID);
        getID.setInt(2, this.productID);
        getID.setInt(3,this.sizeID);

        try(ResultSet rs = getID.executeQuery()) {
            if(rs.next()){
                IDtoGet = rs.getInt(1);
            }
        }

        return IDtoGet;
    }

    @Override
    public void addToDB(Connection connection) throws SQLException {
        if(canAdd(connection)){
            String table = getTable();
            PreparedStatement addStatement = connection.prepareStatement(
                    "INSERT INTO " + table + " (orderID, productID, size) VALUES(?, ?, ?);"
            );
            addStatement.setInt(1, this.orderID);
            addStatement.setInt(2, this.productID);
            addStatement.setInt(3,this.sizeID);

            addStatement.execute();
        }
    }

    void setID(int id) {
        if(id > ID_UNUSED){
            this.id = id;
        }
    }

    void setOrderID(int orderID){
        if(orderID > ID_UNUSED){
            this.orderID = orderID;
        }
    }

    int getProductID(){
        return this.productID;
    }

    void setProductID(int productID){
        if(productID > ID_UNUSED){
            this.productID = productID;
        }
    }

    int getSizeID(){
        return this.sizeID;
    }

    void setSizeID(int sizeID){
        if(sizeID > ID_UNUSED) {
            this.sizeID = sizeID;
        }
    }

    @Override
    public DBObject buildFromID(Connection con, int id) throws SQLException {
        OrderItem toBuild = null;

        if(con != null && id > ID_UNUSED) {
            String table = getTable();
            PreparedStatement buildStatement = con.prepareStatement(
                    "SELECT * FROM " + table + " WHERE id = ?;"
            );
            buildStatement.setInt(1, id);
            try (ResultSet rs = buildStatement.executeQuery()){
                while (rs.next()){
                    toBuild = new OrderItem(
                            rs.getInt("id"),
                            rs.getInt("orderID"),
                            rs.getInt("productID"),
                            rs.getInt("size")
                    );
                }
            }

        }
        return toBuild;
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

    double getValue(Connection con){

        double valueToGet = 0.;
        double price = 0.;
        double multi = 1.;
        if(con!= null){
            try{
                price = getProductPrice(con);
                multi = getSizeMultiplier(con);
                valueToGet = price * multi;

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return valueToGet;
    }

}
