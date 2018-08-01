package com.alexandrustanciu.UControls;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class CMain{

    @FXML
    private Pane mainPane;
    @FXML
    private Button btnOverview;
    @FXML
    private Button btnInventory;
    @FXML
    private Button btnMenu;
    @FXML
    private Button btnOrders;
    @FXML
    private ImageView logoView;

    public CMain(){ }

    @FXML
    public void initialize(){

        ScreenController controller = ScreenController.getInstance();
        controller.prefWidthProperty().bind(mainPane.widthProperty());
        controller.prefHeightProperty().bind(mainPane.heightProperty());

        logoView.setImage(new Image("/png/053-pizza.png"));

        mainPane.getChildren().add(controller);

        btnOverview.setText("Overview");
        btnOverview.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent ->{
            controller.setScreen("Overview");
        });

        btnMenu.setText("Menu");
        btnMenu.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent ->{
            controller.setScreen("Menu");
        });

        btnOrders.setText("Orders");
        btnOrders.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent ->{
            controller.setScreen("Orders");
        });

        btnInventory.setText("Inventory");
        btnInventory.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent ->{
            controller.setScreen("Inventory");
        });

    }
}
