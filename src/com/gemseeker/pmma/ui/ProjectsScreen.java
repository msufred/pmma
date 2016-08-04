package com.gemseeker.pmma.ui;

import com.gemseeker.pmma.ui.components.IconButton;
import com.gemseeker.pmma.ui.components.MaterialButton;
import com.gemseeker.pmma.ControlledScreen;
import com.gemseeker.pmma.data.Project;
import com.gemseeker.pmma.utils.Utils;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 *
 * @author RAFIS-FRED
 */
public class ProjectsScreen extends ControlledScreen {

    private AnchorPane contentView;
    private VBox vbox;
    private HBox hbox;
    private HBox leftBox, rightBox;
    private TableView table;
    private IconButton addBtn, editBtn, deleteBtn;
    private MaterialButton viewProjectBtn;
    
    public ProjectsScreen(){
        super("projects");
        contentView = new AnchorPane();
        contentView.getStyleClass().add("projects-bg");

        vbox = new VBox();
        
        hbox = new HBox();
        hbox.setPrefHeight(40);
        hbox.getStyleClass().add("menu-panel");
        
        leftBox = new HBox();
        leftBox.setAlignment(Pos.CENTER_LEFT);
        leftBox.setPadding(new Insets(0, 0, 0, 16));
        leftBox.setSpacing(8);
        HBox.setHgrow(leftBox, Priority.ALWAYS);
        
        rightBox = new HBox();
        rightBox.setAlignment(Pos.CENTER_RIGHT);
        rightBox.setPadding(new Insets(0, 16, 0, 0));
        rightBox.setSpacing(8);
        HBox.setHgrow(rightBox, Priority.ALWAYS);
        
        hbox.getChildren().addAll(leftBox, rightBox);
        
        viewProjectBtn = new MaterialButton();
        viewProjectBtn.setText("View Project");
        viewProjectBtn.setDisable(true);
        viewProjectBtn.setFlated(true);
        viewProjectBtn.setOnAction(evt->onViewProjectAction());
        leftBox.getChildren().add(viewProjectBtn);
        
        addBtn = new IconButton();
        addBtn.setIcon(getClass().getResourceAsStream("add.svg"));
        addBtn.getStyleClass().add("icon-button");
        addBtn.setOnAction(evt->onAddAction());
        
        editBtn = new IconButton();
        editBtn.setIcon(getClass().getResourceAsStream("edit.svg"));
        editBtn.getStyleClass().add("icon-button");
        editBtn.setDisable(true);
        editBtn.setOnAction(evt->onEditAction());
        
        deleteBtn = new IconButton();
        deleteBtn.setIcon(getClass().getResourceAsStream("delete.svg"));
        deleteBtn.getStyleClass().add("icon-button");
        deleteBtn.setDisable(true);
        deleteBtn.setOnAction(evt->onDeleteAction());
        
        rightBox.getChildren().addAll(addBtn, editBtn, deleteBtn);
        
        table = new TableView();
        table.getStyleClass().addAll("table", "top-z1");
        AnchorPane.setLeftAnchor(table, 8.0);
        AnchorPane.setTopAnchor(table, 8.0);
        AnchorPane.setBottomAnchor(table, 8.0);
        AnchorPane.setRightAnchor(table, 8.0);
        AnchorPane pane = new AnchorPane();
        pane.getChildren().add(table);
        
        VBox.setVgrow(pane, Priority.ALWAYS);
        vbox.getChildren().addAll(hbox, pane);
        
        AnchorPane.setLeftAnchor(vbox, 0.0);
        AnchorPane.setTopAnchor(vbox, 0.0);
        AnchorPane.setBottomAnchor(vbox, 0.0);
        AnchorPane.setRightAnchor(vbox, 0.0);
        contentView.getChildren().add(vbox);
        
        // project name col
        TableColumn<Project, String> projNameCol = new TableColumn<>("Project Name");
        projNameCol.setCellValueFactory((TableColumn.CellDataFeatures<Project, String> param) -> {
            return new SimpleStringProperty(param.getValue().getName());
        });
        projNameCol.setSortable(false);
        projNameCol.getStyleClass().add("project-name-col");
        
        // location col
        TableColumn<Project, String> projLocationCol = new TableColumn<>("Location");
        projLocationCol.setCellValueFactory((TableColumn.CellDataFeatures<Project, String> param) -> {
            return new SimpleStringProperty(param.getValue().getLocation().toString());
        });
        projLocationCol.setSortable(false);
        projLocationCol.getStyleClass().add("project-location-col");
        
        // date started col
        TableColumn<Project, String> projStartedCol = new TableColumn<>("Date Started");
        projStartedCol.setCellValueFactory((TableColumn.CellDataFeatures<Project, String> param) -> {
            return new SimpleStringProperty(Utils.LOCAL_DATE_FORMAT.format(param.getValue().getDateCreated()));
        });
        projStartedCol.setSortable(false);
        projStartedCol.getStyleClass().add("project-started-col");
        
        // deadline col
        TableColumn<Project, String> projToEndCol = new TableColumn<>("Date to Finish");
        projToEndCol.setCellValueFactory((TableColumn.CellDataFeatures<Project, String> param) -> {
            return new SimpleStringProperty(Utils.LOCAL_DATE_FORMAT.format(param.getValue().getDateToFinish()));
        });
        projToEndCol.setSortable(false);
        projToEndCol.getStyleClass().add("project-to-finish-col");
        
        // status col
        TableColumn<Project, String> projStatusCol = new TableColumn<>("Status");
        projStatusCol.setCellValueFactory((TableColumn.CellDataFeatures<Project, String> param) -> {
            return new SimpleStringProperty(param.getValue().getStatus());
        });
        projStatusCol.setSortable(false);
        projStatusCol.getStyleClass().add("project-status-col");
        
        // custom cell factory that enables the Status column to have a particular
        // background color according to the project's status value
        projStatusCol.setCellFactory((col) -> {
            return new TableCell<Project, String>(){
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? "" : item); // put the text
                    setGraphic(null);
                    setAlignment(Pos.CENTER); // center text
                    if(!empty){
                        if(item.equalsIgnoreCase(Project.ON_GOING)){
                            setStyle("-fx-background-color: -on-going-color;");
                        }else if(item.equalsIgnoreCase(Project.POSTPONED)){
                            setStyle("-fx-background-color: -postponed-color;");
                        }else if(item.equalsIgnoreCase(Project.TERMINATED)){
                            setStyle("-fx-background-color: -terminated-color;");
                        }else{
                            setStyle("-fx-background-color: -finished-color;");
                        }
                    }
                }
            };
        });
        
        // adding columns to table
        table.getColumns().addAll(projNameCol, 
                projLocationCol, projStartedCol,
                projToEndCol, projStatusCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // fit columns
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        table.getSelectionModel()
                .selectedIndexProperty()
                .addListener((ObservableValue<? extends Number> observable, 
                        Number oldValue, Number newValue) -> {
                    if(newValue.intValue() > -1){
                        // enable edit and delete buttons
                        editBtn.setDisable(false);
                        deleteBtn.setDisable(false);
                        // enable view project button
                        viewProjectBtn.setDisable(false);
                    }
        });
    }

    @Override
    public Parent getContentView() {
        return contentView;
    }

    @Override
    public EventHandler<ActionEvent> getOnLoadEvent() {
        return evt->table.setItems(((MainActivityScreen)screenController).getProjects());
    }
    
    private void onViewProjectAction(){
        int index = table.getSelectionModel().getSelectedIndex();
        Project p = (Project) table.getItems().get(index);
        if(p != null){
            System.out.println("View Project action called for " + p.getName());
        }
    }
    
    private void onAddAction(){
        System.out.println("Add Action called");
    }
    
    private void onEditAction(){
        int index = table.getSelectionModel().getSelectedIndex();
        Project p = (Project) table.getItems().get(index);
        if(p != null){
            System.out.println("Edit action called for " + p.getName());
        }
    }
    
    private void onDeleteAction(){
        int index = table.getSelectionModel().getSelectedIndex();
        Project p = (Project) table.getItems().get(index);
        if(p != null){
            System.out.println("Delete action called for " + p.getName());
        }
    }
}
