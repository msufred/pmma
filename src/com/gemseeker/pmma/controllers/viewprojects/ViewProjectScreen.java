package com.gemseeker.pmma.controllers.viewprojects;

import com.gemseeker.pmma.ControlledScreen;
import com.gemseeker.pmma.components.SlidingTabsLayout;
import com.gemseeker.pmma.components.ViewPager;
import com.gemseeker.pmma.data.Contact;
import com.gemseeker.pmma.data.Project;
import com.gemseeker.pmma.data.ProjectUpdate;
import com.gemseeker.pmma.fxml.ScreenLoader;
import java.util.HashMap;
import javafx.animation.Interpolator;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

/**
 *
 * @author u
 */
public class ViewProjectScreen extends ControlledScreen {

    public static final String NAME = "viewproject";
    private final HashMap<String, TabScreen> screens = new HashMap<>();
    static final Duration TRANSITION_DURATION = new Duration(400);
    static final double SCREEN_OFFSET = 8.0;

    @FXML Circle circle;
    @FXML Label projectName;
    @FXML Label statusLabel;
    @FXML StackPane container;
    @FXML VBox vbox;
    
    private SlidingTabsLayout tabsLayout;
    private ViewPager viewPager;
    private ViewProjectPageAdapter adapter;
    
    private ToggleGroup toggleGroup;
    
    private TabScreen current = null;
    
    private Project project;
    private Contact contact;
    private ObservableList<ProjectUpdate> updates;
    
    private ProgressIndicator progressIndicator;
    private boolean hasPendingOperation = false;

    public ViewProjectScreen() {
        super(NAME);
        initComponents();
        setAsChild(true);
    }

    private void initComponents() {
        setContentView(ScreenLoader.loadScreen(this, "view_project.fxml"));
        viewPager = new ViewPager();
        adapter = new ViewProjectPageAdapter(this);
        viewPager.setAdapter(adapter);
        
        tabsLayout = new SlidingTabsLayout();
        tabsLayout.setViewPager(viewPager);
        VBox.setVgrow(tabsLayout, Priority.NEVER);
        vbox.getChildren().add(tabsLayout);
        
        progressIndicator = new ProgressIndicator();
        progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        progressIndicator.setMaxSize(48, 48);
    }
    
    private void loadScreen(TabScreen screen){
        screen.setParentScreen(this);
        screen.onStart();
        screens.put(screen.mName, screen);
    }
    
    public void setProject(Project project) {
        projectName.setText(project.getNameProperty().get());
        initStatusIcon(project.getStatusValue());
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
//        setScreen(DetailsTab.NAME);
        viewPager.setCurrentItem(0);
    }

    @Override
    public void onPause() {
        project = null;
    }

    @Override
    public void onResume() {
//        if(current == null){
//            setScreen(DetailsTab.NAME);
//            rectangle.setTranslateX(toggleDetails.getBoundsInParent().getMinX());
//        }else{
//            current.onResume();
//        }
        viewPager.getAdapter().getControllerAt(viewPager.getSelectedIndex()).onResume();
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
