package com.gemseeker.pmma.controllers;

import com.gemseeker.pmma.ControlledScreen;
import com.gemseeker.pmma.animations.interpolators.EasingMode;
import com.gemseeker.pmma.animations.interpolators.ExponentialInterpolator;
import com.gemseeker.pmma.data.Contact;
import com.gemseeker.pmma.data.Project;
import com.gemseeker.pmma.data.ProjectUpdate;
import com.gemseeker.pmma.fxml.ScreenLoader;
import java.util.HashMap;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 *
 * @author u
 */
public class ViewProjectScreen extends ControlledScreen {

    public static final String NAME = "viewproject";
    private final HashMap<String, TabScreen> screens = new HashMap<>();
    static final Duration TRANSITION_DURATION = new Duration(400);
    static final Interpolator IN_INTERPOLATOR = new ExponentialInterpolator(EasingMode.EASE_IN);
    static final Interpolator OUT_INTERPOLATOR = new ExponentialInterpolator(EasingMode.EASE_OUT);
    static final double SCREEN_OFFSET = 8.0;

    @FXML Circle circle;
    @FXML ToggleButton toggleDetails;
    @FXML ToggleButton toggleUpdates;
    @FXML ToggleButton togglePhotos;
    @FXML ToggleButton toggleMaps;
    @FXML Rectangle rectangle;
    @FXML Label projectName;
    @FXML Label statusLabel;
    @FXML StackPane stackPane;
    @FXML StackPane container;
    private ToggleGroup toggleGroup;
    
    private DetailsTab detailsTab;
    private UpdatesTab updatesTab;
    private PhotosTab photosTab;
    private MapsTab mapsTab;
    
    private TabScreen current = null;
    
    private Project project;
    private Contact contact;
    private ObservableList<ProjectUpdate> updates;
    
    private ProgressIndicator progressIndicator;
    private boolean hasPendingOperation = false;

    public ViewProjectScreen() {
        super(NAME);
        initComponents();
        loadCreateTabViews();
        setAsChild(true);
    }

    private void initComponents() {
        setContentView(ScreenLoader.loadScreen(this, "view_project.fxml"));
        toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().addAll(
                toggleDetails, toggleUpdates, togglePhotos, toggleMaps
        );
        toggleGroup.getToggles().stream().forEach((tb) -> {
            ((ToggleButton)tb).addEventFilter(MouseEvent.MOUSE_PRESSED, evt -> {
                if (tb.isSelected() || hasPendingOperation) {
                    evt.consume();
                }
            });
        });
        toggleGroup.selectedToggleProperty()
                .addListener((ObservableValue<? extends Toggle> ov, Toggle old, Toggle newValue) -> {
            if(newValue.equals(toggleUpdates)){
                setScreen(UpdatesTab.NAME);
            }else if(newValue.equals(togglePhotos)){
                setScreen(PhotosTab.NAME);
            }else if(newValue.equals(toggleMaps)){
                setScreen(MapsTab.NAME);
            }else{
                setScreen(DetailsTab.NAME);
            }
            moveIndicator(((ToggleButton)newValue).getBoundsInParent().getMinX());
        });
        
        progressIndicator = new ProgressIndicator();
        progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        progressIndicator.setMaxSize(48, 48);
    }
    
    private void loadCreateTabViews(){
        detailsTab = new DetailsTab();
        updatesTab = new UpdatesTab();
        photosTab = new PhotosTab();
        mapsTab = new MapsTab();
        
        loadScreen(detailsTab);
        loadScreen(updatesTab);
        loadScreen(photosTab);
        loadScreen(mapsTab);
    }
    
    private void loadScreen(TabScreen screen){
        screen.setParentScreen(this);
        screen.onStart();
        screens.put(screen.mName, screen);
    }
    
    private void setScreen(String key){
        TabScreen screen = screens.get(key);
        if(screen == null){
            return;
        }
        
        if(current != null && current == screen){
            screen.onResume();
            return;
        }
        
        Node content = screen.mContentView;
        
        if(current == null){
            stackPane.getChildren().add(content);
            screen.onResume();
            current = screen;
            return;
        }
        
        // block calls, processes and input events
        hasPendingOperation = true;
        
        Transition transition;
        // set initial opacity and position
        content.setOpacity(1);
        content.setTranslateY(stackPane.getHeight());

        // translate screen to load upwards
        TranslateTransition trans = new TranslateTransition(TRANSITION_DURATION, content);
        trans.setToY(0);
        trans.setInterpolator(OUT_INTERPOLATOR);

        // fade out the current screen
        FadeTransition fadeOut = new FadeTransition(new Duration(200), current.mContentView);
        fadeOut.setToValue(0.0);
        fadeOut.setInterpolator(OUT_INTERPOLATOR);

        transition = new ParallelTransition();
        ((ParallelTransition) transition).getChildren().addAll(trans, fadeOut);

        // add the screen to load above the current screen
        stackPane.getChildren().add(content);
        transition.setOnFinished(evt -> {
            current.onPause();
            stackPane.getChildren().remove(current.mContentView);
            screen.onResume();
            current = screen;
            hasPendingOperation = false;
        });
        transition.play();
    }
    
    private void moveIndicator(double to){
        Timeline anim = new Timeline(new KeyFrame(TRANSITION_DURATION,
                new KeyValue(rectangle.translateXProperty(), to, OUT_INTERPOLATOR)
        ));
        anim.play();
    }
    
    public void setProject(Project project) {
        projectName.setText(project.getNameProperty().get());
        initStatusIcon(project.getStatusValue());
        
        updatesTab.setProject(project);
        photosTab.setProject(project);
        mapsTab.setProject(project);
        
        this.project = project;
    }
    
    public Project getProject(){
        return project;
    }
    
    public ObservableList<ProjectUpdate> getProjectUpdates(){
        return updates;
    }

    @FXML
    public void onBackAction(ActionEvent event) {
        ControlledScreen screen = screenController.getBackStack().pull();
        screenController.setScreen(screen.getName());
    }
    
    private void initStatusIcon(String status){
        if(status.equalsIgnoreCase(Project.ON_GOING)){
            circle.setFill(Color.web("#1de9b6"));
        }else if(status.equalsIgnoreCase(Project.POSTPONED)){
            circle.setFill(Color.web("#cccccc"));
        }else if(status.equalsIgnoreCase(Project.TERMINATED)){
            circle.setFill(Color.web("#55676e"));
        }else{
            circle.setFill(Color.web("#ec407a"));
        }
        statusLabel.setText(status);
    }
    
    @Override
    public void onStart() {
        setScreen(DetailsTab.NAME);
    }

    @Override
    public void onPause() {
        project = null;
    }

    @Override
    public void onResume() {
        if(current == null){
            setScreen(DetailsTab.NAME);
            rectangle.setTranslateX(toggleDetails.getBoundsInParent().getMinX());
        }else{
            current.onResume();
        }
    }

    @Override
    public void onFinish() {
        
    }
    
    public static abstract class TabScreen {
        
        public static final int CREATED = 0;
        public static final int STARTED = 1;
        public static final int PAUSED = 2;
        public static final int RESUMED = 3;
        public static final int FINISHED = 4;
        
        protected ViewProjectScreen parentScreen;
        protected int mState;
        protected String mName;
        protected Node mContentView;
        protected Project project;
        
        public TabScreen(String name){
            mName = name;
            mState = CREATED;
        }
        
        public int getState(){
            return mState;
        }
        
        public void setProject(Project p){
            project = p;
        }
        
        public void setParentScreen(ViewProjectScreen parent){
            parentScreen = parent;
        }
        
        public void onStart(){
            mState = STARTED;
        }
        
        public void onPause(){
            mState = PAUSED;
        }
        
        public void onResume(){
            mState = RESUMED;
        }
        
        public void onFinish(){
            mState = FINISHED;
        }
    }
}
