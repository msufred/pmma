package com.gemseeker.pmma.ui.components;


import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Skin;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.SVGPath;

/**
 * Created by Felipe on 25/10/2015.
 */
public class IconButton extends ButtonBase {
	
    private SVGPath icon;
    private SVGPath iconBackground;
    private static final int SIZE = 32;
    private static final int PADDING = 12;

    private double mSize;
    
    public IconButton() {
        setSize(SIZE, SIZE);
        setPadding(PADDING);
        addEventFilter(MouseEvent.MOUSE_CLICKED,(MouseEvent evt)->{
            if(onActionProperty().get()!=null && evt.getButton().equals(MouseButton.PRIMARY)){
                onActionProperty().get().handle(new ActionEvent(this,null));
            }
        });
    }
    
    public void setSize(double width, double height) {
    	setPrefSize(width, height);
    	setMinSize(width, height);
    	setMaxSize(width, height);
    	
    	mSize = width;
    }
    
    public void setPadding(double padding) {
    	setPadding(new Insets(padding));
    }
    
    @Override
    public void fire() {

    }

    @Override
    protected Skin<?> createDefaultSkin() {

        IconButtonSkin skin = new IconButtonSkin(this);
        if(icon!=null) {
            skin.setIcon(icon);
        }
        if(iconBackground!=null) {
            skin.setIcon(iconBackground);
            iconBackground.setVisible(false);
        }
        RippleSkinFactory.getRippleEffect(skin, this);
        return skin;

    }

    public void setIcon(String path) {
        URL url = null;
        try {
            url = new URL(path);
            File file = new File(url.getFile());
            icon = SVGFactory.createSVG(file);
            icon.getStyleClass().add("icon-svg");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
    public void setIcon(InputStream file) {
        icon = SVGFactory.createSVG(file);

    }
    public void setIconBackground(InputStream file) {
       iconBackground = SVGFactory.createSVG(file);

    }
    public void setIconBackground(String path) {
        URL url = null;
        try {
            url = new URL(path);
            File file = new File(url.getFile());
            iconBackground = SVGFactory.createSVG(file);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public String getIcon() {
        return icon.getContent();
    }

    public String getIconBackground() {
        return iconBackground.getContent();
    }

    public SVGPath getIconBackgroundSVG() {
        return iconBackground;
    }

    public SVGPath getIconSVG() {
        return icon;
    }
    
    public void setIconSize(double size) {
    	if(size < mSize) {
//    		double scale = 
    	}
    }

}
