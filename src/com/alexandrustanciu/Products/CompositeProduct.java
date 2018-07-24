package com.alexandrustanciu.Products;

import com.alexandrustanciu.DB.DBManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public abstract class CompositeProduct extends Product {

    @Override
    protected void insertTypeEntry() {
        try{
            Connection con = DBManager.getInstance().getConnection();
            PreparedStatement insertStatement = con.prepareStatement(
                    "INSERT INTO " + TABLE_TYPES + "(name, isComposite) VALUES (?, ?);"
            );
            insertStatement.setString(1, getTypeName());
            insertStatement.setBoolean(2, true);

            int typeId = insertStatement.executeUpdate();
            setType(typeId);

        } catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    protected boolean hasRecipe = false;
    protected ArrayList<Recipe> recipe;

    protected CompositeProduct()
    {
        super();
    }

    protected CompositeProduct(int type)
    {
        super();
        setType(type);
    }

    protected CompositeProduct(String name, double unitPrice){
        super(name, unitPrice);
    }

    protected CompositeProduct(String name, int type, double unitPrice){
        super(name, type, unitPrice);
    }

    protected CompositeProduct(String name){
        super(name);
    }

    protected void calculatePrice() {
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

    protected void setRecipe(ArrayList<Recipe> recipe) {
        if(!hasRecipe){
            this.recipe = recipe;
            hasRecipe = true;
        }
    }


}
