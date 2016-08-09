package com.gemseeker.pmma;

import com.gemseeker.pmma.ui.MainActivityScreen;
import com.gemseeker.pmma.utils.Utils;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author RAFIS-FRED
 */
public class Main extends Application {

    public static final boolean VERBOSE = true;
    
    public static void main(String[] args) {
        if(VERBOSE){
            System.out.println("VERBOSE MODE");
        }else{
            System.out.println("Set to log file!");
            setToLogFile();
            
            // TODO: Load or create properties file. Properties file contains
            // the Settings parameters for the application.
        }
        launch(args);
    }
        
    // This method sets the log to a file. If the file do not exist, create one.
    private static void setToLogFile(){
        File logdir = new File("logs");
        if (!logdir.exists() || !logdir.isDirectory()) {
            logdir.mkdir();
        }
        String name = logdir.getAbsolutePath() 
                + File.separatorChar 
                + Utils.LOG_DATE_FORMAT.format(new Date()) 
                + ".txt";
        File log = new File(name);
        try {
            log.createNewFile();
            PrintStream outstream = new PrintStream(log);
            System.setOut(outstream);
            System.setErr(outstream);
        } catch (IOException e) {
            System.err.println("Failed to create log directory.\n\t[" + e + "]");
        }
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        MainActivityScreen root = new MainActivityScreen();
        Scene scene = new Scene((Parent) root.getContentView());
        scene.getStylesheets()
                .addAll(getClass().getResource("css/styles.css").toExternalForm(),
                        getClass().getResource("css/controls.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
