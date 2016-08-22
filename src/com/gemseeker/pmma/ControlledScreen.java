package com.gemseeker.pmma;

import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;

/**
 *
 * @author RAFIS-FRED
 */
public abstract class ControlledScreen {

    public static final int CREATED = 0;
    public static final int STARTED = 1;
    public static final int PAUSED = 2;
    public static final int RESUMED = 3;
    public static final int FINISHED = 4;
    
    protected int mState;
    
    protected ScreenController screenController;
    protected Parent contentView;
    protected final String name;
    private boolean isChild = false;
    
    public ControlledScreen(String name){
        /*
        name is required, this will be used as key to ScreenController's HashMap
        objects like the map of ControlledScreens and EventHandler<ActionMap>
        on finish actions specific for every screen
        */
        this.name = name;
        mState = CREATED;
    }
    
    public void setScreenController(ScreenController controller){
        screenController = controller;
    }
    
    public ScreenController getScreenController(){
        return screenController;
    }
    
    public String getName(){
        return name;
    }
    
    public void setContentView(URL location){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(location);
        loader.setController(this);
        try{
            loader.load();
            contentView = loader.getRoot();
        }catch(IOException e){
            System.err.println("Failed to load fxml file from " + location.getFile());
            System.err.println(e);
        }
    }
    
    public final void setContentView(Parent content){
        contentView = content;
    }
    
    public final Node getContentView(){
        return contentView;
    }
    
    public final void doOnStart(){
        onStart();
        mState = STARTED;
    }
    
    public abstract void onStart();
    
    public final void doOnPause(){
        onPause();
        mState = PAUSED;
    }
    
    public abstract void onPause();
    
    public final void doOnResume(){
        onResume();
        mState = RESUMED;
    }
    
    public abstract void onResume();
    
    public final void doOnFinish(){
        onFinish();
        mState = FINISHED;
    }
    
    public abstract void onFinish();
    
    public int getState(){
        return mState;
    }

    public final void setAsChild(boolean isChild){
        this.isChild = isChild;
    }
    
    public boolean isChild(){
        return isChild;
    }
}
