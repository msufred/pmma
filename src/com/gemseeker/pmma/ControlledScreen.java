package com.gemseeker.pmma;

import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

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
    protected Node contentView;
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
    
    public final void setContentView(Node content){
        contentView = content;
    }
    
    public final Node getContentView(){
        return contentView;
    }
    
    public void onStart(){
        mState = STARTED;
    }
    
    public void onPause(){
        mState = PAUSED;
    }
    
    public void onResume(){
        mState = RESUMED;
    }
    
    public void onFinish(){
        mState = FINISHED;
    }
    
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
