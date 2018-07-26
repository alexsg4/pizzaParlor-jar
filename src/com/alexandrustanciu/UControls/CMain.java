package com.alexandrustanciu.UControls;


import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

public class CMain{

    @FXML Pane mainPanel;

    public CMain(){ }

    @FXML
    public void initialize(){

        ScreenController.getInstance().prefWidthProperty().bind(mainPanel.widthProperty());
        ScreenController.getInstance().prefHeightProperty().bind(mainPanel.heightProperty());

        mainPanel.getChildren().add(ScreenController.getInstance());


    }


}
