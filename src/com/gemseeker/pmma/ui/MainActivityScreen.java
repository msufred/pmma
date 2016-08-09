package com.gemseeker.pmma.ui;

import com.gemseeker.pmma.ui.components.MaterialButton;
import com.gemseeker.pmma.ControlledScreen;
import com.gemseeker.pmma.ScreenController;
import com.gemseeker.pmma.data.Contractor;
import com.gemseeker.pmma.data.Coordinate;
import com.gemseeker.pmma.data.DBManager;
import com.gemseeker.pmma.data.History;
import com.gemseeker.pmma.data.Location;
import com.gemseeker.pmma.data.Project;
import com.gemseeker.pmma.ui.components.IconButton;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
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
    private MaterialButton dropBtn;
    
    @FXML ToggleButton toggleDash;
    @FXML ToggleButton toggleProjects;
    @FXML ToggleButton toggleReports;
    @FXML ToggleButton toggleSettings;

    private Parent parentView;
    private ToggleGroup toggleGroup;
    private MaterialButton signInBtn;
    private ControlledScreen currentScreen;

    // screens
    private final DashboardScreen dashboardScreen;
    private final ProjectsScreen projectsScreen;
    private final AddProjectScreen addProjectScreen;
    private final ViewProjectScreen viewProjectScreen;
    private final ReportsScreen reportsScreen;
    private final SettingsScreen settingsScreen;

    // data
    private ObservableList<Project> projects = FXCollections.observableArrayList();
    private ObservableList<Location> locations = FXCollections.observableArrayList();
    private ObservableList<History> histories = FXCollections.observableArrayList();
    private ObservableList<Coordinate> coordinates = FXCollections.observableArrayList();
    private ObservableList<Contractor> contractors = FXCollections.observableArrayList();

    /**
     * A boolean that is used to notify the application of a pending operation
     * that must be either completed or cancel.
     */
    private boolean hasPendingOperation = false;
    private boolean isAnimated = true;

    public MainActivityScreen() {
        setScreenContainer(stackPane);
        initComponents();

        projects.setAll(DBManager.getProjects());
        locations.setAll(DBManager.getLocations());
        histories.setAll(DBManager.getHistories());
        coordinates.setAll(DBManager.getCoordinates());
        contractors.setAll(DBManager.getContractors());

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
            
            // contractor
            for(Contractor contractor : contractors){
                if(project.getContractorId().equals(contractor.getId())){
                    project.setContractorId(contractor.getId());
                    project.setContractor(contractor);
                    break;
                }
            }
        }
        
        // initialize screens
        dashboardScreen = new DashboardScreen();
        projectsScreen = new ProjectsScreen();
        addProjectScreen = new AddProjectScreen();
        viewProjectScreen = new ViewProjectScreen();
        reportsScreen = new ReportsScreen();
        settingsScreen = new SettingsScreen();

        loadScreen(dashboardScreen);
        loadScreen(projectsScreen);
        loadScreen(addProjectScreen);
        loadScreen(viewProjectScreen);
        loadScreen(reportsScreen);
        loadScreen(settingsScreen);

        setScreen(DashboardScreen.NAME);
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
            if (!screens.containsKey(key) || currentScreen == screens.get(key)) {
                return;
            }

            ControlledScreen screen = screens.get(key);
            Node content = screen.getContentView();

            // simply add screen if current screen is null (that means there
            // is no screen loaded yet)
            if (currentScreen == null) {
                stackPane.getChildren().add(content);
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
                    stackPane.getChildren().add(1, content);
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
                currentScreen = screen;
            }
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

    public Parent getContentView() {
        return parentView;
    }

    private void initComponents() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("mainview.fxml"));
        loader.setController(MainActivityScreen.this);
        try {
            loader.load();
            parentView = (Parent) loader.getRoot();
        } catch (IOException e) {
            System.err.println("Error loading main UI. Terminating application.");
            System.exit(1);
        }
        
        // TODO: init imageView, usernameLabel and dropBtn here
        imageView.setImage(new Image(getClass().getResourceAsStream("gem.png")));
        dropBtn = new MaterialButton();
        dropBtn.setText("Gem Seeker");
        dropBtn.getStyleClass().add("paper-pink-button");
        dropBtn.setFlated(false);
        usernameBox.getChildren().add(dropBtn);

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
            tb.addEventFilter(MouseEvent.MOUSE_PRESSED, evt -> {
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
                    if (newValue.equals(toggleProjects)) {
                        setScreen(ProjectsScreen.NAME);
                    } else if (newValue.equals(toggleReports)) {
                        setScreen(ReportsScreen.NAME);
                    } else if (newValue.equals(toggleSettings)) {
                        setScreen(SettingsScreen.NAME);
                    } else {
                        setScreen(DashboardScreen.NAME);
                    }
                });

        /*
        signInBtn = new MaterialButton();
        signInBtn.setText("Sign In");
        signInBtn.getStyleClass().add("paper-pink-button");
        toolbarRightBox.getChildren().add(signInBtn);
        */
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
    
    public ObservableList<Contractor> getContractors() {
        return contractors;
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
