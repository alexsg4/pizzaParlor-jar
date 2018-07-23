package com.alexandrustanciu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Order implements DBObject {
    private int id = ID_UNUSED;
    private double value = 0.0;

    //private ArrayList<Product> Items;

    @Override
    public String getTable() {
        return "Orders";
    }

    @Override
    public int getDBID(Connection connection) throws SQLException {
        return this.id;
    }

    @Override
    public boolean canAdd(Connection con) {
        //TODO
            //set ID as last rowid
                //select last_insert_rowid();
            //check for OrderListExistence and if yes
            //compute price for all products in order list into value
            //

        return (con != null);
    }

    @Override
    public void addToDB(Connection con) {
        if (canAdd(con)) {
            //TODO stuff

        }
    }

    @Override
    public DBObject buildFromID(Connection con, int id) {
        return null;
    }


    public static Order getGeneric(){ return new Order(); }
}
