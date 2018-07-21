package com.alexandrustanciu;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

class Pizza extends CompositeProduct{

    @Override
    public final boolean canAdd(Connection con) throws SQLException {

        boolean canAdd = false;
        if(con != null && hasRecipe){
            // Pizzas have a UNIQUE name constraint
            // Check name is not used
            String table = getTable();
            PreparedStatement checkNameStatement = con.prepareStatement("SELECT COUNT (*) FROM " + table + " WHERE LOWER(name) = LOWER(?); ");
            checkNameStatement.setString(1, this.name);

            try(ResultSet rs = checkNameStatement.executeQuery()) {
                //count rows with the same name
                if (rs.next()){
                    if(rs.getInt(1) == 0) { canAdd = true; }
                }
            } catch (SQLException sex) {
                System.out.println(sex.getMessage());
            }
        }
        return canAdd;
    }

    @Override
    public void addToDB(Connection con) throws SQLException, ClassNotFoundException {

        //TODO update recipes for existing pizzas

        if (canAdd(con)) {
            determineIsVeg();
            calculatePrice();

            //Add item with name
            PreparedStatement addStatement = con.prepareStatement(
                    "INSERT INTO " + getTable() + " (name, type, price, isVeg) VALUES (?, ?, ?, ?);");

            addStatement.setString(1, this.name);
            addStatement.setInt(2, this.type);
            addStatement.setDouble(3, this.unitPrice);
            addStatement.setBoolean(4,this.isVeg);

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
            BufferedReader fin = new BufferedReader(new FileReader(file));

            try {
                String lineToProcess;
                while ( (lineToProcess = fin.readLine()) != null) {

                    String titleLine;
                    String recipeLine;

                    titleLine = lineToProcess;

                    if (titleLine.matches("^\\$ ([a-zA-Z&] *)+") && (lineToProcess = fin.readLine()) != null) {

                        titleLine = titleLine.replaceFirst("\\$ ", "").trim();

                        //TODO check recipe line matches a recipe
                        recipeLine = lineToProcess;

                        ArrayList<Recipe> recipeToSet = Recipe.arrayFromString(recipeLine);

                        Pizza toAdd = new Pizza(titleLine);
                        toAdd.setRecipe(recipeToSet);
                        loadedPizzas.add(toAdd);

                    }
                }

                fin.close();

            } catch (IOException ex) {
                ex.printStackTrace();
            }

        } catch (FileNotFoundException fex) {
            fex.printStackTrace();
        }

        return loadedPizzas;
    }

    //Side effect: expects all pizzas have a recipe
    @Override
    public DBObject buildFromID(Connection connection, int id) throws SQLException {

        Pizza toBuild = null;
        if (id > ID_UNUSED && connection != null) {
            PreparedStatement query = connection.prepareStatement(
                    "SELECT * FROM " + getTable() + " WHERE id == ?");
            query.setInt(1, id);


            try (ResultSet rs = query.executeQuery()) {
                if (rs.next()) {
                    toBuild = new Pizza(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("type"),
                            rs.getDouble("price"),
                            rs.getBoolean("isVeg")
                    );
                }
            }

            if (toBuild != null) {

                Recipe dummyRecipe = Recipe.getGeneric();
                String recTable = dummyRecipe.getTable();

                //get IDs of all the recipe entries for this pizza
                PreparedStatement query2 = connection.prepareStatement("SELECT rowid FROM " + recTable + " WHERE pizzaID == ? ;");
                query2.setInt(1, toBuild.id);

                try(ResultSet rs = query2.executeQuery()) {
                    ArrayList<Integer> IDs = new ArrayList<>();
                    while (rs.next()){
                        IDs.add(rs.getInt(1));
                    }
                    ArrayList<Recipe> recipeToBuild = new ArrayList<>();

                    for (int recID : IDs) {
                        recipeToBuild.add((Recipe) (Recipe.getGeneric().buildFromID(connection, recID)));
                    }
                    if(!recipeToBuild.isEmpty())
                    {
                        setRecipe(recipeToBuild);
                    }
                }
            }
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

    private Pizza(int id, String name, int type, double price, boolean isVeg){
        super(name, type, price);
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
