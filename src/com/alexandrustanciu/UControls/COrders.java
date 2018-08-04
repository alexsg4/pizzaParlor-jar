package com.alexandrustanciu.UControls;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;

public class COrders extends ControlledScreen {

    @FXML private FlowPane contentPane;
    @FXML private BorderPane widget;

    @Override
    public void initialize() {

        AnchorPane.setTopAnchor(contentPane, 0d);
        AnchorPane.setRightAnchor(contentPane, 0d);
        AnchorPane.setBottomAnchor(contentPane, 0d);
        AnchorPane.setLeftAnchor(contentPane, 0d);

        //TODO implement
    }

    private void populate(){
        //TODO implement
    }
}

