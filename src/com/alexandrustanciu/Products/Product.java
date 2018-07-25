package com.alexandrustanciu.Products;

import com.alexandrustanciu.DB.DBManager;
import javafx.beans.property.SimpleIntegerProperty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.IllegalFormatConversionException;

public abstract class Product extends PricedItem {

    protected final static String TABLE_TYPES = "ProductTypes";

    @Override
    public final String getTable() {
        return "Products";
    }

    protected SimpleIntegerProperty type = new SimpleIntegerProperty(ID_UNUSED);

    public int getType() { return type.get(); }

    public void setType(int type) {
        if (type > 0) {
            this.type.set(type);
        }
    }

    public abstract String getTypeName();

    protected Product() {
        super();
        if (!hasTypeEntry()) {
            insertTypeEntry();
        }
    }

    protected Product(int type) {
        super();
        setType(type);
    }

    protected Product(String name, double unitPrice) {
        super(name, unitPrice);
        if (!hasTypeEntry()) {
            insertTypeEntry();
        }
    }

    protected Product(String name, int type, double unitPrice) {
        super(name, unitPrice);
        setType(type);
    }

    protected Product(String name) {
        super(name);
        if (!hasTypeEntry()) {
            insertTypeEntry();
        }
    }

    private boolean hasTypeEntry() {
        boolean hasEntry = false;

        try {
            Connection con = DBManager.getInstance().getConnection();
            PreparedStatement checkStatement = con.prepareStatement(
                    "SELECT ROWID FROM ProductTypes WHERE name = ?;"
            );
            checkStatement.setString(1, getTypeName());

            //String entriesLine = checkStatement.executeQuery().getString(1);
            ResultSet rs = checkStatement.executeQuery();
            ArrayList<String> list = new ArrayList<>();
            while (rs.next()) {
                list.add(rs.getString(1));
            }

            hasEntry = (list.size() > 0);

            //set correct product entry
            if (hasEntry) {
                int lastEntry = 0;
                try {
                    lastEntry = Integer.parseInt(list.get(list.size() - 1));
                } catch (IllegalFormatConversionException ex) {
                    ex.printStackTrace();
                }
                setType(lastEntry);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return hasEntry;
    }

    protected void insertTypeEntry() {
        try {
            Connection con = DBManager.getInstance().getConnection();
            PreparedStatement insertStatement = con.prepareStatement(
                    "INSERT INTO " + TABLE_TYPES + "(name) VALUES (?);"
            );
            insertStatement.setString(1, getTypeName());
            int typeID = insertStatement.executeUpdate();

            setType(typeID);

        } catch (SQLException  ex) {
            ex.printStackTrace();
        }
    }
}