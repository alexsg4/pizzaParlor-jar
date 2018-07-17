package app;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Ingredient extends PricedItem {

    private boolean isVeg = false;
    private String baseUnit = "g";
    private int productType = ID_UNUSED;

    @Override
    public final boolean canAdd(Connection con) throws SQLException {
        boolean canAdd = false;
        if (con != null) {
            // Ingredients have a UNIQUE name constraint
            // Check name is not used
            String table = getTable();
            PreparedStatement checkNameStatement = con.prepareStatement("SELECT COUNT (*) FROM " + table + " WHERE LOWER(name) = LOWER(?); ");
            checkNameStatement.setString(1, this.name);

            try {
                //count rows with the same name
                if (checkNameStatement.executeQuery().getInt(1) == 0) canAdd = true;
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

    //TODO consider product type when adding to DB
    @Override
    public final void addToDB(Connection con) throws SQLException {

        if (canAdd(con)) {
            String table = getTable();
            PreparedStatement addStatement = con.prepareStatement(
                    "INSERT INTO " + table + " (name, unitPrice, isVeg, unit) VALUES (?, ?, ?, ?);"
            );

            addStatement.setString(1, this.name);
            addStatement.setDouble(2, this.unitPrice);
            addStatement.setBoolean(3, this.isVeg);
            addStatement.setString(4, this.baseUnit);

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
            Scanner fin = new Scanner(file).useDelimiter(System.getProperty("line.separator"));

            while (fin.hasNextLine()) {

                String lineToProcess;
                lineToProcess = fin.nextLine();

                app.Ingredient toAdd = app.Ingredient.fromString(lineToProcess);

                if (toAdd != null) {
                    loadedIngredients.add(toAdd);
                }
            }

            fin.close();

        } catch (FileNotFoundException fex) {
            fex.printStackTrace();
        }

        return loadedIngredients;
    }

    @Override
    public DBObject buildFromID(Connection connection, int id) throws SQLException, ClassNotFoundException {
        app.Ingredient toBuild = null;
        if(id > ID_UNUSED && connection != null){
            PreparedStatement query = connection.prepareStatement(
                    "SELECT * FROM " + getTable() + " WHERE rowid == ?");
            query.setInt(1, id);
            ResultSet rs = query.executeQuery();

            toBuild = new app.Ingredient(
                    rs.getInt("ingID"),
                    rs.getString("name"),
                    rs.getDouble("unitPrice"),
                    rs.getBoolean("isVeg"),
                    rs.getString("unit")
            );
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

    public static app.Ingredient fromString(String str) {

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
            return new app.Ingredient(name, price, isVeg);
        }
        return null;
    }

    public static app.Ingredient getGeneric() { return new app.Ingredient(); }

    public boolean isVeg() {
        return this.isVeg;
    }

    public String getBaseUnit() {
        return baseUnit;
    }

}

