package com.alexandrustanciu.Products;

import com.alexandrustanciu.DB.DBObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

abstract class PricedItem implements DBObject {

    @Override
    public abstract String getTable();

    @Override
    public abstract void addToDB(Connection con) throws SQLException, ClassNotFoundException;

    @Override
    public int getDBID(Connection connection) throws SQLException{
        int idToGet = ID_UNUSED;

        String table = getTable();

        if(!table.equals(TABLE_UNUSED)){
            PreparedStatement getID = connection.prepareStatement("SELECT rowid FROM " + table + " WHERE lower(name) == lower(?)");
            getID.setString(1, this.name);

            try(ResultSet rs = getID.executeQuery()){
                if(rs.next()){
                    String queryResult = rs.getString(1);
                    if(queryResult.matches("\\d+")){ idToGet = Integer.parseInt(queryResult); }
                }
            }
        }

        return idToGet;
    }

    @Override
    public abstract boolean canAdd(Connection con) throws SQLException;

    protected String name = "Generic PricedItem";
    protected double unitPrice = 0;
    protected int id = ID_UNUSED;

    protected PricedItem(){ }

    protected PricedItem(String name, double unitPrice){
        this.name = name;
        setUnitPrice(unitPrice);
    }

    protected PricedItem(String name){
        this.name = name;
        unitPrice = 0.;
    }

    public String getName() { return name; }

    public double getUnitPrice() { return unitPrice; }

    protected void setUnitPrice(double priceToSet){
        if(priceToSet > 0) {
            unitPrice = priceToSet;
        }
        else{
            unitPrice = 0;
        }
    }
}