package com.alexandrustanciu.UControls;

public abstract class ControlledScreen{

    ScreenController mController;

    public abstract void initialize();

    //This method will allow the injection of the Parent ScreenPane
    void setScreenParent(ScreenController screenParent){
        mController = screenParent;
    }

}
