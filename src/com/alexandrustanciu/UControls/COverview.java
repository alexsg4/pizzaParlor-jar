package com.alexandrustanciu.UControls;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;

public class COverview extends ControlledScreen {

    @FXML private AnchorPane containerPane;
    @FXML private FlowPane contentPane;

    @Override
    public void initialize() {
        AnchorPane.setTopAnchor(contentPane, 0d);
        AnchorPane.setRightAnchor(contentPane, 0d);
        AnchorPane.setBottomAnchor(contentPane, 0d);
        AnchorPane.setLeftAnchor(contentPane, 0d);
    }
}
