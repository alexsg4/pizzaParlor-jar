package com.alexandrustanciu.Orders;

import com.alexandrustanciu.DB.DBManager;
import com.alexandrustanciu.DB.DBObject;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Order implements DBObject {

    private SimpleIntegerProperty id = new SimpleIntegerProperty(ID_UNUSED);
    private SimpleDoubleProperty value = new SimpleDoubleProperty();
    private ArrayList<OrderItem> items = new ArrayList<>();

    public int getId(){ return id.get(); }

    public void setId(double value){ this.value.set(value > 0. ? value : 0.);}

    public void setId(int id){ this.id.set(id > ID_UNUSED ? id : ID_UNUSED);}

    public double getValue(){ return value.get(); }

    @Override
    public String getTable() {
        return "Orders";
    }

    static Order getGeneric(){ return new Order(); }

    //NOT USED HERE
    @Override
    public int getIDfromDB(Connection connection) {
        return getId();
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
            addStatement.setDouble(1, getValue());
            addStatement.execute();

            //Get ID for newly added order
            int id = ID_UNUSED;
            PreparedStatement ps = con.prepareStatement(
                    "SELECT id FROM " + table + " WHERE value = ? ORDER BY id DESC LIMIT 1;"
            );
            ps.setDouble(1, getValue());

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
            double valueToAdd = toAdd.getValue(con);
            value.set(getValue() + valueToAdd);
        } catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    void clear(){ this.items.clear(); }
}
