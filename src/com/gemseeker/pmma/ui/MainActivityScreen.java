package com.gemseeker.pmma.ui;

import com.gemseeker.pmma.ControlledScreen;
import com.gemseeker.pmma.ScreenController;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 *
 * @author RAFIS-FRED
 */
public class MainActivityScreen extends ScreenController {
    
    @FXML StackPane stackPane;
    @FXML HBox toolbarLeftBox;
    @FXML HBox toolbarRightBox;
    @FXML ToggleButton toggleDash;
    @FXML ToggleButton toggleProjects;
    @FXML ToggleButton toggleReports;
    @FXML ToggleButton toggleSettings;
    
    private Parent parentView;
    private ToggleGroup toggleGroup;
    private ControlledScreen currentScreen;
    
    private MaterialButton signInBtn;
    
    // screens
    private DashboardScreen dashboardScreen;
    private ProjectsScreen projectsScreen;

    public MainActivityScreen() {
        setScreenContainer(stackPane);
        initComponents();
        dashboardScreen = new DashboardScreen();
        projectsScreen = new ProjectsScreen();
        
        loadScreen("dashboard", dashboardScreen);
        loadScreen("projects", projectsScreen);
        
        setScreen("dashboard");
    }
    
    @Override
    public void setScreen(String key) {
        if(screens.containsKey(key) && currentScreen != screens.get(key)){
            ControlledScreen screen = screens.get(key);
            Node content = screen.getContentView();
            
            // make sure that content is visible and on the bottom of the UI screen
            content.setOpacity(1);
            content.setTranslateY(stackPane.getHeight());
            stackPane.getChildren().add(0, content);
            
            ParallelTransition anim = new ParallelTransition();
            
            TranslateTransition translateTrans = new TranslateTransition(new Duration(300), content);
            translateTrans.setToY(0.0);
            translateTrans.setInterpolator(Interpolator.EASE_OUT);
            translateTrans.setAutoReverse(false);
            translateTrans.setOnFinished(evt->{
                if(currentScreen != null){
                    stackPane.getChildren().remove(currentScreen.getContentView());
                }
                currentScreen = screen;
            });
            
            if(currentScreen != null){
                Node curr = currentScreen.getContentView();
                FadeTransition fadeOut = new FadeTransition(new Duration(200), curr);
                fadeOut.setToValue(0.0);
                fadeOut.setInterpolator(Interpolator.EASE_OUT);
                fadeOut.setAutoReverse(false);
                anim.getChildren().add(fadeOut);
            }
            
            anim.getChildren().add(translateTrans);
            anim.play();
        }
    }

    @Override
    public ControlledScreen getCurrentScreen() {
        return currentScreen;
    }

    @Override
    public ScreenController onBackPressed() {
        return this;
    }

    public Parent getContentView(){
        return parentView;
    }
    
    private void initComponents(){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("mainview.fxml"));
        loader.setController(MainActivityScreen.this);
        try{
            loader.load();
            parentView = (Parent)loader.getRoot();
        }catch(IOException e){
            System.err.println("Error loading main UI. Terminating application.");
            System.exit(1);
        }
        
        // sets the toggle group
        toggleGroup = new ToggleGroup();
        toggleDash.setToggleGroup(toggleGroup);
        toggleProjects.setToggleGroup(toggleGroup);
        toggleReports.setToggleGroup(toggleGroup);
        toggleSettings.setToggleGroup(toggleGroup);
        
        // This line of codes below makes sure that the ToggleButton will not
        // be toggled when clicked if it is still selected. First ToggleButtons
        // are added to a list then used the list to iterate all the ToggleButtons
        // to add an event filter.
        ArrayList<ToggleButton> btns = new ArrayList<>(Arrays.asList(
                toggleDash,
                toggleProjects,
                toggleReports,
                toggleSettings
        ));
        btns.stream().forEach((tb) -> {
            tb.addEventFilter(MouseEvent.MOUSE_PRESSED, evt->{
                if(tb.isSelected()){
                    evt.consume();
                }
            });
        });
        
        // toggle change listener sets the current screen to load
        toggleGroup.selectedToggleProperty()
                .addListener((ObservableValue<? extends Toggle> observable,
                        Toggle oldValue,
                        Toggle newValue) -> {
                    
            if(newValue.equals(toggleProjects)){
                setScreen("projects");
            }else if(newValue.equals(toggleReports)){
                
            }else if(newValue.equals(toggleSettings)){
                
            }else{
                setScreen("dashboard");
            }
        });
        
        signInBtn = new MaterialButton();
        signInBtn.setText("Sign In");
        toolbarRightBox.getChildren().add(signInBtn);
    }
}
