package com.alexandrustanciu.Orders;

import com.alexandrustanciu.DB.DBManager;
import com.alexandrustanciu.DB.DBObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Order implements DBObject {
    private int id = ID_UNUSED;
    private double value = 0.0;
    private ArrayList<OrderItem> items = new ArrayList<>();

    @Override
    public String getTable() {
        return "Orders";
    }

    static Order getGeneric(){ return new Order(); }

    //NOT USED HERE
    @Override
    public int getIDfromDB(Connection connection) {
        return this.id;
    }

    @Override
    public boolean canAdd(Connection con) {
        return (con!=null && !items.isEmpty());
    }

    @Override
    public void addToDB(Connection con) throws SQLException{
        if (canAdd(con)) {
            String table = getTable();

            //Add order with value
            if(con.isClosed()){
                con = DBManager.getInstance().getConnection();
            }

            PreparedStatement addStatement = con.prepareStatement(
                    "INSERT INTO " + table + "(value) VALUES(?);"
            );
            addStatement.setDouble(1, this.value);
            addStatement.execute();

            //Get ID for newly added order
            int id = ID_UNUSED;
            PreparedStatement ps = con.prepareStatement(
                    "SELECT id FROM " + table + " WHERE value = ? ORDER BY id DESC LIMIT 1;"
            );
            ps.setDouble(1, this.value);

            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    id = rs.getInt(1);
                }
            }

            //Add order items to DB if add was successful
            if (id > ID_UNUSED) {
                for (OrderItem item : items) {
                    OrderItem itemToAdd = OrderItem.getGeneric();
                    itemToAdd.setOrderID(id);
                    itemToAdd.setProductID(item.getProductID());
                    itemToAdd.setSizeID(item.getSizeID());

                    itemToAdd.addToDB(con);
                }
            }

        }
    }

    //TODO implement
    @Override
    public DBObject buildFromID(Connection con, int id) {
        Order toBuild = null;

        return toBuild;
    }

    //TODO handle duplicate order items
    void addOrderItem(OrderItem toAdd){
        this.items.add(toAdd);
        try(Connection con = DBManager.getInstance().getConnection()){
            this.value += toAdd.getValue(con);
        } catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    void clear(){ this.items.clear(); }
}
