package com.alexandrustanciu.UControls;

import com.alexandrustanciu.UEvents.SetScreenEvent;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.HashMap;

public class ScreenController extends StackPane {

    private static ScreenController instance = new ScreenController();
    private HashMap<String, Node> loadedScreens = new HashMap<>();

    private String currentScreen;

    private ScreenController(){
        super();
    }

    public static ScreenController getInstance() { return instance; }

    //Add the screen to the collection
    private void addScreen(String name, Node screen) {
        loadedScreens.put(name, screen);
    }

    //Loads the fxml file, add the screen to the loadedScreens collection and
    //finally injects the screenPane to the mController.
    public boolean loadScreen(String name, String resource) {
        try {
            FXMLLoader myLoader = new FXMLLoader(getClass().getResource(resource));

            Parent screenToLoad = myLoader.load();
            ControlledScreen myScreenController = myLoader.getController();
            myScreenController.setScreenParent(this);
            addScreen(name, screenToLoad);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    //Returns the Node with the appropriate name
    public Node getScreen(String name) {
        return loadedScreens.get(name);
    }

    public Node getScreen(EScreens name) {
        return loadedScreens.get(name.toString());
    }

    //This method tries to displayed the screen with a predefined name.
    //First it makes sure the screen has been already loaded.  Then if there is more than
    //one screen the new screen is been added second, and then the current screen is removed.
    // If there isn't any screen being displayed, the new screen is just added to the root.
    public boolean setScreen(final String name) {
        if (loadedScreens.get(name) != null) {   //screen loaded
            final DoubleProperty opacity = opacityProperty();

            if (!getChildren().isEmpty()) {    //if there is more than one screen

                if(currentScreen != name) {
                    Timeline fade = new Timeline(
                            new KeyFrame(Duration.ZERO, new KeyValue(opacity, 1.0)),
                            new KeyFrame(new Duration(1000), t -> {
                                getChildren().remove(0);                    //remove the displayed screen
                                getChildren().add(0, loadedScreens.get(name));     //add the screen
                                Timeline fadeIn = new Timeline(
                                        new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)),
                                        new KeyFrame(new Duration(800), new KeyValue(opacity, 1.0)));
                                fadeIn.play();
                            }, new KeyValue(opacity, 0.0)));
                    fade.play();
                }

            } else {
                setOpacity(0.0);
                getChildren().add(loadedScreens.get(name));       //no one else been displayed, then just show
                Timeline fadeIn = new Timeline(
                        new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)),
                        new KeyFrame(new Duration(2500), new KeyValue(opacity, 1.0)));
                fadeIn.play();
            }
            System.out.println("ScreenController set screen: " + name);
            currentScreen = name;
            loadedScreens.get(name).fireEvent(SetScreenEvent.ON_SET);
            return true;
        } else {
            System.out.println("screen hasn't been loaded!!! \n");
            return false;
        }

    }

    public boolean setScreen(EScreens screen){
        return setScreen(screen.toString());
    }

    //This method will remove the screen with the given name from the collection of loadedScreens
    public boolean unloadScreen(String name) {
        if (loadedScreens.remove(name) == null) {
            System.out.println("Screen didn't exist");
            return false;
        } else {
            return true;
        }
    }

    //This method will remove the screen with the given name from the collection of loadedScreens
    public boolean unloadScreen(EScreens screen) {
        return unloadScreen(screen.toString());
    }

    public String getCurrentScreenId(){
        return this.currentScreen;
    }

    public Node getCurrentScreen(){
        return getScreen(this.currentScreen);
    }
}
