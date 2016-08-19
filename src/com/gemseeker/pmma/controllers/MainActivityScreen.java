package com.gemseeker.pmma.controllers;

import com.gemseeker.pmma.ui.components.MaterialButton;
import com.gemseeker.pmma.ControlledScreen;
import com.gemseeker.pmma.ScreenController;
import com.gemseeker.pmma.data.Contact;
import com.gemseeker.pmma.data.Coordinate;
import com.gemseeker.pmma.data.DBManager;
import com.gemseeker.pmma.data.History;
import com.gemseeker.pmma.data.Location;
import com.gemseeker.pmma.data.Project;
import com.gemseeker.pmma.fxml.ScreenLoader;
import java.util.List;
import java.util.stream.Collectors;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 *
 * @author RAFIS-FRED
 */
public class MainActivityScreen extends ScreenController {

    public static final String DEBUG_NAME = "MainActivityScreen";

    static final Duration TRANSITION_DURATION = new Duration(300);
    static final double SCREEN_OFFSET = 8.0;

    @FXML StackPane stackPane;
    @FXML HBox toolbarLeftBox;
    @FXML HBox toolbarRightBox;
    
    @FXML ImageView imageView;
    @FXML HBox usernameBox;
    
    @FXML ToggleButton toggleDash;
    @FXML ToggleButton toggleProjects;
    @FXML ToggleButton toggleContacts;
    @FXML ToggleButton toggleReports;
    @FXML ToggleButton toggleSettings;

    private final Parent parentView;
    private ToggleGroup toggleGroup;
    private MaterialButton signInBtn;
    private ControlledScreen currentScreen;

    // screens
    private final DashboardScreen dashboardScreen;
    private final ProjectsScreen projectsScreen;
    private final ReportsScreen reportsScreen;
    private final SettingsScreen settingsScreen;
    private final AddProjectScreen addProjectScreen;

    // data
    private final ObservableList<Project> projects = FXCollections.observableArrayList();
    private final ObservableList<Location> locations = FXCollections.observableArrayList();
    private final ObservableList<History> histories = FXCollections.observableArrayList();
    private final ObservableList<Coordinate> coordinates = FXCollections.observableArrayList();
    private final ObservableList<Contact> contacts = FXCollections.observableArrayList();

    /**
     * A boolean that is used to notify the application of a pending operation
     * that must be either completed or cancel.
     */
    private boolean hasPendingOperation = false;
    private boolean isAnimated = true;
    private Pane overlay;
    
    private boolean dataIsLoaded = false;

    public MainActivityScreen() {
        parentView = ScreenLoader.loadScreen(MainActivityScreen.this, "mainview.fxml");
        setScreenContainer(stackPane);
        initComponents();
        
        initData();
        initComponentData();
        
        // initialize screens
        dashboardScreen = new DashboardScreen();
        projectsScreen = new ProjectsScreen();
        reportsScreen = new ReportsScreen();
        settingsScreen = new SettingsScreen();
        addProjectScreen = new AddProjectScreen();

        loadScreen(dashboardScreen);
        loadScreen(projectsScreen);
        loadScreen(reportsScreen);
        loadScreen(settingsScreen);
        loadScreen(addProjectScreen);

        setScreen(DashboardScreen.NAME);
    }

     private void initComponents() {
        // TODO: init imageView, usernameLabel and dropBtn here
        imageView.setImage(new Image(getClass().getResourceAsStream("gem.png")));

        // sets the toggle group
        toggleGroup = new ToggleGroup();
        toggleDash.setToggleGroup(toggleGroup);
        toggleProjects.setToggleGroup(toggleGroup);
        toggleContacts.setToggleGroup(toggleGroup);
        toggleReports.setToggleGroup(toggleGroup);
        toggleSettings.setToggleGroup(toggleGroup);

        // This line of codes below makes sure that the ToggleButton will not
        // be toggled when clicked if it is still selected. First ToggleButtons
        // are added to a list then used the list to iterate all the ToggleButtons
        // to add an event filter.
        toggleGroup.getToggles().stream().forEach((tb) -> {
            ((ToggleButton)tb).addEventFilter(MouseEvent.MOUSE_PRESSED, evt -> {
                if (tb.isSelected()) {
                    evt.consume();
                }
            });
        });

        // toggle change listener sets the current screen to load
        // NOTE: Clear the ScreenBackStack ALWAYS when a toggle is selected.
        toggleGroup.selectedToggleProperty()
                .addListener((ObservableValue<? extends Toggle> observable,
                        Toggle oldValue,
                        Toggle newValue) -> {
                    getBackStack().clear();
                    setOnSetScreenEvent(null);
                    if (newValue.equals(toggleProjects)) {
                        setScreen(ProjectsScreen.NAME);
                    } else if(newValue.equals(toggleContacts)){
                        
                    } else if (newValue.equals(toggleReports)) {
                        setScreen(ReportsScreen.NAME);
                    } else if (newValue.equals(toggleSettings)) {
                        setScreen(SettingsScreen.NAME);
                    } else {
                        setScreen(DashboardScreen.NAME);
                    }
                });

        overlay = new Pane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4);");
    }

     /**
      * Retrieves data from database.
      */
    private void initData() {
        Task t = new Task(){
            @Override
            public Object call(){
                projects.setAll(DBManager.getProjects());
                locations.setAll(DBManager.getLocations());
                histories.setAll(DBManager.getHistories());
                coordinates.setAll(DBManager.getCoordinates());
                contacts.setAll(DBManager.getContacts());
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                System.out.println("Done loading data!");
            }
            
            
        };
    }
    
    private void initComponentData() {
        for(Project project : projects){
            // set location
            for(Location location : locations){
                if(location.getId().equals(project.getLocationId())){
                    project.setLocationId(location.getId());
                    project.setLocation(location);
                    break;
                }
            }
            // histories
            List<History> histList = histories.stream()
                    .filter(hist -> hist.getProjectId().equals(project.getId()))
                    .collect(Collectors.toList());
            project.setHistories(FXCollections.observableArrayList(histList));
            
            // coordinates
            List<Coordinate> coords = coordinates.stream()
                    .filter(coord -> coord.getProjectId().equals(project.getId()))
                    .collect(Collectors.toList());
            project.setCoordinates(FXCollections.observableArrayList(coords));
            
        }
    }

    
    @Override
    public void setScreen(String key) {
        if (hasPendingOperation) {
            Alert alertDialog = new Alert(Alert.AlertType.CONFIRMATION,
                    "Are you sure you want to cancel current operation?",
                    ButtonType.YES, ButtonType.NO);
            alertDialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    hasPendingOperation = false;
                    setScreen(key);
                }
            });
        } else {
            // check if screens contains the screen to load and if current screen
            // is not the screen to load
            if (!getScreens().containsKey(key) || getCurrentScreen() == getScreen(key)) {
                return;
            }

            ControlledScreen screen = getScreen(key);
            Node content = screen.getContentView();

            // simply add screen if current screen is null (that means there
            // is no screen loaded yet)
            if (currentScreen == null) {
                stackPane.getChildren().add(content);
                screen.onResume();
                currentScreen = screen;
                return;
            }

            if (isAnimated) {
                Transition transition;
                if (currentScreen.isChild()) {
                    // translate the currentScreen downwards
                    TranslateTransition trans = new TranslateTransition(new Duration(200), currentScreen.getContentView());
                    trans.setToY(stackPane.getHeight() + SCREEN_OFFSET);
                    trans.setInterpolator(Interpolator.EASE_OUT);

                    // fade in the screen to load
                    FadeTransition fadeIn = new FadeTransition(TRANSITION_DURATION, content);
                    fadeIn.setToValue(1.0);
                    fadeIn.setInterpolator(Interpolator.EASE_OUT);

                    transition = new ParallelTransition();
                    ((ParallelTransition) transition).getChildren().addAll(trans, fadeIn);

                    // add the screen to load below the current screen
                    stackPane.getChildren().add(0, content);
                    transition.setOnFinished(evt -> {
                        currentScreen.onPause();
                        stackPane.getChildren().remove(1);
                        screen.onResume();
                        currentScreen = screen;
                        
                        if(getOnSetScreenEvent() != null){
                            getOnSetScreenEvent().handle(evt);
                        }
                    });
                } else {
                    // set initial opacity and position
                    content.setOpacity(1);
                    content.setTranslateY(stackPane.getHeight() + SCREEN_OFFSET);

                    // translate screen to load upwards
                    TranslateTransition trans = new TranslateTransition(TRANSITION_DURATION, content);
                    trans.setToY(0);
                    trans.setInterpolator(Interpolator.EASE_OUT);

                    // fade out the current screen
                    FadeTransition fadeOut = new FadeTransition(new Duration(200), currentScreen.getContentView());
                    fadeOut.setToValue(0.0);
                    fadeOut.setInterpolator(Interpolator.EASE_OUT);

                    transition = new ParallelTransition();
                    ((ParallelTransition) transition).getChildren().addAll(trans, fadeOut);

                    // add the screen to load above the current screen
                    stackPane.getChildren().add(content);
                    transition.setOnFinished(evt -> {
                        currentScreen.onPause();
                        stackPane.getChildren().remove(0);
                        screen.onResume();
                        currentScreen = screen;
                        
                        if(getOnSetScreenEvent() != null){
                            getOnSetScreenEvent().handle(evt);
                        }
                    });
                } // end IF currentScreen is child
                transition.play();
            } else {
                // if not animated, add the screen to load to the top, and remove
                // the previous one
                stackPane.getChildren().remove(0);
                stackPane.getChildren().add(0, content);
                screen.onResume();
                currentScreen = screen;
            }
        }
    }
    
    public void popUpScreen(String key){
        Node content = getScreens().get(key).getContentView();
        if(content != null){
            content.setScaleX(0.0);
            content.setScaleY(0.0);
            ScaleTransition scaleUp = new ScaleTransition(TRANSITION_DURATION, content);
            scaleUp.setByX(1.0);
            scaleUp.setByY(1.0);
            scaleUp.setInterpolator(Interpolator.EASE_OUT);
            stackPane.getChildren().add(content);
            scaleUp.play();
        }
    }
    
    public void closePopUpScreen(Node content){
        if(content != null){
            ScaleTransition scaleDown = new ScaleTransition(TRANSITION_DURATION, content);
            scaleDown.setByX(0.0);
            scaleDown.setByY(0.0);
            scaleDown.setInterpolator(Interpolator.EASE_OUT);
            scaleDown.setOnFinished(evt -> {
                stackPane.getChildren().remove(content);
            });
            scaleDown.play();
        }
    }

    @Override
    public ControlledScreen getCurrentScreen() {
        return currentScreen;
    }

    public Parent getContentView() {
        return parentView;
    }

   
    
    public ObservableList<Project> getProjects() {
        return projects;
    }

    public ObservableList<Location> getLocations() {
        return locations;
    }

    public ObservableList<History> getHistories() {
        return histories;
    }

    public ObservableList<Coordinate> getCoordinates() {
        return coordinates;
    }
    
    public ObservableList<Contact> getContacts() {
        return contacts;
    }
    
    public void setPendingOperationState(boolean hasPendingOperation) {
        this.hasPendingOperation = hasPendingOperation;
    }

    public boolean hasPendingOperation() {
        return hasPendingOperation;
    }

    public void setAnimated(boolean animated) {
        isAnimated = animated;
    }

    public boolean isAnimated() {
        return isAnimated;
    }
}
