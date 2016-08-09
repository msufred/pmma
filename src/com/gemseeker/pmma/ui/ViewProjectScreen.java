package com.gemseeker.pmma.ui;

import com.gemseeker.pmma.ControlledScreen;
import com.gemseeker.pmma.data.DBManager;
import com.gemseeker.pmma.data.Project;
import com.gemseeker.pmma.ui.components.IconButton;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
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
    @FXML StackPane stackPane;
    @FXML ToggleButton overview;
    @FXML ToggleButton updates;
    @FXML ToggleButton photos;
    @FXML ToggleButton geotags;

    private Project project;
    private Circle circle;
    private final UpdatesTable updatesTable;

    public ViewProjectScreen() {
        super(NAME);
        initComponents();
        setAsChild(true);
        updatesTable = new UpdatesTable();
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
       
        ToggleGroup toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().addAll(overview, updates, photos, geotags);
        toggleGroup.selectedToggleProperty()
                .addListener((ObservableValue<? extends Toggle> observable,
                        Toggle oldValue, Toggle newValue) -> {
                    if(newValue == overview){
                        if(updatesTable != null){
                            setContent(updatesTable);
                        }
                    }else if(newValue == updates){
                        setContent(new Label("Updates Here"));
                    }else if(newValue == photos){
                        setContent(new Label("Photos Here"));
                    }else{
                        setContent(new Label("Geotags Here"));
                    }
                });
        toggleGroup.getToggles().stream().forEach(toggle -> {
            ((ToggleButton)toggle).addEventFilter(MouseEvent.MOUSE_PRESSED, evt -> {
                if(toggle.isSelected()){
                    evt.consume();
                }
            });
        });
    }

    public void setProject(Project project) {
        projectName.setText(project.getName());
        initStatusIcon(project.getStatus());
        
        if(updatesTable != null){
            updatesTable.setItems(project.getHistories());
        }
        this.project = project;
    }

    private void onBackAction() {
        screenController.setOnSetScreenEvent(null);
        ControlledScreen screen = screenController.getBackStack().pull();
        screenController.setScreen(screen.getName());
    }

    private void setContent(Node content) {
        if (stackPane.getChildren().isEmpty()) {
            stackPane.getChildren().add(content);
        } else {
            stackPane.getChildren().remove(0);
            stackPane.getChildren().add(content);
        }
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
}
