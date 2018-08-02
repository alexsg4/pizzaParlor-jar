package com.alexandrustanciu.UControls;

public enum EScreens {
    OVER ("Overview"),
    INVT ("Inventory"),
    ORDR ("Orders"),
    MENU ("Menu");

    private final String name;

    EScreens(String s){
        name = s;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
