package com.gemseeker.pmma.controllers;

import com.gemseeker.pmma.ControlledScreen;
import com.gemseeker.pmma.data.Project;
import com.gemseeker.pmma.fxml.ScreenLoader;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

/**
 *
 * @author RAFIS-FRED
 */
public class DashboardScreen extends ControlledScreen {

    public static final String NAME = "dashboard";
    
    @FXML VBox onGoingBox;
    @FXML VBox postponedBox;
    @FXML VBox terminatedBox;
    @FXML VBox finishedBox;
    @FXML Label onGoingCountLabel;
    @FXML Label postponedCountLabel;
    @FXML Label terminatedCountLabel;
    @FXML Label finishedCountLabel;
    @FXML ComboBox historyComboBox;
    @FXML TableView historyTable;
    
    private ObservableList<Project> projects;
    private final SimpleStringProperty onGoingText, postponedText, terminatedText,
            finishedText;

    public DashboardScreen(){
        super(NAME);
        setContentView(ScreenLoader.loadScreen(DashboardScreen.this, "dashboard.fxml"));
        
        onGoingText = new SimpleStringProperty();
        postponedText = new SimpleStringProperty();
        terminatedText = new SimpleStringProperty();
        finishedText = new SimpleStringProperty();
        
        onGoingCountLabel.textProperty().bind(onGoingText);
        postponedCountLabel.textProperty().bind(postponedText);
        terminatedCountLabel.textProperty().bind(terminatedText);
        finishedCountLabel.textProperty().bind(finishedText);
        
        onGoingBox.setOnMouseClicked(evt -> displayProjects(Project.ON_GOING));
        postponedBox.setOnMouseClicked(evt -> displayProjects(Project.POSTPONED));
        terminatedBox.setOnMouseClicked(evt -> displayProjects(Project.TERMINATED));
        finishedBox.setOnMouseClicked(evt -> displayProjects(Project.FINISHED));
        
        historyComboBox.setItems(FXCollections.observableArrayList("Recent Updates", "History"));
        historyComboBox.getSelectionModel()
                .selectedIndexProperty()
                .addListener((ObservableValue<? extends Number> observable,
                        Number oldValue, Number newValue) -> {
            if(newValue.intValue() == 0){
                System.out.println("Show Recent Updates");
            }else if(newValue.intValue() == 1){
                System.out.println("Show All History");
            }
        });
        historyComboBox.getSelectionModel().select(0);
    }
    
    private ObservableList<Project> getProjects(String tag){
        return FXCollections.observableArrayList(projects
                    .stream()
                    .filter(proj -> proj.getStatusValue().equals(tag))
                    .collect(Collectors.toList()));
    }
    
    private void displayProjects(String tag){
        ProjectsScreen projectScreen = (ProjectsScreen) screenController.getScreen(ProjectsScreen.NAME);
        projectScreen.filter.getSelectionModel().select(tag);
        ((MainActivityScreen)screenController).toggleProjects.setSelected(true);
    }
    
    /***************************************************************************
    *                                                                         *
    * ControlledScreen Overridden Methods                                     *
    *                                                                         *
    ***************************************************************************/
    
    // onStart calculates or counts the projects by status
    @Override
    public void onStart() {
        projects = ((MainActivityScreen) screenController).getProjects();
    }

    @Override
    public void onPause() {
    }
    
    // onResume recalculates the counts of projects by status
    @Override
    public void onResume() {
        Platform.runLater(()->{
            onGoingText.set(getProjects(Project.ON_GOING).size() + "");
            postponedText.set(getProjects(Project.POSTPONED).size() + "");
            terminatedText.set(getProjects(Project.TERMINATED).size() + "");
            finishedText.set(getProjects(Project.FINISHED).size() + "");
        });
    }

    @Override
    public void onFinish() {
    }
}
