package app;


import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class DBManager {
    private static Connection mConnection;
    private static final String mDatabase = "main.db";
    private static DBManager mInstance;

    private void establishConnection() throws ClassNotFoundException, SQLException {
        // sqlite driver
        Class.forName("org.sqlite.JDBC");
        // database path, if it's new database, it will be created in the project folder
        mConnection = DriverManager.getConnection("jdbc:sqlite:" + mDatabase);
    }

    public Connection getConnection() throws ClassNotFoundException, SQLException {
        if(mConnection == null) { establishConnection(); }
        return mConnection;
    }

    private DBManager() throws ClassNotFoundException, SQLException {
        if(mConnection == null ) { establishConnection(); }
    }

    public static DBManager getInstance() throws ClassNotFoundException, SQLException {
        if (mInstance == null) {
            mInstance = new DBManager();
        }
        return mInstance;
    }

    public void addDBObject(DBObject toAdd) throws SQLException, ClassNotFoundException {
        toAdd.addToDB(mConnection);
    }

    public void addDBObjectsFrom(ArrayList<DBObject> arraySource) throws ClassNotFoundException, SQLException {
        if(arraySource.isEmpty()) { return; }

        for (DBObject toAdd : arraySource){
            toAdd.addToDB(mConnection);
        }
    }

    //Dangerous. TODO remove after testing is complete.
    public void clearDB() throws SQLException, ClassNotFoundException{

            PreparedStatement delStat = mConnection.prepareStatement("delete from \"Ingredients\";");
            PreparedStatement delStat2 = mConnection.prepareStatement("delete from \"Pizza\";");
            PreparedStatement delStat3 = mConnection.prepareStatement("delete from \"Recipe\";");

            delStat.execute();
            delStat2.execute();
            delStat3.execute();
    }

    public int getDBObjectID(DBObject object) throws SQLException, ClassNotFoundException {
        return object.getDBID(mConnection);
    }

    public DBObject buildDBObjFromID(DBObject genericObject, int id) throws SQLException, ClassNotFoundException {
        return genericObject.buildFromID(mConnection, id);
    }


}

abstract class Item implements DBObject {

    @Override
    public final int getDBID(Connection connection) throws SQLException, ClassNotFoundException{
        int idToGet = ID_UNUSED;

        String table = getTable();

        if(!table.equals(TABLE_UNUSED)){
            PreparedStatement getID = connection.prepareStatement("SELECT rowid FROM " + table + " WHERE lower(name) == lower(?)");
            getID.setString(1, this.name);

            String queryResult = getID.executeQuery().getString(1);

            if(queryResult.matches("\\d+")){ idToGet = Integer.parseInt(queryResult); }
        }

        return idToGet;
    }

    @Override
    public String getTable() {
        return TABLE_UNUSED;
    }

    @Override
    public abstract void addToDB(Connection con) throws SQLException, ClassNotFoundException;

    @Override
    public abstract ArrayList<DBObject> loadFromFile(String path);

    protected String name = "Generic Item";
    protected double unitPrice = 0;
    protected int id = ID_UNUSED;

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

    protected abstract boolean canAdd(Connection con) throws SQLException;
}

class Ingredient extends Item{

    private boolean isVeg = false;
    private String baseUnit = "grams";

    @Override
    protected final boolean canAdd(Connection con) throws SQLException {
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

    @Override
    public final void addToDB(Connection con) throws SQLException, ClassNotFoundException {

        if (canAdd(con)) {
            String table = getTable();
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

    @Override
    public ArrayList<DBObject> loadFromFile(String path) {

        ArrayList<DBObject> loadedIngredients = new ArrayList<>();

        try {
            File file = new File(path);
            Scanner fin = new Scanner(file).useDelimiter(System.getProperty("line.separator"));

            while (fin.hasNextLine()) {

                String lineToProcess;
                lineToProcess = fin.nextLine();

                Ingredient toAdd = Ingredient.fromString(lineToProcess);

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
        Ingredient toBuild = null;
        if(id > ID_UNUSED && connection != null){
            PreparedStatement query = connection.prepareStatement(
                    "SELECT * FROM " + getTable() + " WHERE rowid == ?");
            query.setInt(1, id);
            ResultSet rs = query.executeQuery();

            toBuild = new Ingredient(
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

    public Ingredient(String tName){
        super(tName);
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

            String name = s.next().trim();
            double price = 0;
            int isVeg = 0;

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
                    isVeg = Integer.parseInt(s.next().trim());
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

}

class Pizza extends Item{

    @Override
    public final String getTable() {
        return "Pizza";
    }

    @Override
    protected final boolean canAdd(Connection con) throws SQLException{

        boolean canAdd = false;
        if(con != null){
            // Pizzas have a UNIQUE name constraint
            // Check name is not used
            String table = this.getTable();
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
    public void addToDB(Connection con) throws SQLException, ClassNotFoundException {

       //TODO update recipes for existing pizzas

        if (canAdd(con) && hasRecipe) {
            determineIsVeg();
            calculatePrice();

            //Add item with name
            PreparedStatement addStatement = con.prepareStatement(
                    "INSERT INTO " + getTable() + " (name, price, isVeg) VALUES (?, ?, ?);");

            addStatement.setString(1, this.name);
            addStatement.setDouble(2, this.unitPrice);
            addStatement.setBoolean(3,this.isVeg);

            try {
                addStatement.execute();
            } catch (SQLException sex) {
                sex.printStackTrace();
            }

            this.id= getDBID(con);

            for (Recipe entry : this.recipe){

                Recipe recipeEntry = new Recipe(this.id, entry.getIngredientID(), entry.getQty());
                recipeEntry.addToDB(con);
            }
        }
    }

    @Override
    public ArrayList<DBObject> loadFromFile(String path) {
        ArrayList<DBObject> loadedPizzas = new ArrayList<>();

        try {
            File file = new File(path);
            Scanner fin = new Scanner(file).useDelimiter(System.getProperty("line.separator"));

            while (fin.hasNextLine()) {

                String titleLine;
                String recipeLine;

                titleLine = fin.nextLine();

                if(titleLine.matches("^\\$ ([a-zA-Z&] *)+") && fin.hasNextLine()){

                    titleLine = titleLine.replaceFirst("\\$ ", "").trim();

                    //TODO check recipe line matches a recipe
                    recipeLine = fin.nextLine();

                    ArrayList<Recipe> recipeToSet = Recipe.loadFromString(recipeLine);

                    Pizza toAdd = new Pizza(titleLine);
                    toAdd.setRecipe(recipeToSet);
                    loadedPizzas.add(toAdd);

                }
            }

            fin.close();

        } catch (FileNotFoundException fex) {
            fex.printStackTrace();
        }

        return loadedPizzas;
    }

    //Side effect: expects all pizzas have a recipe
    @Override
    public DBObject buildFromID(Connection connection, int id) throws SQLException, ClassNotFoundException {

        Pizza toBuild = null;
        if (id > ID_UNUSED && connection != null) {
            PreparedStatement query = connection.prepareStatement(
                    "SELECT * FROM " + getTable() + " WHERE id == ?");
            query.setInt(1, id);

            ResultSet rs = query.executeQuery();
            toBuild = new Pizza(
                    rs.getInt("pizzaID"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getBoolean("isVeg")
            );

            Recipe dummyRecipe = Recipe.getGeneric();
            String recTable = dummyRecipe.getTable();

            //get IDs of all the recipe entries for this pizza
            query = connection.prepareStatement("SELECT rowid FROM " + recTable + " WHERE pizzaID == ? ;");
            query.setInt(1, toBuild.id);
            int[] IDs = (int[]) query.executeQuery().getArray(1).getArray();

            ArrayList<Recipe> recipeToBuild = new ArrayList<>();

            //TODO test

            for(int recID : IDs){
                recipeToBuild.add((Recipe)(Recipe.getGeneric().buildFromID(connection, recID)));
            }
            setRecipe(recipeToBuild);
        }

        return toBuild;
    }

    private boolean isVeg = false;
    private boolean hasRecipe = false;
    private ArrayList<Recipe> recipe;

    public static Pizza getGeneric(){ return new Pizza(); }

    private Pizza(){
        super();
    }

    public Pizza(String name) { super(name); }

    private Pizza(int id, String name, double price, boolean isVeg){
        super(name, price);
        this.id = id;
        this.isVeg = isVeg;
    }

    private Pizza(int tId, String tName){
        super(tName);
        id = tId;

        determineIsVeg();
        calculatePrice();
    }

    private void determineIsVeg() {
        if(hasRecipe){
             boolean isVeg = true;
             for(Recipe rec : recipe){
                 Ingredient ingToBuild = null;

                 try {
                     int id = rec.getIngredientID();
                     ingToBuild = (Ingredient) DBManager.getInstance().buildDBObjFromID(Ingredient.getGeneric(), id);

                 } catch (SQLException | ClassNotFoundException e) {
                     e.printStackTrace();
                 }

                 if(ingToBuild != null && !ingToBuild.isVeg()) {
                     isVeg = false;
                     break;
                 }
             }
             this.isVeg = isVeg;
        }
    }

    private void calculatePrice() {
        if (hasRecipe) {
            unitPrice = 0.;

            for (Recipe rec : recipe) {
                Ingredient ingToBuild = null;

                try {
                    int id = rec.getIngredientID();
                    ingToBuild = (Ingredient) DBManager.getInstance().buildDBObjFromID(Ingredient.getGeneric(), id);

                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }

                if(ingToBuild != null){
                    unitPrice += ingToBuild.getUnitPrice() * rec.getQty();
                }
            }
        }
    }

    private void setRecipe(ArrayList<Recipe> recipe) {
        if(!hasRecipe){
            this.recipe = recipe;
            hasRecipe = true;
        }
    }
}

class Recipe implements DBObject{
    private int id = ID_UNUSED;
    private int pizzaID = ID_UNUSED;
    private int ingredientID = ID_UNUSED;
    private int qty = 1;

    @Override
    public final String getTable() {
        return "Recipe";
    }

    @Override
    public final int getDBID(Connection connection) throws SQLException, ClassNotFoundException{
        int idToGet = ID_UNUSED;

        if(connection != null){
            String table = getTable();

            if(!table.equals(TABLE_UNUSED)){
                PreparedStatement getID = connection.prepareStatement("SELECT rowid FROM " + table + " WHERE pizzaID == ? AND ingID == ?");
                getID.setInt(1, pizzaID);
                getID.setInt(2, ingredientID);

                idToGet = getID.executeQuery().getInt(1);
            }
        }

        return idToGet;
    }

    @Override
    public void addToDB (Connection con) throws SQLException, ClassNotFoundException {
        String table = getTable();
        PreparedStatement insertStatement = con.prepareStatement("INSERT INTO " + table + " (pizzaID, ingID, qty) VALUES(?, ?, ?)" );
        insertStatement.setInt(1, pizzaID);
        insertStatement.setInt(2, ingredientID);
        insertStatement.setInt(3, qty);

        try{
            insertStatement.execute();
        } catch (SQLException sex){
            sex.printStackTrace();
        }

    }

    @Override
    public ArrayList<DBObject> loadFromFile(String path) {
        //TODO implement
        return null;
    }

    public static ArrayList<Recipe> loadFromString(String src){
        ArrayList<Recipe> toReturn = new ArrayList<>();

        Scanner in = new Scanner(src).useDelimiter(", *");
        while (in.hasNext()){
            String toProcess = in.next().trim();

            boolean canAdd = false;
            Recipe toAdd = null;

            if(toProcess.matches( "(\\w *)+")){
                int ingID = ID_UNUSED;

                try {
                    Connection con = DBManager.getInstance().getConnection();
                    ingID = new Ingredient(toProcess).getDBID(con);
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

    @Override
    public DBObject buildFromID(Connection connection, int id) throws SQLException, ClassNotFoundException {
        Recipe toBuild = null;
        if(id > ID_UNUSED && connection != null){

            PreparedStatement query = connection.prepareStatement(
                    "SELECT * FROM " + getTable() + " WHERE id == ?");
            query.setInt(1, id);

            ResultSet rs = query.executeQuery();

            toBuild = new Recipe(
                    rs.getInt("recID"),
                    rs.getInt("pizzaID"),
                    rs.getInt("ingID"),
                    rs.getInt("qty")
            );
        }

        return toBuild;
    }

    private Recipe(int id, int pizzaID, int ingredientID, int qty){
        this.id = id;
        this.pizzaID = pizzaID;
        this.ingredientID = ingredientID;
        setQty(qty);
    }

    private Recipe() { }

    public static Recipe getGeneric() { return new Recipe(); }


    public Recipe(int pizzaID, int ingredientID, int qty){
        this.pizzaID = pizzaID;
        this.ingredientID = ingredientID;
        setQty(qty);
    }

    public Recipe(int pizzaID, int ingredientID){
        this.pizzaID = pizzaID;
        this.ingredientID = ingredientID;
    }

    private void setQty(int qty) {
        this.qty = qty > 0 ? qty : 1;
    }

    public void setId(int id){
        this.id = id >= 0 ? id : ID_UNUSED;
    }

    public void setPizzaID(int pizzaID){
        if(id > ID_UNUSED) {
            this.pizzaID = pizzaID;
        }
    }

    public void setIngredientID(int ingredientID) {
        if(ingredientID > ID_UNUSED){
            this.ingredientID = ingredientID;
        }
    }

    public int getPizzaID(){
        return pizzaID;
    }

    public int getIngredientID(){
        return ingredientID;
    }

    public int getQty(){ return qty; }

}