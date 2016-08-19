package com.gemseeker.pmma.controllers;

import com.gemseeker.pmma.ControlledScreen;
import com.gemseeker.pmma.ScreenController;
import com.gemseeker.pmma.data.Project;
import com.gemseeker.pmma.ui.components.IconButton;
import com.gemseeker.pmma.ui.components.MaterialButton;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 *
 * @author u
 */
public class ViewProjectScreen extends ControlledScreen {

    public static final String NAME = "viewproject";

    @FXML HBox toolbarLeft;
    @FXML Label projectName;
    @FXML Label statusLabel;
    @FXML HBox optionsPane;
    @FXML StackPane stackPane;

    private Project project;
    private Circle circle;
    private final ViewScreenController viewController;

    public ViewProjectScreen() {
        super(NAME);
        initComponents();
        viewController = new ViewScreenController(stackPane);
        // load screens to map here
        
        // set default scree here
        setAsChild(true);
    }

    private void initComponents() {
        setContentView(getClass().getResource("view_project.fxml"));

        // status icon
        circle = new Circle(10);
        toolbarLeft.getChildren().add(0, circle);
        
        // back icon button
        IconButton backBtn = new IconButton();
        backBtn.setIcon(getClass().getResourceAsStream("back_arrow.svg"));
        backBtn.getStyleClass().add("icon-button");
        backBtn.setOnAction(evt -> onBackAction());
        toolbarLeft.getChildren().add(0, backBtn);
        
        MaterialButton detailsBtn = new MaterialButton();
        detailsBtn.setText("Details");
        detailsBtn.getStyleClass().add("paper-pink-flat-button");
        detailsBtn.setPrefSize(140, 40);
        detailsBtn.setOnAction(evt -> onDetailsAction());
        HBox.setHgrow(detailsBtn, Priority.ALWAYS);
        optionsPane.getChildren().add(detailsBtn);
        
        MaterialButton histBtn = new MaterialButton();
        histBtn.setText("Histories");
        histBtn.getStyleClass().add("paper-pink-flat-button");
        histBtn.setPrefSize(140, 40);
        histBtn.setOnAction(evt -> onHistoriesAction());
        HBox.setHgrow(histBtn, Priority.ALWAYS);
        optionsPane.getChildren().add(histBtn);
        
        MaterialButton photosBtn = new MaterialButton();
        photosBtn.setText("Photos");
        photosBtn.setPrefSize(140, 40);
        photosBtn.getStyleClass().add("paper-pink-flat-button");
        photosBtn.setOnAction(evt -> onPhotosAction());
        HBox.setHgrow(photosBtn, Priority.ALWAYS);
        optionsPane.getChildren().add(photosBtn);
        
        MaterialButton geoBtn = new MaterialButton();
        geoBtn.setText("Geotags");
        geoBtn.setPrefSize(140, 40);
        geoBtn.getStyleClass().add("paper-pink-flat-button");
        geoBtn.setOnAction(evt -> onGeotagAction());
        HBox.setHgrow(geoBtn, Priority.ALWAYS);
        optionsPane.getChildren().add(geoBtn);
    }
    
    public void setProject(Project project) {
        projectName.setText(project.getName());
        initStatusIcon(project.getStatus());
        this.project = project;
    }

    private void onBackAction() {
        screenController.setOnSetScreenEvent(null);
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
        statusLabel.setText(String.format("(%s)", status));
    }
    
    private void onDetailsAction(){
        
    }
    
    public void onHistoriesAction(){
        
    }
    
    public void onPhotosAction(){
        
    }
    
    public void onGeotagAction(){
        
    }
    
    /***************************************************************************
     * Custom ScreenController for ViewProjectScreen                           *
     ***************************************************************************/
    private class ViewScreenController extends ScreenController {

        ControlledScreen current;
        boolean animated;
        StackPane stackPane;
        
        public ViewScreenController(StackPane container){
            current = null;
            this.stackPane = container;
        }
        
        @Override
        public void setScreen(String key) {
            // set animated value here, the settings might have changed
            animated = ((MainActivityScreen) screenController).isAnimated();
            ControlledScreen screen = getScreens().get(key);
            if(screen  == null){
                return;
            }
            Node content = screen.getContentView();
            if(getScreenContainer().getChildren().isEmpty()){
                getScreenContainer().getChildren().add(content);
            }else{
                getScreenContainer().getChildren().remove(0);
                getScreenContainer().getChildren().add(0, content);
            }
            current = screen;
        }

        @Override
        public ControlledScreen getCurrentScreen() {
            return current;
        }
        
    }
}
