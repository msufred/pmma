package com.gemseeker.pmma.controllers;

import com.gemseeker.pmma.ControlledScreen;
import com.gemseeker.pmma.data.Contact;
import com.gemseeker.pmma.data.DBManager;
import com.gemseeker.pmma.data.Project;
import com.gemseeker.pmma.fxml.ScreenLoader;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

/**
 *
 * @author RAFIS-FRED
 */
public class ProjectsScreen extends ControlledScreen {

    public static final String NAME = "projects";
    
    @FXML Button viewBtn;
    @FXML Button addBtn;
    @FXML Button editBtn;
    @FXML Button deleteBtn;
    @FXML ToggleButton toggleCard;
    @FXML ToggleButton toggleTable;
    @FXML ComboBox filter;
    @FXML StackPane projectStackPane;
    @FXML TableView<Project> table;
    @FXML TableColumn<Project, String> nameCol;
    @FXML TableColumn<Project, String> locationCol;
    @FXML TableColumn<Project, String> dateStartedCol;
    @FXML TableColumn<Project, String> dateCompletionCol;
    @FXML TableColumn<Project, String> statusCol;
    
    private DateTimeFormatter DEFAULT_DATE_FORMAT;
    private int filterSelectedIndex = -1;
    // For Added or Updated Project and Contact
    private Project projectHolder; // for adding or updating project
    private Contact contactHolder; // for adding or udpating contact
    
    public ProjectsScreen(){
        super(NAME);
        initComponents();
    }

    private void initComponents() {
        // Create the DateTimeFormatter
        DateTimeFormatterBuilder formatterBuilder = new DateTimeFormatterBuilder();
        formatterBuilder.appendPattern("MMM dd, yyyy");
        DEFAULT_DATE_FORMAT = formatterBuilder.toFormatter();
        
//        setContentView(contentView);
        setContentView(ScreenLoader.loadScreen(ProjectsScreen.this, "projects.fxml"));
        
        addBtn.setOnAction(evt -> onAddAction());
        editBtn.setOnAction(evt -> onEditAction());
        deleteBtn.setOnAction(evt -> onDeleteAction());
        viewBtn.setOnAction(evt -> onViewProjectAction());
        
        // View Toggles
        ToggleGroup toggleViewGroup = new ToggleGroup();
        toggleCard.setToggleGroup(toggleViewGroup);
        toggleTable.setToggleGroup(toggleViewGroup);
        // consume click of selected toggle
        toggleViewGroup.getToggles().stream().forEach(tb -> {
            ((ToggleButton)tb).addEventFilter(MouseEvent.MOUSE_PRESSED, evt -> {
                if(tb.isSelected()){
                    evt.consume();
                }
            });
        });
        
        // init TableColumns
        nameCol.setCellValueFactory((TableColumn.CellDataFeatures<Project, String> value) -> {
            return new SimpleStringProperty(value.getValue().getName());
        });
        locationCol.setCellValueFactory((TableColumn.CellDataFeatures<Project, String> value) -> {
            return new SimpleStringProperty(value.getValue().getLocation().toString());
        });
        dateStartedCol.setCellValueFactory((TableColumn.CellDataFeatures<Project, String> value) -> {
            return new SimpleStringProperty(value.getValue().getDateCreated().format(DEFAULT_DATE_FORMAT));
        });
        dateCompletionCol.setCellValueFactory((TableColumn.CellDataFeatures<Project, String> value) -> {
            return new SimpleStringProperty(value.getValue().getDateToFinish().format(DEFAULT_DATE_FORMAT));
        });
        statusCol.setCellValueFactory((TableColumn.CellDataFeatures<Project, String> value) -> {
            return new SimpleStringProperty(value.getValue().getStatus());
        });
        
        // set cell color according to project's status
        statusCol.setCellFactory((col) -> {
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
                    }else{
                        setStyle(null);
                    }
                }
            };
        });
        
        table.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> observable, 
                Number oldValue, Number newValue) -> {
            if(newValue.intValue() > -1){
                viewBtn.setDisable(false);
                editBtn.setDisable(false);
                deleteBtn.setDisable(false);
            }
        });
        
        filter.setItems(FXCollections.observableArrayList("All Project", Project.ON_GOING, Project.POSTPONED,
                Project.TERMINATED, Project.FINISHED));
        filter.getSelectionModel().selectedIndexProperty()
                .addListener((ObservableValue<? extends Number> o,
                        Number old, Number newValue) -> {
            String tag = filter.getItems().get(newValue.intValue()).toString();
            if(tag.equals("All Project") && screenController != null){
                table.setItems(((MainActivityScreen) screenController).getProjects());
            }else{
                table.setItems(filterProjects(tag));
            }
            filterSelectedIndex = newValue.intValue();
        });
    } //END INITCOMPONENTS

    private ObservableList<Project> filterProjects(String tag){
        return FXCollections.observableArrayList(
                ((MainActivityScreen) screenController).getProjects()
                        .stream()
                        .filter(p -> p.getStatus().equals(tag))
                        .collect(Collectors.toList()));
    }
    
    @Override
    public void onStart() {
        table.setItems(((MainActivityScreen)screenController).getProjects());
        filter.getSelectionModel().select(0);
    }

    /**
     * onResume refreshes the table.
     */
    @Override
    public void onResume() {
        super.onResume();
        // refresh table
        int temp = filterSelectedIndex;
        filter.getSelectionModel().select(0);
        filter.getSelectionModel().select(temp);
        table.getSelectionModel().select(-1);
    }
    
    private void onViewProjectAction(){
        
    }
    
    private void onAddAction(){
        screenController.getBackStack().push(this);
        screenController.setScreen(AddProjectScreen.NAME);
    }
    
    private void onEditAction(){
        int index = table.getSelectionModel().getSelectedIndex();
        Project p = (Project) table.getItems().get(index);
        if(p != null){
            AddProjectScreen screen = (AddProjectScreen) screenController.getScreen(AddProjectScreen.NAME);
            screen.editProject(p);
            onAddAction();
        }
    }
    
    private void onDeleteAction(){
        int index = table.getSelectionModel().getSelectedIndex();
        Project p = (Project) table.getItems().get(index);
        if(p != null){
            DBManager.deleteProject(p.getId());
            ((MainActivityScreen)screenController).getProjects().remove(p);
            if(((MainActivityScreen)screenController).getProjects().isEmpty()){
                editBtn.setDisable(true);
                deleteBtn.setDisable(true);
                viewBtn.setDisable(true);
            }
        }
    }
    
    public void showProjects(String tag){
        System.out.println(tag);
    }
    
}
