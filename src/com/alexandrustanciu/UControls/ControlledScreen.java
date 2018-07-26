package com.alexandrustanciu.UControls;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;


public abstract class ControlledScreen {

    protected String id;
    protected String fxml;
    //TODO remove
    @FXML
    Pane mainPanel;
    ScreenController controller;

    protected ControlledScreen() { }

    public abstract void initialize();

    //This method will allow the injection of the Parent ScreenPane
    void setScreenParent(ScreenController screenParent){
        controller = screenParent;
    }

}
