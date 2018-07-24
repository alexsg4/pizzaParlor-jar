package com.alexandrustanciu.Products;

import com.alexandrustanciu.DB.DBObject;
import com.alexandrustanciu.FileLoadable;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Ingredient extends PricedItem implements FileLoadable<DBObject> {

    private boolean isVeg = false;
    private String baseUnit = "g";
    private int productType = ID_UNUSED;

    @Override
    public final boolean canAdd(Connection con) throws SQLException {
        boolean canAdd = false;
        if (con != null && productType > ID_UNUSED) {
            // Ingredients have a UNIQUE name constraint
            // Check name is not used
            String table = getTable();
            PreparedStatement checkNameStatement = con.prepareStatement(
                    "SELECT COUNT (*) FROM " + table + " WHERE LOWER(name) = LOWER(?) AND productType = ?; ");
            checkNameStatement.setString(1, this.name);
            checkNameStatement.setInt(2, this.productType);

            try (ResultSet rs = checkNameStatement.executeQuery()){
                //count rows with the same name
                if (rs.next()){
                    if(rs.getInt(1) == 0){ canAdd = true; }
                }
            } catch (SQLException sex) {
                System.out.println(sex.getMessage());
            }
        }
        return canAdd;
    }

    @Override
    public final String getTable(){
        return "Ingredients";
    }

    @Override
    public final void addToDB(Connection con) throws SQLException {

        if (canAdd(con)) {
            String table = getTable();
            PreparedStatement addStatement = con.prepareStatement(
                    "INSERT INTO " + table + " (name, productType, unitPrice, isVeg, unit) VALUES (?, ?, ?, ?, ?);"
            );

            addStatement.setString(1, this.name);
            addStatement.setInt(2, this.productType);
            addStatement.setDouble(3, this.unitPrice);
            addStatement.setBoolean(4, this.isVeg);
            addStatement.setString(5, this.baseUnit);

            try {
                addStatement.execute();
            } catch (SQLException sex) {
                System.out.println(sex.getMessage());
            }
        }
    }

    @Override
    public ArrayList<DBObject> loadFromFile(String path) {

        ArrayList<DBObject> loadedIngredients = new ArrayList<>();

        try {
            File file = new File(path);
            BufferedReader fin = new BufferedReader(new FileReader(file));

            String lineToProcess;
            try {
                while ((lineToProcess = fin.readLine()) != null ) {
                    Ingredient toAdd = Ingredient.fromString(lineToProcess);
                    if (toAdd != null) {
                        loadedIngredients.add(toAdd);
                    }
                }
                fin.close();
            } catch (IOException ex) { ex.printStackTrace(); }
        } catch (FileNotFoundException fex) {
            fex.printStackTrace();
        }

        return loadedIngredients;
    }

    @Override
    public ArrayList<DBObject> loadFromFile(File file) {

        ArrayList<DBObject> loadedIngredients = new ArrayList<>();

        try {
            BufferedReader fin = new BufferedReader(new FileReader(file));

            String lineToProcess;
            try {
                while ((lineToProcess = fin.readLine()) != null ) {
                    Ingredient toAdd = Ingredient.fromString(lineToProcess);
                    if (toAdd != null) {
                        loadedIngredients.add(toAdd);
                    }
                }
                fin.close();
            } catch (IOException ex) { ex.printStackTrace(); }
        } catch (FileNotFoundException fex) {
            fex.printStackTrace();
        }

        return loadedIngredients;
    }


    @Override
    public DBObject buildFromID(Connection connection, int id) throws SQLException {
        Ingredient toBuild = null;
        if(id > ID_UNUSED && connection != null){
            PreparedStatement query = connection.prepareStatement(
                    "SELECT * FROM " + getTable() + " WHERE rowid = ?");
            query.setInt(1, id);

            try(ResultSet rs = query.executeQuery()){
                if(rs.next()) {
                    toBuild = new Ingredient(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("productType"),
                            rs.getDouble("unitPrice"),
                            rs.getBoolean("isVeg"),
                            rs.getString("unit")
                    );
                }
            }
        }
        return toBuild;
    }

    private Ingredient() {
        super();
    }

    public Ingredient(String name){
        super(name);
    }

    private Ingredient(int id, String name, double price, boolean veg) {
        super(name, price);
        this.id = id;
        this.isVeg = veg;
    }

    private Ingredient(int id, String name, double price, boolean veg, String unit) {
        super(name, price);
        this.id = id;
        this.isVeg = veg;
        this.baseUnit = unit;
    }

    private Ingredient(int id, String name, int productType, double price, boolean veg, String unit) {
        super(name, price);
        this.id = id;
        this.productType = productType;
        this.isVeg = veg;
        this.baseUnit = unit;
    }

    private Ingredient(String name, double price, boolean veg, String unit) {
        super(name, price);
        this.isVeg = veg;
        this.baseUnit = unit;
    }

    private Ingredient(String name, double price, boolean veg) {
        super(name, price);
        this.isVeg = veg;
    }

    public static Ingredient fromString(String str) {

        //if(str.matches("^[^\\s*/\\s*]+$")) {
        if(str.matches("[^\\s*][[\\S+\\s+]+[\\s*/\\s*]*]+\\s*$")) {
            Scanner s = new Scanner(str).useDelimiter("\\s*/\\s*");

            String name = s.next().trim();
            double price = 0.;
            boolean isVeg = false;

            try {
                if(s.hasNext())
                {
                    price = Double.parseDouble(s.next().trim());
                }
            } catch (NumberFormatException | NoSuchElementException nex) {
                price = 0;
                nex.printStackTrace();
            }

            try {
                if(s.hasNext()){
                    int intVeg = Integer.parseInt(s.next().trim());
                    if(intVeg != 0) { isVeg = true; }
                }
            } catch (NumberFormatException | NoSuchElementException nex){
                nex.printStackTrace();
            }

            s.close();
            return new Ingredient(name, price, isVeg);
        }
        return null;
    }

    public static Ingredient getGeneric() { return new Ingredient(); }

    public boolean isVeg() {
        return this.isVeg;
    }

    public String getBaseUnit() {
        return baseUnit;
    }

    public void setProductType(int productType){
        this.productType = productType > ID_UNUSED ? productType : ID_UNUSED;
    }

}

