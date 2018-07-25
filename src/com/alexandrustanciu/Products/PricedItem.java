package com.alexandrustanciu.Products;

import com.alexandrustanciu.DB.DBObject;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

abstract class PricedItem implements DBObject {

    protected SimpleStringProperty name = new SimpleStringProperty("Generic PricedItem");
    protected SimpleDoubleProperty unitPrice = new SimpleDoubleProperty(0.);
    protected SimpleIntegerProperty id = new SimpleIntegerProperty(ID_UNUSED);

    protected PricedItem(String name, double unitPrice){
        setName(name);
        setUnitPrice(unitPrice);
    }

    protected PricedItem(String name){
        setName(name);
    }

    public String getName() { return name.getValue(); }

    public void setName(String name) { this.name.set(name);}

    public double getUnitPrice() { return unitPrice.get(); }

    public void setUnitPrice(double unitPrice) { this.unitPrice.set(unitPrice > 0. ? unitPrice : 0.);}

    @Override
    public abstract String getTable();

    @Override
    public abstract void addToDB(Connection con) throws SQLException, ClassNotFoundException;

    public int getId() { return id.get(); }

    @Override
    public abstract boolean canAdd(Connection con) throws SQLException;


    protected PricedItem(){ }

    public void setId(int id) { this.id.set(id > ID_UNUSED ? id : ID_UNUSED); }

    @Override
    public int getIDfromDB(Connection connection) throws SQLException{
        int idToGet = ID_UNUSED;

        String table = getTable();

        if(!table.equals(TABLE_UNUSED)){
            PreparedStatement getID = connection.prepareStatement("SELECT rowid FROM " + table + " WHERE lower(name) = lower(?)");
            getID.setString(1, getName());

            try(ResultSet rs = getID.executeQuery()){
                if(rs.next()){
                    String queryResult = rs.getString(1);
                    if(queryResult.matches("\\d+")){ idToGet = Integer.parseInt(queryResult); }
                }
            }
        }

        return idToGet;
    }
}