package app;


import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
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

    public void addDBObject(DBObject toAdd) throws SQLException, ClassNotFoundException {
        if(connection == null) { establishConnection(); }
        toAdd.addToDB(connection);
    }

    public void addObjectsFrom(ArrayList<Object> arraySource) throws ClassNotFoundException, SQLException {
        if(arraySource.isEmpty()) { return; }

        for (Object toAdd : arraySource){
                try {
                    DBObject dbObject = (DBObject) toAdd;
                    dbObject.addToDB(connection);
                }
                //TODO catch proper illegal conversion exception
                catch (Exception cex){
                    cex.printStackTrace();
            }
        }
    }

    public void clearDB() throws SQLException, ClassNotFoundException{
            Connection con = this.getConnection();
            PreparedStatement delStat = con.prepareStatement("delete from \"Ingredients\";");
            PreparedStatement delStat2 = con.prepareStatement("delete from \"Pizza\";");
            PreparedStatement delStat3 = con.prepareStatement("delete from \"Recipe\";");

            delStat.execute();
            delStat2.execute();
            delStat3.execute();
    }
}


abstract class Item implements DBObject {

    @Override
    public final int getID() throws SQLException, ClassNotFoundException{
        int idToGet = ID_NOTFOUND;

        Connection connection = DBManager.getInstance().getConnection();

        String table = getTable();

        if(table != TABLE_UNUSED){
            PreparedStatement getID = connection.prepareStatement("SELECT rowid FROM " + table + " WHERE lower(name) = lower(?)");
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
    public abstract ArrayList<Object> loadFromFile(String path);

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

    protected abstract boolean canAdd(Connection con) throws SQLException;
}

class Ingredient extends Item{

    private boolean isVeg = false;
    private String baseUnit = "grams";

    @Override
    protected final boolean canAdd(Connection con) throws SQLException{
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
    public ArrayList<Object> loadFromFile(String path) {

        ArrayList<Object> ingredients = new ArrayList<>();

        try {
            File file = new File(path);
            Scanner fin = new Scanner(file).useDelimiter(System.getProperty("line.separator"));

            while (fin.hasNextLine()) {

                String lineToProcess;
                lineToProcess = fin.nextLine();

                Ingredient toAdd = Ingredient.fromString(lineToProcess);

                if (toAdd != null) {
                    ingredients.add(toAdd);
                }
            }

            fin.close();

        } catch (FileNotFoundException fex) {
            fex.printStackTrace();
        }

        return ingredients;
    }

    @Override
    public DBObject buildFromID(int id) throws SQLException, ClassNotFoundException {
        Ingredient toBuild = null;
        if(id > ID_NOTFOUND){
            Connection con = DBManager.getInstance().getConnection();
            PreparedStatement query = con.prepareStatement(
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

    public static Ingredient getGeneric() { return new Ingredient(); }

    //assumes delimiter is ',' for now

    ArrayList<Ingredient> getRecipeFronString(String recipe){
        if(recipe.matches("([\\w ]+[[/ \\d+]?[, ]?]?)+")){
            ArrayList<Ingredient> recipeArr = new  ArrayList<>();
            Scanner in = new Scanner(recipe).useDelimiter(", *");
            while (in.hasNext()){
                Ingredient toAdd = Ingredient.fromString(in.next().trim());
                recipeArr.add(toAdd);
            }
            return recipeArr;
        }
        return null;
    }

    public boolean isVeg() {
        return this.isVeg;
    }

    public String getBaseUnit() {
        return baseUnit;
    }

}

class Pizza extends Item{

    @Override
    public String getTable() {
        return "Pizza";
    }

    @Override
    public void addToDB(Connection con) throws SQLException, ClassNotFoundException {

       //TODO update recipes for existing pizzas

        if (canAdd(con) && hasRecipe) {
            String table = getTable();
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
                System.out.println(sex.getMessage());
            }

            this.id=getID();
            for (Recipe rec : this.recipe){

                Recipe toAdd = new Recipe(this.id, rec.getIngredientID(), rec.getQty());
                toAdd.addToDB(con);
            }
        }
    }

    @Override
    public ArrayList<Object> loadFromFile(String path) {
        ArrayList<Object> pizzas = new ArrayList<>();

        try {
            File file = new File(path);
            Scanner fin = new Scanner(file).useDelimiter(System.getProperty("line.separator"));

            while (fin.hasNextLine()) {

                String titleLine;
                String recipeLine;

                titleLine = fin.nextLine();

                //TODO check recipe line matches a recipe
                if(titleLine.matches("^\\$ ([a-zA-Z&] *)+") && fin.hasNextLine()){


                    titleLine = titleLine.replaceFirst("\\$ ", "").trim();
                    //titleLine = titleLine.trim();


                    ArrayList<Recipe> recipeToSet = Recipe.loadFromString(fin.nextLine());

                    Pizza toAdd = new Pizza(titleLine);
                    toAdd.setRecipe(recipeToSet);
                    pizzas.add(toAdd);

                }
            }

            fin.close();

        } catch (FileNotFoundException fex) {
            fex.printStackTrace();
        }

        return pizzas;
    }

    @Override
    protected final boolean canAdd(Connection con) throws SQLException{
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
        return canAdd;
    }

    @Override
    public DBObject buildFromID(int id) throws SQLException, ClassNotFoundException {
        Pizza toBuild = null;
        if(id > ID_NOTFOUND){
            Connection con = DBManager.getInstance().getConnection();
            PreparedStatement query = con.prepareStatement(
                    "SELECT * FROM " + getTable() + " WHERE id == ?");
            query.setInt(1, id);
            ResultSet rs = query.executeQuery();

            toBuild = new Pizza(
                    rs.getInt("pizzaID"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getBoolean("isVeg")
            );
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
                 Ingredient ingToBuild = Ingredient.getGeneric();
                 try {
                     ingToBuild = (Ingredient)ingToBuild.buildFromID(rec.getIngredientID());

                 } catch (SQLException e) {
                     e.printStackTrace();
                 } catch (ClassNotFoundException e) {
                     e.printStackTrace();
                 }
                 if(ingToBuild.isVeg() == false) {
                     isVeg = false;
                     break;
                 }
             }
             this.isVeg = isVeg;
        }
    }

    private void calculatePrice(){
        if(hasRecipe){
            unitPrice = 0.;

            for(Recipe rec : recipe){
                Ingredient ingredient = null;
                try {
                    ingredient = (Ingredient)
                            ( Ingredient.getGeneric().buildFromID(rec.getIngredientID()) );
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                unitPrice += ingredient.getUnitPrice() * rec.getQty();
            }
        }
    }

    public void setRecipe(ArrayList<Recipe> recipe) {
        if(!hasRecipe){
            this.recipe = recipe;
            hasRecipe = true;
        }
    }
}

class Recipe implements DBObject{
    private int id = ID_NOTFOUND;
    private int pizzaID;
    private int ingredientID;
    private int qty = 1;

    @Override
    public final int getID() throws SQLException, ClassNotFoundException{
        int idToGet = ID_NOTFOUND;

        Connection connection = DBManager.getInstance().getConnection();

        String table = getTable();

        if(table != TABLE_UNUSED){
            PreparedStatement getID = connection.prepareStatement("SELECT rowid FROM " + table + " WHERE pizzaID == ? AND ingID == ?");
            getID.setInt(1, pizzaID);
            getID.setInt(2, ingredientID);

            idToGet = getID.executeQuery().getInt(1);
        }

        return idToGet;
    }

    @Override
    public String getTable() {
        return "Recipe";
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
    public ArrayList<Object> loadFromFile(String path) {
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
                int ingID = ID_NOTFOUND;

                try {
                    ingID = new Ingredient(toProcess).getID();
                    if(ingID > ID_NOTFOUND){
                        toAdd = new Recipe(ID_NOTFOUND, ingID);
                        canAdd = true;
                    }
                } catch (SQLException sex){
                    sex.printStackTrace();
                } catch (ClassNotFoundException cex){
                    cex.printStackTrace();
                }
            }

            else if (toProcess.matches("(\\w *)+( {1}/ \\d+)")){
                Scanner pin = new Scanner(toProcess).useDelimiter("/");
                Ingredient ing = new Ingredient(pin.next().trim());

                int ingID = ID_NOTFOUND;

                try {
                    ingID = ing.getID();
                    if( ingID > ID_NOTFOUND){
                        toAdd = new Recipe(ID_NOTFOUND, ingID, Integer.parseInt(pin.next().trim()));
                        canAdd = true;
                    }
                } catch (SQLException sex){
                    sex.printStackTrace();
                } catch (ClassNotFoundException cex){
                    cex.printStackTrace();
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
    public DBObject buildFromID(int id) throws SQLException, ClassNotFoundException {
        Recipe toBuild = null;
        if(id > ID_NOTFOUND){
            Connection con = DBManager.getInstance().getConnection();
            PreparedStatement query = con.prepareStatement(
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
        this.id = id >= 0 ? id : ID_NOTFOUND;
    }

    public void setPizzaID(int pizzaID){
        if(id > ID_NOTFOUND) {
            this.pizzaID = pizzaID;
        }
    }

    public void setIngredientID(int ingredientID) {
        if(ingredientID > ID_NOTFOUND){
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