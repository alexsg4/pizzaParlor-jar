package com.alexandrustanciu.UControls;

import com.alexandrustanciu.DB.DBManager;
import com.alexandrustanciu.Orders.Order;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class COverview extends ControlledScreen {

    @FXML private FlowPane contentPane;
    @FXML private BorderPane widgetSales;
    @FXML private Label titleSales;
    @FXML private Label textSalesNumber;

    private Executor exec;
    private SalesDAO salesDAO;


    @Override
    public void initialize() {

        AnchorPane.setTopAnchor(contentPane, 0d);
        AnchorPane.setRightAnchor(contentPane, 0d);
        AnchorPane.setBottomAnchor(contentPane, 0d);
        AnchorPane.setLeftAnchor(contentPane, 0d);

        titleSales.setText("Products sold");

        try{
            salesDAO = new SalesDAO();
        } catch (SQLException ex){
            ex.printStackTrace();
        }

        exec = Executors.newCachedThreadPool(runnable -> {
           Thread t = new Thread(runnable);
           t.setDaemon(true);
           return t;
        });

        populate();
    }

    private void populate(){
       Task<String> getSalesDataTask = new Task<String>(){
           @Override
           public String call(){
               return salesDAO.getSalesData();
           }
       };

       getSalesDataTask.setOnFailed(e->{
           //getSalesDataTask.getException().printStackTrace();
           System.out.println(getSalesDataTask.getException().getMessage());
       });

       getSalesDataTask.setOnSucceeded(e -> {
           //TODO remove
           System.out.println("DBG: "+getSalesDataTask.getValue());
           textSalesNumber.setText(getSalesDataTask.getValue());
        });

       exec.execute(getSalesDataTask);
    }
}

class SalesDAO{
    private Connection con;

    SalesDAO() throws SQLException{
        con = DBManager.getInstance().getConnection();
    }

    String getSalesData(){
        String toReturn = "null";
        try{
            String table = Order.getGeneric().getTable();
            PreparedStatement ps = con.prepareStatement(
                    "SELECT COUNT(*) FROM "+ table
            );
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                toReturn = rs.getString(1);
                if(toReturn.equals("0")){
                    toReturn = "none";
                }
            }

        } catch (SQLException ex){
            ex.printStackTrace();
        }
        return toReturn;
    }
}
