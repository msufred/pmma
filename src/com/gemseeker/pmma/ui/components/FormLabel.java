package com.gemseeker.pmma.ui.components;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.shape.SVGPath;

/**
 *
 * @author u
 */
public class FormLabel extends Label {
    
    public static final int TOO_SHORT = 0;
    public static final int ALREADY_EXISTS = 1;
    public static final int ALLOWED = 2;
    public static final int ERROR = 3;
    
    private int mStyle;
    
    public FormLabel(){
        setPrefWidth(60);
    }
    
    public void setGraphic(int style){
        switch(style){
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
