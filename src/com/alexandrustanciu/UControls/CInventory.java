package com.alexandrustanciu.UControls;

import com.alexandrustanciu.DB.DBManager;
import com.alexandrustanciu.Products.Ingredient;
import com.alexandrustanciu.UEvents.SetScreenEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CInventory extends ControlledScreen {

    @FXML private TableView ingTable;
    @FXML private AnchorPane contentPane;

    private InventoryDAO inventoryDAO;
    private Executor exec;
    private ObservableList<Ingredient> IngredientData;

    @Override
    public void initialize() {

        AnchorPane.setTopAnchor(ingTable, 0d);
        AnchorPane.setRightAnchor(ingTable, 0d);
        AnchorPane.setBottomAnchor(ingTable, 0d);
        AnchorPane.setLeftAnchor(ingTable, 0d);

        exec= Executors.newCachedThreadPool(runnable -> {
           Thread t = new Thread(runnable);
           t.setDaemon(true);
           return t;
        });

        try {
            inventoryDAO = new InventoryDAO();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        IngredientData = FXCollections.observableArrayList();

        buildTable();
        //populate();

        contentPane.addEventFilter(SetScreenEvent.ON_SET_SCREEN, e -> populate());
    }

    private void buildTable() {
        TableColumn<Ingredient, String> nameCol = new TableColumn<>();
        TableColumn<Ingredient, Double> priceCol = new TableColumn<>();
        TableColumn<Ingredient, Boolean> vegCol = new TableColumn<>();

        nameCol.setMinWidth(50);
        nameCol.setText("name");
        nameCol.setCellValueFactory(
                new PropertyValueFactory<Ingredient, String>("name")
        );

        priceCol.setMinWidth(50);
        priceCol.setText("price");
        priceCol.setCellValueFactory(
                new PropertyValueFactory<Ingredient, Double>("unitPrice")
        );

        vegCol.setMinWidth(50);
        vegCol.setText("isVeg");
        vegCol.setCellValueFactory(
                new PropertyValueFactory<Ingredient, Boolean>("isVeg")
        );

        ingTable.getColumns().addAll(nameCol, priceCol, vegCol);

    }
    private void populate() {
        Task<List<Ingredient>> buildIngredientsTask = new Task<List<Ingredient>>() {
            @Override
            protected List<Ingredient> call() {
                //TODO test and remove
                System.out.println("DBG: CInventory: buildIngredientsTask!!");
                return inventoryDAO.buildData();
            }
        };

        buildIngredientsTask.setOnFailed( e->{
            Throwable ex = buildIngredientsTask.getException();
            //ex.printStackTrace();
            System.out.println(ex.getMessage());
        });

        buildIngredientsTask.setOnSucceeded( e->{
            IngredientData.setAll(buildIngredientsTask.getValue());
            ingTable.setItems(IngredientData);
        });

        exec.execute(buildIngredientsTask);

    }

}

class InventoryDAO{
    private Connection con;

    InventoryDAO() throws SQLException{
        con = DBManager.getInstance().getConnection();
    }

    List<Ingredient> buildData(){

        List<Ingredient> ingredientList = new ArrayList<>();
        try{
            String table = Ingredient.getGeneric().getTable();
            PreparedStatement queryData = con.prepareStatement(
                    "SELECT ROWID FROM " + table
            );
            ResultSet rs = queryData.executeQuery();
            while (rs.next()){
                Ingredient toAdd = (Ingredient)Ingredient.getGeneric().buildFromID(con, rs.getInt(1));

                if(toAdd != null){
                    ingredientList.add(toAdd);
                }
            }
        } catch (SQLException ex){
            ex.printStackTrace();
        }
        return ingredientList;
    }
}