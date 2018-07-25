package com.alexandrustanciu.Products;

import com.alexandrustanciu.DB.DBObject;
import com.alexandrustanciu.FileLoadable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Ingredient extends PricedItem implements FileLoadable<DBObject> {

    private SimpleBooleanProperty isVeg = new SimpleBooleanProperty(false);

    private SimpleStringProperty baseUnit = new SimpleStringProperty("g");

    private SimpleIntegerProperty productType = new SimpleIntegerProperty(ID_UNUSED);

    private Ingredient(int id, String name, double unitPrice, boolean isVeg) {
        super(name, unitPrice);
        setId(id);
        setIsVeg(isVeg);
    }

    private Ingredient(int id, String name, double unitPrice, boolean isVeg, String baseUnit) {
        super(name, unitPrice);
        setId(id);
        setIsVeg(isVeg);
        setBaseUnit(baseUnit);
    }

    private Ingredient(int id, String name, int productType, double unitPrice, boolean isVeg, String baseUnit) {
        super(name, unitPrice);
        setId(id);
        setProductType(productType);
        setUnitPrice(unitPrice);
        setIsVeg(isVeg);
        setBaseUnit(baseUnit);
    }

    private Ingredient(String name, double unitPrice, boolean isVeg, String baseUnit) {
        super(name, unitPrice);
        setIsVeg(isVeg);
        setBaseUnit(baseUnit);
    }

    private Ingredient(String name, double unitPrice, boolean isVeg) {
        super(name, unitPrice);
        setIsVeg(isVeg);
    }

    public boolean getIsVeg() { return isVeg.get(); }

    public void setIsVeg(boolean isVeg){ this.isVeg.set(isVeg); }

    @Override
    public final String getTable(){
        return "Ingredients";
    }

    public String getBaseUnit() {
        return baseUnit.getValue();
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
                while(rs.next()) {
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

    public void setBaseUnit(String baseUnit) {
        this.baseUnit.setValue(baseUnit);
    }

    public int getProductType(){ return productType.get(); }

    public void setProductType(int productType){
        this.productType.set(productType > ID_UNUSED ? productType : ID_UNUSED);
    }

    @Override
    public final boolean canAdd(Connection con) throws SQLException {
        boolean canAdd = false;
        if (con != null && getProductType() > ID_UNUSED) {
            // Ingredients have a UNIQUE name constraint
            // Check name is not used
            String table = getTable();
            PreparedStatement checkNameStatement = con.prepareStatement(
                    "SELECT COUNT (*) FROM " + table + " WHERE LOWER(name) = LOWER(?) AND productType = ?; ");
            checkNameStatement.setString(1, getName());
            checkNameStatement.setInt(2, getProductType());

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
    public final void addToDB(Connection con) throws SQLException {

        if (canAdd(con)) {
            String table = getTable();
            PreparedStatement addStatement = con.prepareStatement(
                    "INSERT INTO " + table + " (name, productType, unitPrice, isVeg, unit) VALUES (?, ?, ?, ?, ?);"
            );

            addStatement.setString(1, getName());
            addStatement.setInt(2, getProductType());
            addStatement.setDouble(3, getUnitPrice());
            addStatement.setBoolean(4, getIsVeg());
            addStatement.setString(5, getBaseUnit());

            try {
                addStatement.execute();
            } catch (SQLException sex) {
                System.out.println(sex.getMessage());
            }
        }
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

}

