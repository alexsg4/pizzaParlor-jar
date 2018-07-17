package app;

import java.sql.SQLException;
import java.util.ArrayList;

public abstract class CompositeProduct extends Product {

    @Override
    public abstract ArrayList<DBObject> loadFromFile(String path);

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
