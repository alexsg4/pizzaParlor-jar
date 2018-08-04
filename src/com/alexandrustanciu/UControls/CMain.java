package com.alexandrustanciu.UControls;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class CMain{

    @FXML Pane mainPane;
    @FXML private Button btnOverview, btnOrders, btnMenu, btnInventory;
    @FXML private ImageView logoView;

    @FXML
    public void initialize(){

        ScreenController controller = ScreenController.getInstance();
        controller.prefWidthProperty().bind(mainPane.widthProperty());
        controller.prefHeightProperty().bind(mainPane.heightProperty());

        mainPane.getChildren().add(controller);

        buildSideBar(controller);

    }

    private void buildSideBar(ScreenController controller) {

        logoView.setImage(new Image("/png/053-pizza.png"));

        btnOverview.setText(EScreens.OVER.toString());
        btnOverview.setOnAction( mouseEvent -> controller.setScreen(EScreens.OVER.toString()));

        btnMenu.setText(EScreens.MENU.toString());
        btnMenu.setOnAction( mouseEvent -> controller.setScreen(EScreens.MENU.toString()));

        btnOrders.setText(EScreens.ORDR.toString());
        btnOrders.setOnAction( mouseEvent -> controller.setScreen(EScreens.ORDR.toString()));

        btnInventory.setText(EScreens.INVT.toString());
        btnInventory.setOnAction( mouseEvent -> controller.setScreen(EScreens.INVT.toString()));
    }

}
