package com.gemseeker.pmma.ui.components;

import javafx.scene.control.Label;

/**
 *
 * @author u
 */
public class FormLabel extends Label {
    
    public static final int NONE = -2;
    public static final int TOO_SHORT = 0;
    public static final int ALREADY_EXISTS = 2;
    public static final int ALLOWED = 4;
    public static final int ERROR = 6;
    public static final int NOT_EDITABLE = 8;
    
    private int mStyle;
    
    public FormLabel(){
        setPrefWidth(100);
    }
    
    public void setGraphic(int style){
        switch(style){
            // NOTICE that getStyleClass().clear() is always called for each
            // case. The logic is that this FormLabel changes its state from
            // time to time. If getStyleClass().clear() is not called, the styles
            // will stack up and that will make this FormLabel look weird.
            case TOO_SHORT:
                getStyleClass().clear();
                getStyleClass().add("form-label-error");
                setText("Too Short");
                break;
            case ALREADY_EXISTS:
                getStyleClass().clear();
                getStyleClass().add("form-label-error");
                setText("Already Exists");
                break;
            case ALLOWED:
                getStyleClass().clear();
                getStyleClass().add("form-check");
                setText("");
                break;
            case ERROR:
                getStyleClass().clear();
                getStyleClass().add("form-error");
                setText("");
                break;
            case NOT_EDITABLE:
                getStyleClass().clear();
                getStyleClass().add("form-label-error");
                setText("Not Editable");
                break;
            default:
                getStyleClass().clear();
                setText("");
        }
        mStyle = style;
    }
    
    public int getLabelStyle(){
        return mStyle;
    }
}
