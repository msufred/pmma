package com.gemseeker.pmma.controllers;

import com.gemseeker.pmma.ControlledScreen;
import com.gemseeker.pmma.animations.EasingMode;
import com.gemseeker.pmma.animations.ExponentialInterpolator;
import com.gemseeker.pmma.data.DBManager;
import com.gemseeker.pmma.data.Location;
import com.gemseeker.pmma.data.Project;
import com.gemseeker.pmma.fxml.ScreenLoader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Arrays;
import java.util.stream.Collectors;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

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
    @FXML ComboBox<String> filter;
    @FXML StackPane projectStackPane;
    @FXML TableView<Project> table;
    @FXML TableColumn<Project, String> nameCol;
    @FXML TableColumn<Project, String> locationCol;
    @FXML TableColumn<Project, String> dateStartedCol;
    @FXML TableColumn<Project, String> dateCompletionCol;
    @FXML TableColumn<Project, String> statusCol;
    
    private DateTimeFormatter DEFAULT_DATE_FORMAT;
    private int filterSelectedIndex = -1;
    
    private ProjectForm projectForm;
    private Pane overlay;
    private ObservableList<Project> projects;
    
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
            return value.getValue().getName();
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
            return value.getValue().getStatus();
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
        
        // Disable action buttons if no item is selected from the table.
        table.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> observable, 
                Number oldValue, Number newValue) -> {
            disableActionButtons(newValue.intValue() < 0);
        });
        
        filter.setItems(FXCollections.observableArrayList("All Project", Project.ON_GOING, Project.POSTPONED,
                Project.TERMINATED, Project.FINISHED));
        filter.getSelectionModel().selectedIndexProperty()
                .addListener((ObservableValue<? extends Number> o,
                        Number old, Number newValue) -> {
            String tag = filter.getItems().get(newValue.intValue());
            if(tag.equals("All Project") && screenController != null){
                table.setItems(((MainActivityScreen) screenController).getProjects());
            }else{
                table.setItems(filterProjects(tag));
            }
            filterSelectedIndex = newValue.intValue();
            table.getSelectionModel().clearSelection();
        });
        
        projectForm = new ProjectForm();
        overlay = new Pane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
        overlay.setOnMouseClicked(evt -> hideProjectForm());
    } //END INITCOMPONENTS

    private void disableActionButtons(boolean disable){
        editBtn.setDisable(disable);
        deleteBtn.setDisable(disable);
        viewBtn.setDisable(disable);
    }
    
    private ObservableList<Project> filterProjects(String tag){
        return FXCollections.observableArrayList(
                ((MainActivityScreen) screenController).getProjects()
                        .stream()
                        .filter(p -> p.getStatusValue().equals(tag))
                        .collect(Collectors.toList()));
    }
        
    @FXML
    public void onAddAction(ActionEvent e){
        projectForm.setEditMode(false);
        showProjectForm();
    }
    
    @FXML
    public void onEditAction(ActionEvent e){
        int index = table.getSelectionModel().getSelectedIndex();
        Project p = projects.get(index);
        if(p != null){
            projectForm.edit(p);
            showProjectForm();
        }
    }
    
    @FXML
    public void onDeleteAction(ActionEvent e){
        Project p = table.getSelectionModel().getSelectedItem();
        if(p != null){
            System.out.println(p.getIdValue());
            DBManager.deleteProject(p.getIdValue());
            table.getItems().remove(p);
            if(projects.isEmpty()){
                editBtn.setDisable(true);
                deleteBtn.setDisable(true);
                viewBtn.setDisable(true);
            }
        }else{
            System.out.println("null");
        }
    }
    
    @FXML
    public void onViewAction(ActionEvent e){
        int index = table.getSelectionModel().getSelectedIndex();
        Project p = projects.get(index);
        if(p != null){
            ViewProjectScreen screen = (ViewProjectScreen) screenController.getScreen(ViewProjectScreen.NAME);
            screen.setProject(p);
            screenController.getBackStack().push(this);
            screenController.setScreen(ViewProjectScreen.NAME);
        }
    }
    
    private void showProjectForm(){
        overlay.setOpacity(0.0);
        projectForm.contentView.setScaleX(0.0);
        projectForm.contentView.setScaleY(0.0);
        projectStackPane.getChildren().add(overlay);
        projectStackPane.getChildren().add(projectForm.contentView);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), overlay);
        fadeIn.setToValue(1.0);
        fadeIn.setInterpolator(Interpolator.EASE_IN);
        fadeIn.setOnFinished(evt -> {
            ScaleTransition scaleIn = new ScaleTransition(Duration.millis(400), projectForm.contentView);
            scaleIn.setInterpolator(new ExponentialInterpolator(EasingMode.EASE_OUT));
            scaleIn.setByX(1.0);
            scaleIn.setByY(1.0);
            scaleIn.play();
        });
        fadeIn.play();
    }
    
    private void hideProjectForm(){
        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(400), projectForm.contentView);
        scaleOut.setToX(0.0);
        scaleOut.setToY(0.0);
        scaleOut.setInterpolator(new ExponentialInterpolator(EasingMode.EASE_IN));
        scaleOut.setOnFinished(evt -> {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(200), overlay);
            fadeOut.setToValue(0.0);
            fadeOut.setInterpolator(Interpolator.EASE_OUT);
            fadeOut.setOnFinished(e -> {
                projectStackPane.getChildren().remove(overlay);
                projectStackPane.getChildren().remove(projectForm.contentView);
            });
            fadeOut.play();
        });
        scaleOut.play();
    }

    
    /***************************************************************************
     *                                                                         *
     * ControlledScreen Overridden Methods                                     *
     *                                                                         *
     ***************************************************************************/
    
    @Override
    public void onStart() {
        projects = ((MainActivityScreen)screenController).getProjects();
        table.setItems(projects);
        filter.getSelectionModel().select(0);
    }

    /**
     * onResume refreshes the table.
     */
    @Override
    public void onResume() {
        if(table != null){
            table.refresh();
        }
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onFinish() {
    }
    
    /**************************************************************************/
    
    private class ProjectForm {
        
        Parent contentView;
        @FXML TextField codeField;
        @FXML TextField nameField;
        @FXML TextField provinceField;
        @FXML TextField cityField;
        @FXML TextField streetField;
        @FXML DatePicker dateStarted;
        @FXML DatePicker dateCompletion;
        @FXML ComboBox<String> status;
        @FXML Button doneBtn;
        
        private boolean isEditMode = false;
        private boolean projectCodeAllowed = false;
        private boolean projectNameAllowed = false;
        private boolean dateStartedAllowed = false;
        private boolean dateCompletionAllowed = false;
        private final DateTimeFormatter dateFormatter;
        private Project tempProject = null;
        
        public ProjectForm(){
            contentView = ScreenLoader.loadScreen(ProjectForm.this, "project_form.fxml");
            
            codeField.textProperty()
                    .addListener((ObservableValue<? extends String> ov,String old, 
                            String newValue) -> {
                validateProjectCode(newValue);
            });

            nameField.textProperty()
                    .addListener((ObservableValue<? extends String> ov,
                            String old, String newValue) -> {
                validateProjectName(newValue);
            });
            
            dateStarted.valueProperty().addListener((ObservableValue<? extends LocalDate> o, LocalDate old, LocalDate newValue) -> {
                dateStartedAllowed = newValue != null;
                checkIfCanCreate();
            });

            dateCompletion.valueProperty().addListener((ObservableValue<? extends LocalDate> o, LocalDate old, LocalDate newValue) -> {
                dateCompletionAllowed = newValue != null;
                checkIfCanCreate();
            });
            
            status.setItems(FXCollections.observableArrayList(Arrays.asList(
                    Project.ON_GOING, Project.POSTPONED, Project.TERMINATED, Project.FINISHED
            )));
            status.getSelectionModel().select(0);
            
            DateTimeFormatterBuilder dfb = new DateTimeFormatterBuilder();
            dfb.appendPattern("M/dd/yyyy");
            dateFormatter = dfb.toFormatter();
        }
        
        private void validateProjectCode(String projectCode){
            final ObservableList<String> styles = codeField.getStyleClass();
            if(projectCode.equals("")){
                projectCodeAllowed = false;
                styles.removeAll("form-error", "form-check");
            }else{
                projectCodeAllowed = isProjectCodeValid(projectCode);
                setFormErrorStyle(styles, projectCodeAllowed);
            }
            checkIfCanCreate();
        }

        private void validateProjectName(String projectName){
            final ObservableList<String> styles = nameField.getStyleClass();
            if(projectName.equals("")){
                projectNameAllowed = false;
                styles.removeAll("form-error", "form-check");
            }else{
                projectNameAllowed = isProjectNameValid(projectName);
                setFormErrorStyle(styles, projectNameAllowed);
            }
            checkIfCanCreate();
        }
        
        private boolean isProjectCodeValid(String codeStr){
            boolean hasMatch = projects.stream().anyMatch((p) -> (p.getIdValue().equals(codeStr)));
            if(isEditMode && hasMatch){
                return true;
            }else{
                return codeStr.length() >= 4 
                        && (projects != null && !isEditMode) 
                        && !hasMatch;
            }
        }

        private boolean isProjectNameValid(String nameStr){
            boolean hasMatch = projects.stream().anyMatch((p) -> (nameStr.equalsIgnoreCase(p.getNameValue())));
            if(isEditMode && hasMatch){
                return true;
            }else{
                return nameStr.length() >= 6 
                        && (projects != null && !isEditMode) 
                        && !hasMatch;
            }
        }

        private void setFormErrorStyle(ObservableList<String> styles, boolean isValid){
            if(isValid){
                styles.remove("form-error");
                if(!styles.contains("form-check")){
                    styles.add("form-check");
                }
            }else{
                styles.remove("form-check");
                if(!styles.contains("form-error")){
                    styles.add("form-error");
                }
            }
        }
        
        private void checkIfCanCreate() {
            if (projectCodeAllowed && projectNameAllowed /* && provinceValueAllowed
                    && cityValueAllowed && streetValueAllowed */
                    && dateStartedAllowed && dateCompletionAllowed) {
                doneBtn.setDisable(false);
            }
        }
        
        public void setEditMode(boolean isEditMode){
            if(!isEditMode){
                clearFields();
            }
            this.isEditMode = isEditMode;
        }
        
        public void edit(Project p){
            isEditMode = true;
            fillFields(p);
        }
        
        private void clearFields(){
            codeField.setEditable(true);
            codeField.clear();
            nameField.clear();
            provinceField.clear();
            cityField.clear();
            streetField.clear();
            dateStarted.getEditor().clear();
            dateCompletion.getEditor().clear();
            status.getSelectionModel().select(0);
        }
        
        private void fillFields(Project p){
            codeField.setText(p.getIdValue());
            codeField.setEditable(false);
            nameField.setText(p.getNameValue());
            provinceField.setText(p.getLocation().getProvince());
            cityField.setText(p.getLocation().getCity());
            streetField.setText(p.getLocation().getStreet());

            dateStarted.setValue(p.getDateCreated());
            if(dateStarted.getEditor().getText().equals("")){
                dateStarted.getEditor().setText(p.getDateCreated().format(dateFormatter));
            }
            dateCompletion.setValue(p.getDateToFinish());
            if(dateCompletion.getEditor().getText().equals("")){
                dateCompletion.getEditor().setText(p.getDateToFinish().format(dateFormatter));
            }

            // Set the status
            status.getSelectionModel().select(p.getStatusValue());
            tempProject = p;
        }
        
        @FXML
        public void onCancelAction(ActionEvent event){
            hideProjectForm();
        }
        
        @FXML
        public void onSaveAction(ActionEvent event){
            // Assumed that all field values are validated
            Project newProject = new Project();
            newProject.setId(codeField.getText());
            newProject.setName(nameField.getText());

            // retrieve location text values
            String street = streetField.getText();
            String city = cityField.getText();
            String province = provinceField.getText();

            // check if location already exist
            boolean locationExists = ((MainActivityScreen)screenController).getLocations()
                    .stream()
                    .anyMatch(loc -> (loc.getProvince().equals(province) 
                            && loc.getCity().equals(city) 
                            && loc.getStreet().equals(street)));

            // If the location does not exist, create a new one and set it as the
            // new Project's location. Add it to the database and Location's observable list.
            if (!locationExists) {
                ObservableList<Location> locations = ((MainActivityScreen)screenController).getLocations();
                Location newLoc = new Location();
                newLoc.setId("loc_id_" + locations.size());
                newLoc.setStreet(street == null ? "" : street);
                newLoc.setCity(city == null ? "" : city);
                newLoc.setProvince(province == null ? "" : province);
                newProject.setLocation(newLoc);
                newProject.setLocationId(newLoc.getId());

                // add to database and locations list
                DBManager.addLocation(newLoc);
                locations.add(newLoc);
            }else{
                Location location = ((MainActivityScreen)screenController).getLocations()
                        .stream()
                        .filter(loc -> (loc.getProvince().equals(province) 
                                && loc.getCity().equals(city) 
                                && loc.getStreet().equals(street)))
                        .findFirst()
                        .get();
                newProject.setLocationId(location.getId());
                newProject.setLocation(location);
            }

            newProject.setDateCreated(dateStarted.getValue());
            newProject.setDateToFinish(dateCompletion.getValue());
            newProject.setStatus(status.getSelectionModel().getSelectedItem());

            ObservableList<Project> projects = ((MainActivityScreen)screenController).getProjects();
            if(isEditMode){
                boolean updated = DBManager.updateProject(newProject);
                if(updated){
                    // NOTE: When on edit mode, tempProject is ALWAYS assumed to be
                    // not equal to null, so no need to check.
                    for(int i=0; i<projects.size(); i++){
                        Project p = projects.get(i);
                        if(p.getIdValue().equalsIgnoreCase(tempProject.getIdValue())){
                            projects.set(i, newProject);
                            tempProject = null;
                            table.refresh();
                            break;
                        }
                    }
                }else{
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to add project");
                    alert.showAndWait();
                }
            }else{
                // Add new project to the database and to the list as well.
                boolean added = DBManager.addProject(newProject);
                if (added) {
                    projects.add(newProject);
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to add project");
                    alert.showAndWait();
                }
            }
            hideProjectForm();
        }
    }
}
