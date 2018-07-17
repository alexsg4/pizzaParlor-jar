package app;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

class Pizza extends CompositeProduct{

    @Override
    public final boolean canAdd(Connection con) throws SQLException {

        boolean canAdd = false;
        if(con != null){
            // Pizzas have a UNIQUE name constraint
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

            this.id = getDBID(con);

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

                    ArrayList<Recipe> recipeToSet = Recipe.arrayFromString(recipeLine);

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

    @Override
    public final String getTypeName() {
        return "Pizza";
    }

    private boolean isVeg = false;

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

    private Pizza(int id, String name){
        super(name);
        this.id = id;

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

}
