package com.alexandrustanciu.UControls;

import com.alexandrustanciu.DB.DBManager;
import com.alexandrustanciu.Products.Ingredient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class CMain{

    @FXML Pane mainPanel;
    @FXML TableView<Ingredient> ingTable;
    private TableColumn<Ingredient, String> nameCol;
    private TableColumn<Ingredient, Double> priceCol;
    private TableColumn<Ingredient, Boolean> vegCol;
    private ObservableList<Ingredient> IngredientData;

    public CMain(){ }

    @FXML
    public void initialize(){
        assert ingTable != null : "fx:id=\"tableview\" was not injected: check your FXML file 'UserMaster.fxml'.";
        nameCol = new TableColumn<>();
        priceCol = new TableColumn<>();
        vegCol = new TableColumn<>();

        buildData();

        nameCol.setMinWidth(50);
        nameCol.setText("name");
        nameCol.setCellValueFactory(
                new PropertyValueFactory<Ingredient, String>("name")
        );

        //setCellValueFactory((p)->{
        //String x = p.getValue().getName();
        //return new SimpleStringProperty(x != null ? x : "<no name>");
        //});

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

        ingTable.getColumns().add(nameCol);
        ingTable.getColumns().add(priceCol);
        ingTable.getColumns().add(vegCol);

    }

    private void buildData(){
        IngredientData = FXCollections.observableArrayList();
        try{
            Connection con = DBManager.getInstance().getConnection();
            String table = Ingredient.getGeneric().getTable();
            PreparedStatement queryData = con.prepareStatement(
                    "SELECT ROWID FROM " + table
            );
            ResultSet rs = queryData.executeQuery();
            while (rs.next()){
                Ingredient toAdd = (Ingredient)Ingredient.getGeneric().buildFromID(con, rs.getInt(1));

                if(toAdd != null){
                    IngredientData.add(toAdd);
                }
            }
            ingTable.setItems(IngredientData);

        } catch (SQLException ex){
            ex.printStackTrace();
        }

    }
}
