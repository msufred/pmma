package com.gemseeker.pmma.controllers;

import com.gemseeker.pmma.ui.components.MaterialButton;
import com.gemseeker.pmma.ControlledScreen;
import com.gemseeker.pmma.ScreenController;
import com.gemseeker.pmma.animations.EasingMode;
import com.gemseeker.pmma.animations.ExponentialInterpolator;
import com.gemseeker.pmma.data.Contact;
import com.gemseeker.pmma.data.Coordinate;
import com.gemseeker.pmma.data.DBManager;
import com.gemseeker.pmma.data.History;
import com.gemseeker.pmma.data.Location;
import com.gemseeker.pmma.data.Project;
import com.gemseeker.pmma.data.ProjectContact;
import com.gemseeker.pmma.fxml.ScreenLoader;
import java.util.List;
import java.util.stream.Collectors;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 *
 * @author RAFIS-FRED
 */
public class MainActivityScreen extends ScreenController {

    public static final String DEBUG_NAME = "MainActivityScreen";
    
    // This will be the position of the components on the container.
    private static final int PROGRESS_INDICATOR_INDEX = 0;
    private static final int NEXT_SCREEN_INDEX = 2;
    private static final int PREVIOUS_SCREEN_INDEX = 1;

    static final Duration TRANSITION_DURATION = new Duration(400);
    static final Interpolator IN_INTERPOLATOR = new ExponentialInterpolator(EasingMode.EASE_IN);
    static final Interpolator OUT_INTERPOLATOR = new ExponentialInterpolator(EasingMode.EASE_OUT);
    static final double SCREEN_OFFSET = 8.0;

    @FXML StackPane stackPane;
    @FXML ToggleButton toggleDash;
    @FXML ToggleButton toggleProjects;
    @FXML ToggleButton toggleContacts;
    @FXML ToggleButton toggleReports;
    @FXML ToggleButton toggleSettings;
    @FXML Rectangle rectangle;
    private ProgressIndicator progressIndicator;

    private final Parent parentView;
    private ToggleGroup toggleGroup;
    private MaterialButton signInBtn;
    private ControlledScreen currentScreen;

    // screens
    private DashboardScreen dashboardScreen;
    private ProjectsScreen projectsScreen;
    private ViewProjectScreen viewProjectScreen;
    private ContactsScreen contactsScreen;
    private ReportsScreen reportsScreen;
    private SettingsScreen settingsScreen;

    // data
    private final ObservableList<Project> projects = FXCollections.observableArrayList();
    private final ObservableList<Location> locations = FXCollections.observableArrayList();
    private final ObservableList<History> histories = FXCollections.observableArrayList();
    private final ObservableList<Coordinate> coordinates = FXCollections.observableArrayList();
    private final ObservableList<Contact> contacts = FXCollections.observableArrayList();
    private final ObservableList<ProjectContact> projectContacts = FXCollections.observableArrayList();

    /**
     * A boolean that is used to notify the application of a pending operation
     * that must be either completed or cancel.
     */
    private boolean hasPendingOperation = false;
    private boolean isAnimated = true;
    private Pane overlay;
    
    private Task loadDataTask;
    private boolean isDataLoaded = false;

    public MainActivityScreen() {
        parentView = ScreenLoader.loadScreen(MainActivityScreen.this, "mainview.fxml");
        setScreenContainer(stackPane);
        initComponents();
        loadData();
    }

     private void initComponents() {
        // sets the toggle group
        toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().addAll(toggleDash, toggleProjects, toggleReports,
                toggleContacts, toggleSettings);

        // If Toggle is already selected or hasPendingOperation is true, consume
        // the event (user can't click the Toggle).
        toggleGroup.getToggles().stream().forEach((tb) -> {
            ((ToggleButton)tb).addEventFilter(MouseEvent.MOUSE_PRESSED, evt -> {
                if (tb.isSelected() || hasPendingOperation) {
                    evt.consume();
                }
            });
        });

        // toggle change listener sets the current screen to load
        toggleGroup.selectedToggleProperty()
                .addListener((ObservableValue<? extends Toggle> observable,
                        Toggle oldValue,
                        Toggle newValue) -> {
            // clear BackStack
            getBackStack().clear();
            if (newValue.equals(toggleProjects)) {
                setScreen(ProjectsScreen.NAME);
            } else if(newValue.equals(toggleContacts)){
                setScreen(ContactsScreen.NAME);
            } else if (newValue.equals(toggleReports)) {
                setScreen(ReportsScreen.NAME);
            } else if (newValue.equals(toggleSettings)) {
                setScreen(SettingsScreen.NAME);
            } else {
                setScreen(DashboardScreen.NAME);
            }
            moveIndicator(((ToggleButton)newValue).getBoundsInParent().getMinY());
        });
        
        progressIndicator = new ProgressIndicator();
        progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        progressIndicator.setMaxSize(48, 48);

        // Task for loading data to be called by loadData() method
        loadDataTask = new Task(){
            @Override
            protected Object call() throws Exception {
                // add progress indicator to stackPane
                stackPane.getChildren().add(progressIndicator);
                
                projects.setAll(DBManager.getProjects());
                locations.setAll(DBManager.getLocations());
                histories.setAll(DBManager.getHistories());
                coordinates.setAll(DBManager.getCoordinates());
                contacts.setAll(DBManager.getContacts());
                projectContacts.setAll(DBManager.getProjectContacts());
                
                setupProjectContactsData();
                setupProjectsData();
                createLoadScreens();
                
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();

                // remove the progress indicator and load dashboard screen
                stackPane.getChildren().remove(progressIndicator);
                setScreen(DashboardScreen.NAME);
                isDataLoaded = true;
            }

            @Override
            protected void failed() {
                super.failed(); 
                System.out.println("Failed to load data from database!");
                stackPane.getChildren().remove(progressIndicator);
                isDataLoaded = false;
            }
        };
    }

     /**
      * Retrieves data from database.
      */
    private void loadData() {
        Thread thread = new Thread(loadDataTask);
        thread.setDaemon(true);
        thread.start();
    }
    
    private void setupProjectContactsData(){
        projectContacts.stream().forEach(pc -> {
            for(Contact contact : contacts){
                if(contact.getContactId().equals(pc.getContactIdValue())){
                    pc.setContact(contact);
                    break;
                }
            }
        });
    }
    
    private void setupProjectsData() {
        for(Project project : projects){
            // contacts
            List<Contact> pContacts = projectContacts.stream()
                    .filter(pc -> pc.getProjectIdValue().equals(project.getIdValue()))
                    .map(pc -> pc.getContact())
                    .collect(Collectors.toList());
            project.setContacts(FXCollections.observableArrayList(pContacts));
            
            // set location
            for(Location location : locations){
                if(location.getId().equals(project.getLocationIdValue())){
                    project.setLocationId(location.getId());
                    project.setLocation(location);
                    break;
                }
            }
            // histories
            List<History> histList = histories.stream()
                    .filter(hist -> hist.getProjectId().equals(project.getIdValue()))
                    .collect(Collectors.toList());
            project.setHistories(FXCollections.observableArrayList(histList));
            
            // coordinates
            List<Coordinate> coords = coordinates.stream()
                    .filter(coord -> coord.getProjectId().equals(project.getIdValue()))
                    .collect(Collectors.toList());
            project.setCoordinates(FXCollections.observableArrayList(coords));
            
        }
    }
    
    private void createLoadScreens(){
        // initialize screens
        dashboardScreen = new DashboardScreen();
        projectsScreen = new ProjectsScreen();
        viewProjectScreen = new ViewProjectScreen();
        contactsScreen = new ContactsScreen();
        reportsScreen = new ReportsScreen();
        settingsScreen = new SettingsScreen();

        loadScreen(dashboardScreen);
        loadScreen(projectsScreen);
        loadScreen(viewProjectScreen);
        loadScreen(contactsScreen);
        loadScreen(reportsScreen);
        loadScreen(settingsScreen);
    }
    
    private void moveIndicator(double to){
        Timeline anim = new Timeline(new KeyFrame(TRANSITION_DURATION,
                new KeyValue(rectangle.translateYProperty(), to, OUT_INTERPOLATOR)
        ));
        anim.play();
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

    /***************************************************************************
    *                           Setters and Getters                            *
    ***************************************************************************/
    /**
     * 
     * @return 
     */
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
    
    /***************************************************************************
     *                  ScreenController Overridden Methods                    *
     ***************************************************************************/
    /**
     * 
     * @param key 
     */
    @Override
    public void setScreen(String key) {
        ControlledScreen screen = getScreen(key);
        // check if screens contains the screen to load and if current screen
        // is not the screen to load
        if (!getScreens().containsKey(key) || getCurrentScreen() == screen) {
            return;
        }

        Node content = screen.getContentView();
        // simply add screen if current screen is null (that means there
        // is no screen loaded yet)
        if (currentScreen == null) {
            stackPane.getChildren().add(content);
            screen.doOnResume();
            currentScreen = screen;
            return;
        }
        
        // block all input events and processes
        hasPendingOperation = true;

        if (isAnimated) {
            Transition transition;
            if (currentScreen.isChild()) {
                // translate the currentScreen downwards
                TranslateTransition trans = new TranslateTransition(new Duration(200), currentScreen.getContentView());
                trans.setToY(stackPane.getHeight() + SCREEN_OFFSET);
                trans.setInterpolator(IN_INTERPOLATOR);

                // fade in the screen to load
                FadeTransition fadeIn = new FadeTransition(TRANSITION_DURATION, content);
                fadeIn.setToValue(1.0);
                fadeIn.setInterpolator(IN_INTERPOLATOR);

                transition = new ParallelTransition();
                ((ParallelTransition) transition).getChildren().addAll(trans, fadeIn);

                // add the screen to load below the current screen
                stackPane.getChildren().add(0, content);
                transition.setOnFinished(evt -> {
                    currentScreen.doOnPause();
                    stackPane.getChildren().remove(1);
                    screen.doOnResume();
                    currentScreen = screen;
                    
                    // allow input events and processes
                    hasPendingOperation = false;
                });
            } else {
                // set initial opacity and position
                content.setOpacity(1);
                content.setTranslateY(stackPane.getHeight() + SCREEN_OFFSET);

                // translate screen to load upwards
                TranslateTransition trans = new TranslateTransition(TRANSITION_DURATION, content);
                trans.setToY(0);
                trans.setInterpolator(OUT_INTERPOLATOR);

                // fade out the current screen
                FadeTransition fadeOut = new FadeTransition(new Duration(200), currentScreen.getContentView());
                fadeOut.setToValue(0.0);
                fadeOut.setInterpolator(OUT_INTERPOLATOR);

                transition = new ParallelTransition();
                ((ParallelTransition) transition).getChildren().addAll(trans, fadeOut);

                // add the screen to load above the current screen
                stackPane.getChildren().add(content);
                transition.setOnFinished(evt -> {
                    currentScreen.doOnPause();
                    stackPane.getChildren().remove(0);
                    screen.doOnResume();
                    currentScreen = screen;
                    
                    // allow input events and processes
                    hasPendingOperation = false;
                });
            } // end IF currentScreen is child
            transition.play();
        } else {
            hasPendingOperation = true;
            {
                stackPane.getChildren().remove(currentScreen.getContentView());
                currentScreen.doOnPause();
                stackPane.getChildren().add(content);
                screen.doOnResume();
                currentScreen = screen;
            }
            hasPendingOperation = false;
        }
    }
}
