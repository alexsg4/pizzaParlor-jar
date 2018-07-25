package com.alexandrustanciu.Products;

import com.alexandrustanciu.DB.DBManager;
import com.alexandrustanciu.DB.DBObject;
import javafx.beans.property.SimpleIntegerProperty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class Recipe implements DBObject {

    private SimpleIntegerProperty id = new SimpleIntegerProperty(ID_UNUSED);
    private SimpleIntegerProperty productID = new SimpleIntegerProperty(ID_UNUSED);
    private SimpleIntegerProperty ingredientID = new SimpleIntegerProperty(ID_UNUSED);
    private SimpleIntegerProperty qty = new SimpleIntegerProperty(1);


    private Recipe() { super(); }
    public Recipe(int productID, int ingredientID){
        setProductID(productID);
        setIngredientID(ingredientID);
    }

    public Recipe(int productID, int ingredientID, int qty){
        setProductID(productID);
        setIngredientID(ingredientID);
        setQty(qty);
    }
    private Recipe(int id, int productID, int ingredientID, int qty){
        setId(id);
        setProductID(productID);
        setIngredientID(ingredientID);
        setQty(qty);
    }

    public static Recipe getGeneric() { return new Recipe(); }

    public int getId() { return id.get(); }

    public void setId(int id){
        this.id.set(id >= 0 ? id : ID_UNUSED);
    }

    public int getProductID(){
        return productID.get();
    }

    @Override
    public final String getTable() {
        return "Recipes";
    }

    public static ArrayList<Recipe> arrayFromString(String src){
        ArrayList<Recipe> toReturn = new ArrayList<>();

        Scanner in = new Scanner(src).useDelimiter(", *");
        while (in.hasNext()){
            String toProcess = in.next().trim();

            boolean canAdd = false;
            Recipe toAdd = null;

            //TODO refactor

            if(toProcess.matches( "(\\w *)+")){
                try {
                    Connection con = DBManager.getInstance().getConnection();
                    int ingID = new Ingredient(toProcess).getIDfromDB(con);
                    if(ingID > ID_UNUSED){
                        toAdd = new Recipe(ID_UNUSED, ingID);
                        canAdd = true;
                    }
                } catch (SQLException  e){
                    e.printStackTrace();
                }
            }

            else if (toProcess.matches("(\\w *)+( {1}/ \\d+)")){
                Scanner pin = new Scanner(toProcess).useDelimiter("/");

                int ingID;

                try {
                    Connection con = DBManager.getInstance().getConnection();

                    String ingredientName = pin.next().trim();
                    ingID = new Ingredient(ingredientName).getIDfromDB(con);

                    if( ingID > ID_UNUSED){
                        toAdd = new Recipe(ID_UNUSED, ingID, Integer.parseInt(pin.next().trim()));
                        canAdd = true;
                    }
                } catch (SQLException e){
                    e.printStackTrace();
                } finally {
                    pin.close();
                }
            }

            if(canAdd){
                toReturn.add(toAdd);
            }
        }

        in.close();
        return toReturn;
    }

    public static Recipe objectFromString(String src){

        Scanner in = new Scanner(src);
        Recipe toReturn = null;

        //TODO refactor
        if (in.hasNext()){
            String toProcess = in.next().trim();

            if(toProcess.matches( "(\\w *)+")){
                try {
                    Connection con = DBManager.getInstance().getConnection();
                    int ingID = new Ingredient(toProcess).getIDfromDB(con);
                    if(ingID > ID_UNUSED){
                        toReturn = new Recipe(ID_UNUSED, ingID);
                    }
                } catch (SQLException e){
                    e.printStackTrace();
                }
            }

            else if (toProcess.matches("(\\w *)+( {1}/ \\d+)")){
                Scanner pin = new Scanner(toProcess).useDelimiter("/");

                try {
                    Connection con = DBManager.getInstance().getConnection();

                    String ingredientName = pin.next().trim();
                    int ingID = new Ingredient(ingredientName).getIDfromDB(con);

                    if( ingID > ID_UNUSED){
                        toReturn = new Recipe(ID_UNUSED, ingID, Integer.parseInt(pin.next().trim()));
                    }
                } catch (SQLException  e){
                    e.printStackTrace();
                } finally {
                    pin.close();
                }
            }
        }

        in.close();
        return toReturn;
    }

    public void setProductID(int productID){
        this.productID.set(productID >= 0 ? productID : ID_UNUSED);
    }

    public int getIngredientID(){
        return ingredientID.get();
    }

    @Override
    public boolean canAdd(Connection con) {

        boolean canAdd = false;

        try {
            if(getIDfromDB(con) != ID_UNUSED){
                canAdd = true;
            }

        } catch (SQLException ex){
            ex.printStackTrace();
        }

        return !canAdd;
    }

    @Override
    public DBObject buildFromID(Connection connection, int id) throws SQLException {
        Recipe toBuild = null;
        if(id > ID_UNUSED && connection != null){

            PreparedStatement query = connection.prepareStatement(
                    "SELECT ROWID, * FROM " + getTable() + " WHERE ROWID = ?");
            query.setInt(1, id);

            try(ResultSet rs = query.executeQuery()) {
                while (rs.next()) {
                    toBuild = new Recipe(
                            rs.getInt(1),
                            rs.getInt("productID"),
                            rs.getInt("ingredientID"),
                            rs.getInt("qty")
                    );
                }
            }
        }

        return toBuild;
    }

    public void setIngredientID(int ingredientID){
        this.ingredientID.set(ingredientID >= 0 ? ingredientID : ID_UNUSED);
    }

    public int getQty(){ return qty.get(); }

    private void setQty(int qty) {
        this.qty.set(qty > 0 ? qty : 1);
    }

    @Override
    public void addToDB (Connection con) throws SQLException {

        if(canAdd(con)){
            String table = getTable();
            PreparedStatement insertStatement = con.prepareStatement("INSERT INTO " + table + " VALUES(?, ?, ?)");
            insertStatement.setInt(1, getProductID());
            insertStatement.setInt(2, getIngredientID());
            insertStatement.setInt(3, getQty());

            try {
                insertStatement.execute();
            } catch (SQLException sex) {
                sex.printStackTrace();
            }

        }
    }

    @Override
    public int getIDfromDB(Connection connection) throws SQLException {
        int idToGet = ID_UNUSED;

        if(connection != null){
            String table = getTable();

            if(!table.equals(TABLE_UNUSED)){
                PreparedStatement getIDQuery = connection.prepareStatement(
                        "SELECT rowid FROM " + table + " WHERE productID = ? AND ingredientID = ?"
                );
                getIDQuery.setInt(1, getProductID());
                getIDQuery.setInt(2, getIngredientID());

                try(ResultSet rs = getIDQuery.executeQuery()) {
                    if (rs.next()) {
                        String idLine = rs.getString(1);
                        if (idLine.matches("\\d+")) {
                            idToGet = Integer.parseInt(idLine);
                        }
                    }
                }
            }
        }

        return idToGet;
    }

}