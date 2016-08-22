package com.gemseeker.pmma.fxml;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 * A class for loading FXML files on this package. Since my goal is to separate
 * FXML files from the controllers, I have to define a class that uses a
 * FXMLLoader for loading and retrieving the content of FXML files. My
 * controllers will not have to worry how the FXML file is loaded, it will just
 * call the loadScreen() method that returns a Parent object. The loadScreen()
 * method takes a String value of FXML file name.
 *
 * @author RAFIS-FRED
 */
public class ScreenLoader {
    /**
     * Loads the FXML file from a String resource. String resource is SHOULD
     * not be a path.
     * @param controller
     * @param resource
     * @return 
     */
    public static Parent loadScreen(Object controller, String resource) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ScreenLoader.class.getResource(resource));
        loader.setController(controller);
        try {
            loader.load();
            return loader.getRoot();
        } catch (IOException e) {
            System.err.println("Failed to load FXML file: " + resource);
            System.err.println("--> " + e.getLocalizedMessage());
            return null;
        }
    }
}
