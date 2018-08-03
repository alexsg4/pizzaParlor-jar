package com.alexandrustanciu.UEvents;

import javafx.event.Event;
import javafx.event.EventType;

public class ScreenEvent extends Event {

    private static final EventType<ScreenEvent> OPTIONS_ALL = new EventType<>("OPTIONS_ALL");
    public static final EventType<ScreenEvent> ON_SET_SCREEN = new EventType<>(OPTIONS_ALL, "ON_SET_SCREEN");
    public static final ScreenEvent ON_SET = new ScreenEvent(ON_SET_SCREEN);

    private ScreenEvent(EventType type){
        super(type);
    }

}
