package com.alexandrustanciu.UControls;

public enum Screens {
    OVER ("Overview"),
    INVT ("Inventory"),
    ORDR ("Orders"),
    MENU ("Menu");

    private final String name;

    Screens(String s){
        name = s;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
