package com.gemseeker.pmma.maps;

import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 *
 * @author RAFIS-FRED
 */
public class GoogleMapView {

    private final WebView webView;
    private final WebEngine webEngine;
    
    public GoogleMapView(){
        webView = new WebView();
        webEngine = webView.getEngine();
    }
    
    public void load(){
        webEngine.load(getClass().getResource("google.html").toExternalForm());
    }
}
