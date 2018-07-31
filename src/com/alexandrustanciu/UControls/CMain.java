package com.alexandrustanciu.UControls;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class CMain{

    @FXML private Pane mainPane;
    @FXML private Button btnOverview;
    @FXML private Button btnIngredients;
    @FXML private Button btnMenu;
    @FXML private Button btnOrders;


    public CMain(){ }

    @FXML
    public void initialize(){

        ScreenController controller = ScreenController.getInstance();
        controller.prefWidthProperty().bind(mainPane.widthProperty());
        controller.prefHeightProperty().bind(mainPane.heightProperty());

        mainPane.getChildren().add(controller);

        btnOverview.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> controller.setScreen("Overview"));
        btnIngredients.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> controller.setScreen("Ingredients"));
        btnMenu.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> controller.setScreen("Menu"));
        btnOrders.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> controller.setScreen("Orders"));

    }


}
