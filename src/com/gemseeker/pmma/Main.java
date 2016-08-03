package com.gemseeker.pmma;

import com.gemseeker.pmma.ui.MainActivityScreen;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author RAFIS-FRED
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        MainActivityScreen root = new MainActivityScreen();
        Scene scene = new Scene((Parent) root.getContentView());
        scene.getStylesheets()
                .add(getClass()
                .getResource("css/styles.css")
                .toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
