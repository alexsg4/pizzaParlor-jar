package com.alexandrustanciu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class Recipe implements DBObject {
    private int id = ID_UNUSED;
    private int productID = ID_UNUSED;
    private int ingredientID = ID_UNUSED;
    private int qty = 1;

    @Override
    public final String getTable() {
        return "Recipes";
    }

    @Override
    public int getDBID(Connection connection) throws SQLException {
        int idToGet = ID_UNUSED;

        if(connection != null){
            String table = getTable();

            if(!table.equals(TABLE_UNUSED)){
                PreparedStatement getIDQuery = connection.prepareStatement(
                        "SELECT rowid FROM " + table + " WHERE productID = ? AND ingredientID = ?"
                );
                getIDQuery.setInt(1, productID);
                getIDQuery.setInt(2, ingredientID);

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

    @Override
    public boolean canAdd(Connection con) {

        boolean canAdd = false;

        try {
            if(getDBID(con) != ID_UNUSED){
                canAdd = true;
            }

        } catch (SQLException ex){
            ex.printStackTrace();
        }

        return !canAdd;
    }

    @Override
    public void addToDB (Connection con) throws SQLException {

        if(canAdd(con)){
            String table = getTable();
            PreparedStatement insertStatement = con.prepareStatement("INSERT INTO " + table + " VALUES(?, ?, ?)");
            insertStatement.setInt(1, productID);
            insertStatement.setInt(2, ingredientID);
            insertStatement.setInt(3, qty);

            try {
                insertStatement.execute();
            } catch (SQLException sex) {
                sex.printStackTrace();
            }

        }
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
                    int ingID = new Ingredient(toProcess).getDBID(con);
                    if(ingID > ID_UNUSED){
                        toAdd = new Recipe(ID_UNUSED, ingID);
                        canAdd = true;
                    }
                } catch (SQLException | ClassNotFoundException e){
                    e.printStackTrace();
                }
            }

            else if (toProcess.matches("(\\w *)+( {1}/ \\d+)")){
                Scanner pin = new Scanner(toProcess).useDelimiter("/");

                int ingID;

                try {
                    Connection con = DBManager.getInstance().getConnection();

                    String ingredientName = pin.next().trim();
                    ingID = new Ingredient(ingredientName).getDBID(con);

                    if( ingID > ID_UNUSED){
                        toAdd = new Recipe(ID_UNUSED, ingID, Integer.parseInt(pin.next().trim()));
                        canAdd = true;
                    }
                } catch (SQLException | ClassNotFoundException e){
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
                    int ingID = new Ingredient(toProcess).getDBID(con);
                    if(ingID > ID_UNUSED){
                        toReturn = new Recipe(ID_UNUSED, ingID);
                    }
                } catch (SQLException | ClassNotFoundException e){
                    e.printStackTrace();
                }
            }

            else if (toProcess.matches("(\\w *)+( {1}/ \\d+)")){
                Scanner pin = new Scanner(toProcess).useDelimiter("/");

                try {
                    Connection con = DBManager.getInstance().getConnection();

                    String ingredientName = pin.next().trim();
                    int ingID = new Ingredient(ingredientName).getDBID(con);

                    if( ingID > ID_UNUSED){
                        toReturn = new Recipe(ID_UNUSED, ingID, Integer.parseInt(pin.next().trim()));
                    }
                } catch (SQLException | ClassNotFoundException e){
                    e.printStackTrace();
                } finally {
                    pin.close();
                }
            }
        }

        in.close();
        return toReturn;
    }

    @Override
    public DBObject buildFromID(Connection connection, int id) throws SQLException {
        Recipe toBuild = null;
        if(id > ID_UNUSED && connection != null){

            PreparedStatement query = connection.prepareStatement(
                    "SELECT ROWID, * FROM " + getTable() + " WHERE ROWID = ?");
            query.setInt(1, id);

            try(ResultSet rs = query.executeQuery()){

                toBuild = new Recipe(
                        rs.getInt(1),
                        rs.getInt("productID"),
                        rs.getInt("ingredientID"),
                        rs.getInt("qty")
                );
            }
        }

        return toBuild;
    }

    private Recipe(int id, int productID, int ingredientID, int qty){
        this.id = id;
        this.productID = productID;
        this.ingredientID = ingredientID;
        setQty(qty);
    }

    private Recipe() { super(); }

    public static Recipe getGeneric() { return new Recipe(); }

    public Recipe(int productID, int ingredientID, int qty){
        this.productID = productID;
        this.ingredientID = ingredientID;
        setQty(qty);
    }

    public Recipe(int productID, int ingredientID){
        this.productID = productID;
        this.ingredientID = ingredientID;
    }

    private void setQty(int qty) {
        this.qty = qty > 0 ? qty : 1;
    }

    public void setId(int id){
        this.id = id >= 0 ? id : ID_UNUSED;
    }

    public void setProductID(int productID){
        this.productID = productID >= 0 ? productID : ID_UNUSED;
    }

    public void setIngredientID(int ingredientID){
        this.ingredientID = ingredientID >= 0 ? ingredientID : ID_UNUSED;
    }

    public int getProductID(){
        return productID;
    }

    public int getIngredientID(){
        return ingredientID;
    }

    public int getQty(){ return qty; }

}