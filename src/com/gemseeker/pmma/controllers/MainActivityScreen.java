package com.gemseeker.pmma.controllers;

import com.gemseeker.pmma.AppConstants;
import static com.gemseeker.pmma.AppConstants.IN_ANIM_INTERPOLATOR;
import static com.gemseeker.pmma.AppConstants.OUT_ANIM_INTERPOLATOR;
import com.gemseeker.pmma.controllers.viewprojects.ViewProjectScreen;
import com.gemseeker.pmma.ControlledScreen;
import com.gemseeker.pmma.ScreenController;
import com.gemseeker.pmma.fxml.ScreenLoader;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * The core of the application. MainActivityScreen is a subclass of a ScreenController on which
 * has the function to manage all ControlledScreen objects. MainActivityScreen will not have to
 * access the database (for retrieving, adding data etc). All database access should be done
 * by ControlledScreen objects.
 *
 * @author RAFIS-FRED
 */
public class MainActivityScreen extends ScreenController {

    public static final String DEBUG_NAME = "MainActivityScreen";

    static final Duration TRANSITION_DURATION = new Duration(450);
    static final double SCREEN_OFFSET = 8.0;

    @FXML StackPane stackPane;

    // sidebar menu components is composed of ToggleButtons
    @FXML ToggleButton toggleProjects;
    @FXML ToggleButton toggleContacts;
    @FXML ToggleButton toggleCalendar;
    @FXML ToggleButton toggleReports;
    @FXML ToggleButton toggleHistories;
    @FXML ToggleButton toggleSettings;
    @FXML Rectangle rectangle; // sidebar indicator

    private final Parent parentView;
    private ToggleGroup toggleGroup;

    // holds the current ControlledScreen visible to the user
    private ControlledScreen currentScreen;

    // screens
    private ProjectsScreen projectsScreen;
    private ViewProjectScreen viewProjectScreen;
    private ContactsScreen contactsScreen;
    private CalendarScreen calendarScreen;
    private HistoriesScreen historiesScreen;
    private ReportsScreen reportsScreen;
    private SettingsScreen settingsScreen;

    /**
     * A boolean that is used to notify the application of a pending operation
     * that must be either completed or cancel.
     */
    private boolean hasPendingOperation = false;
    private boolean isAnimated = true;
    private Pane overlay;

    public MainActivityScreen() {
        parentView = ScreenLoader.loadScreen(MainActivityScreen.this, "mainview.fxml");
        setScreenContainer(stackPane);
        initComponents();
        createLoadScreens();
        setScreen(ProjectsScreen.NAME);
    }

     private void initComponents() {

        // sets the toggle group
        toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().addAll(toggleProjects, toggleContacts, toggleCalendar,
                toggleReports, toggleHistories, toggleSettings);

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
            } else if (newValue.equals(toggleCalendar)) {
                setScreen(CalendarScreen.NAME);
            } else if (newValue.equals(toggleHistories)) {
                setScreen(HistoriesScreen.NAME);
            } else if (newValue.equals(toggleReports)) {
                setScreen(ReportsScreen.NAME);
            } else if (newValue.equals(toggleSettings)) {
                setScreen(SettingsScreen.NAME);
            }
            moveIndicator(((ToggleButton)newValue).getBoundsInParent().getMinY());
        });
    }
    
    private void createLoadScreens(){
        // initialize screens
        projectsScreen = new ProjectsScreen();
        viewProjectScreen = new ViewProjectScreen();
        contactsScreen = new ContactsScreen();
        calendarScreen = new CalendarScreen();
        historiesScreen = new HistoriesScreen();
        reportsScreen = new ReportsScreen();
        settingsScreen = new SettingsScreen();

        loadScreen(projectsScreen);
        loadScreen(viewProjectScreen);
        loadScreen(contactsScreen);
        loadScreen(calendarScreen);
        loadScreen(historiesScreen);
        loadScreen(reportsScreen);
        loadScreen(settingsScreen);
    }
    
    private void moveIndicator(double to){
        Timeline anim = new Timeline(new KeyFrame(new Duration(400),
                new KeyValue(rectangle.translateYProperty(), to, AppConstants.IN_ANIM_INTERPOLATOR)
        ));
        anim.play();
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
    
    // <editor-fold defaultstate="collapsed" desc="use the default setScreen method without transition animation">
    
    @Override
    public void setScreen(String key) {
        ControlledScreen screen = getScreen(key);
        // check if screens contains the screen to load and if current screen
        // is not the screen to load
        if (screen == null || getCurrentScreen() == screen) {
            return;
        }

        Node content = screen.getContentView();
        // simply add screen if current screen is null (that means there
        // is no screen loaded yet)
        if (currentScreen == null) {
            stackPane.getChildren().add(content);
            screen.onResume();
            currentScreen = screen;
            return;
        }
        
        // block all input events and processes
        hasPendingOperation = true;

        if (isAnimated) {
            Transition transition;
            /*
             * If current screen is a child, meaning a screen within a screen, the
             * animation is set to translate the screen downwards.
             */
            if (currentScreen.isChild()) {
                // translate the currentScreen downwards
                TranslateTransition trans = new TranslateTransition(new Duration(300), currentScreen.getContentView());
                trans.setToY(stackPane.getHeight() + SCREEN_OFFSET);
                trans.setInterpolator(OUT_ANIM_INTERPOLATOR);

                // fade in the screen to load
                FadeTransition fadeIn = new FadeTransition(TRANSITION_DURATION, content);
                fadeIn.setToValue(1.0);
                fadeIn.setInterpolator(OUT_ANIM_INTERPOLATOR);

                transition = new ParallelTransition();
                ((ParallelTransition) transition).getChildren().addAll(trans, fadeIn);

                // add the screen to load below the current screen
                stackPane.getChildren().add(0, content);
                transition.setOnFinished(evt -> {
                    currentScreen.onPause();
                    stackPane.getChildren().remove(1);
                    screen.onResume();
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
                trans.setToY(0.0);
                trans.setInterpolator(IN_ANIM_INTERPOLATOR);

                // fade out the current screen
                FadeTransition fadeOut = new FadeTransition(new Duration(300), currentScreen.getContentView());
                fadeOut.setToValue(0.0);
                fadeOut.setInterpolator(IN_ANIM_INTERPOLATOR);

                transition = new ParallelTransition();
                ((ParallelTransition) transition).getChildren().addAll(trans, fadeOut);

                // add the screen to load above the current screen
                stackPane.getChildren().add(content);
                transition.setOnFinished(evt -> {
                    currentScreen.onPause();
                    stackPane.getChildren().remove(0);
                    screen.onResume();
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
                currentScreen.onPause();
                stackPane.getChildren().add(content);
                screen.onResume();
                currentScreen = screen;
            }
            hasPendingOperation = false;
        } // -- end IF ANIMATED
    }
    
    // </editor-fold>
}
