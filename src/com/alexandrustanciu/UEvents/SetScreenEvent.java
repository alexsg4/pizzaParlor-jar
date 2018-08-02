package com.alexandrustanciu.UEvents;

import javafx.event.Event;
import javafx.event.EventType;

public class SetScreenEvent extends Event {

    public static final SetScreenEvent ON_SET = new SetScreenEvent(ON_SET_SCREEN);
    private static final EventType<SetScreenEvent> OPTIONS_ALL = new EventType<>("OPTIONS_ALL");
    public static final EventType<SetScreenEvent> ON_SET_SCREEN = new EventType<>(OPTIONS_ALL, "ON_SET_SCREEN");

    private SetScreenEvent(EventType type){
        super(type);
    }

}
