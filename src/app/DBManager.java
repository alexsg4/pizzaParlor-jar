package app;


import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class DBManager {
    private static Connection connection;
    private static final String database = "main.db";
    private static DBManager manager;

    public static DBManager getInstance() throws ClassNotFoundException, SQLException {
        if (manager == null) {
            manager = new DBManager();
        }
        return manager;
    }

    private void establishConnection() throws ClassNotFoundException, SQLException {
        // sqlite driver
        Class.forName("org.sqlite.JDBC");
        // database path, if it's new database, it will be created in the project folder
        connection = DriverManager.getConnection("jdbc:sqlite:" + database);
    }

    private DBManager() throws ClassNotFoundException, SQLException {
        if(connection == null ) { establishConnection(); }
    }

    public Connection getConnection() throws ClassNotFoundException, SQLException {
       if(connection == null) { establishConnection(); }
       return connection;
    }

    //TODO implement
    public void addDBObject(DBObject toAdd) throws SQLException, ClassNotFoundException {
        if(connection == null) { establishConnection(); }
        toAdd.addToDB(connection);
    }

    public void loadIngredientsFromFile(String path) throws ClassNotFoundException, SQLException {

        try {
            File file = new File(path);
            Scanner fin = new Scanner(file).useDelimiter(System.getProperty("line.separator"));

            while (fin.hasNextLine()) {

                String lineToProcess;
                lineToProcess = fin.nextLine();

                Ingredient toAdd = Ingredient.fromString(lineToProcess);

                if (toAdd != null) {
                    addDBObject(toAdd);
                }
            }

            fin.close();

        } catch (FileNotFoundException fex) {
            fex.printStackTrace();
        }
    }

}

class Item implements DBObject {

    @Override
    public final int getID() throws SQLException, ClassNotFoundException{
        int idToGet = ID_NOTFOUND;

        Connection connection = DBManager.getInstance().getConnection();

        String table = getTable();

        if(table != TABLE_UNUSED){
            PreparedStatement getID = connection.prepareStatement("SELECT rowid FROM " + table + " WHERE lower(name) = lower(?)");
            getID.setString(1, this.name);

            idToGet = getID.executeQuery().getInt(1);
        }

        return idToGet;
    }

    @Override
    public String getTable() {
        return TABLE_UNUSED;
    }

    @Override
    public void addToDB(Connection con) throws SQLException, ClassNotFoundException {
        return;
    }

    protected String name = "Generic Item";
    protected double unitPrice = 0;
    protected int id = ID_NOTFOUND;

    protected Item(){ }

    protected Item(String tName, double tPrice){
        name = tName;
        setUnitPrice(tPrice);
    }

    protected Item(String tName){
        name = tName;
        unitPrice = 0;
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

class Ingredient extends Item{

    private boolean isVeg = false;
    private String baseUnit = "grams";

    @Override
    public final String getTable(){
        return "Ingredients";
    }

    @Override
    public final void addToDB(Connection con) throws SQLException, ClassNotFoundException {
        // Ingredients have a UNIQUE name constraint
        // Check name is not used
        String table = this.getTable();
        PreparedStatement checkNameStatement = con.prepareStatement("SELECT COUNT (*) FROM " + table + " WHERE LOWER(name) = LOWER(?); ");
        checkNameStatement.setString(1, this.name);

        boolean canAdd = false;

        try {
            //count rows with the same name
            if (checkNameStatement.executeQuery().getInt(1) == 0) canAdd = true;
        } catch (SQLException sex) {
            System.out.println(sex.getMessage());
        }

        if (canAdd) {

            PreparedStatement addStatement = con.prepareStatement("INSERT INTO " + table + " (name, unitPrice, isVeg, unit) VALUES (?, ?, ?, ?);");

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

    private Ingredient() {
        super();
    }

    private Ingredient(int tId, String tName, double tPrice, boolean tVeg) {
        super(tName, tPrice);
        id = tId;
        isVeg = tVeg;
    }

    private Ingredient(int tId, String tName, double tPrice, boolean tVeg, String tUnit) {
        super(tName, tPrice);
        id = tId;
        isVeg = tVeg;
        baseUnit = tUnit;
    }

    private Ingredient(String tName, double tPrice, boolean tVeg, String tUnit) {
        super(tName, tPrice);
        isVeg = tVeg;
        baseUnit = tUnit;
    }

    private Ingredient(String tName, double tPrice, int tVeg) {
        super(tName, tPrice);

        if (tVeg != 0) {
            isVeg = true;
        }
    }

    public static Ingredient fromString(String str) {

        //if(str.matches("^[^\\s*/\\s*]+$")) {
        if(str.matches("[^\\s*][[\\S+\\s+]+[\\s*/\\s*]*]+\\s*$")) {
            Scanner s = new Scanner(str).useDelimiter("\\s*/\\s*");

            String name = s.next();
            double price = 0;
            int isVeg = 0;

            try {
                if(s.hasNext())
                {
                    price = Double.parseDouble(s.next().trim());
                }
            } catch (NumberFormatException nex) {
                price = 0;
                nex.printStackTrace();
            } catch (NoSuchElementException nsex){
                price = 0;
                nsex.printStackTrace();
            }

            try {
                if(s.hasNext()){
                    isVeg = Integer.parseInt(s.next().trim());
                }
            } catch (NumberFormatException nex) {
                isVeg = 0;
                nex.printStackTrace();
            } catch (NoSuchElementException nsex){
                isVeg = 0;
                nsex.printStackTrace();
            }

            s.close();
            return new Ingredient(name, price, isVeg);
        }
        return null;
    }

    public static Ingredient getDummy() { return new Ingredient(); }

    //assumes delimiter is ',' for now
    ArrayList<Ingredient> getRecipeFronString(String recipe){
        if(recipe.matches("([\\w ]+[[/ \\d+]?[, ]?]?)+")){
            ArrayList<Ingredient> recipeArr = new  ArrayList<>();
            Scanner in = new Scanner(recipe).useDelimiter(", ");
            while (in.hasNext()){
                Ingredient toAdd = Ingredient.fromString(in.next());
                recipeArr.add(toAdd);
            }
            return recipeArr;
        }
        return null;
    }

    public boolean isVeg() {
        return isVeg;
    }

    public String getBaseUnit() {
        return baseUnit;
    }

}

//TODO implement
class Pizza extends Item{

    @Override
    public String getTable() {
        return "Pizza";
    }

    @Override
    public void addToDB(Connection con) throws SQLException, ClassNotFoundException {
        //TODO implement
    }

    private static final String recipe = "Recipe";

    private boolean isVeg = false;
    private boolean hasRecipe = false;

    private Pizza(){
        super();
    }

    private Pizza(int tId, String tName){
        super(tName);
        id = tId;

        determineIsVeg();
        calculatePrice();
    }

    private void addIngredientsToRecipe(Ingredient[] toAdd){
        if(toAdd.length == 0) { return; }
        int id = this.id;

    }

    //TODO
    private void determineIsVeg(){
        if(hasRecipe){
            //set isVeg = true
            //for each ingredient, verify isVeg
                //if isVeg = false, set isVeg = false and return
        }
    }

    //TODO
    private void calculatePrice(){
        if(hasRecipe){
            //TODO for each ingredient add unit price x qty to total
                //set unitPrice
        }
    }
}

