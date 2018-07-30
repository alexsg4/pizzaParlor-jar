package com.alexandrustanciu.UControls;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class CMain{

    @FXML private Pane mainPanel;
    @FXML private Button btnOverview;
    @FXML private Button btnIngredients;
    @FXML private Button btnMenu;
    @FXML private Button btnOrders;


    public CMain(){ }

    @FXML
    public void initialize(){

        ScreenController controller = ScreenController.getInstance();
        controller.prefWidthProperty().bind(mainPanel.widthProperty());
        controller.prefHeightProperty().bind(mainPanel.heightProperty());

        mainPanel.getChildren().add(controller);

        btnOverview.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> controller.setScreen("Overview"));
        btnIngredients.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> controller.setScreen("Ingredients"));
        btnMenu.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> controller.setScreen("Menu"));
        btnOrders.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> controller.setScreen("Orders"));

    }


}
