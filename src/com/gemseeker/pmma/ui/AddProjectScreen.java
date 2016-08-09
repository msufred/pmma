package com.gemseeker.pmma.ui;

import com.gemseeker.pmma.ControlledScreen;
import com.gemseeker.pmma.data.Contractor;
import com.gemseeker.pmma.data.DBManager;
import com.gemseeker.pmma.data.Location;
import com.gemseeker.pmma.data.Project;
import com.gemseeker.pmma.ui.components.FormLabel;
import com.gemseeker.pmma.ui.components.IconButton;
import com.gemseeker.pmma.ui.components.MaterialButton;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.stream.Collectors;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 *
 * @author RAFIS-FRED
 */
public class AddProjectScreen extends ControlledScreen {

    public static final String NAME = "addproject";
    
    // FXML Component References
    @FXML HBox toolbar;
    @FXML HBox codeBox;
    @FXML HBox nameBox;
    @FXML TextField projectCodeField;
    @FXML TextField projectNameField;
    @FXML ComboBox<String> provinceComboBox;
    @FXML ComboBox<String> cityComboBox;
    @FXML ComboBox<String> streetComboBox;
    @FXML DatePicker startedDatePicker;
    @FXML DatePicker completionDatePicker;
    @FXML ComboBox<String> statusComboBox;
    @FXML HBox actionBox;
    private MaterialButton createBtn, cancelBtn;
    private FormLabel codeFormLabel, nameFormLabel;

    // data
    private ObservableList<Project> projects;
    private ObservableList<Location> locations;
    private ObservableList<Contractor> contractors;
    
    private final ObservableList<String> provinces = FXCollections.observableArrayList();
    private final ObservableList<String> cities = FXCollections.observableArrayList();
    private final ObservableList<String> streets = FXCollections.observableArrayList();

    // These values are used to validate the form values. For now, the required
    // fields are the project code and name.
    private boolean projectCodeAllowed = false;
    private boolean projectNameAllowed = false;

    // Edit Mode Members
    //----------------------------------------------
    private boolean isEditMode = false;
    private Project tempProject = null;
    
    private boolean projectNameHasChanged = false;
    private boolean provinceValueHasChanged = false;
    private boolean cityValueHasChanged = false;
    private boolean streetValueHasChanged = false;
    private boolean contractorIsUpdated = false;
    // [End] Edit Mode Members
    //----------------------------------------------
    
    public AddProjectScreen() {
        super(NAME);
        initComponents();
        initComponentData();
        setAsChild(true); // this screen is a child of projects screen
    }

    private void initComponents() {
        setContentView(getClass().getResource("add_project.fxml"));

        // back arrow icon button
        IconButton backBtn = new IconButton();
        backBtn.setIcon(getClass().getResourceAsStream("back_arrow.svg"));
        backBtn.setOnAction(evt -> onCancelAction());
        toolbar.getChildren().add(backBtn);

        // add FormLabels
        codeFormLabel = new FormLabel();
        codeBox.getChildren().add(codeFormLabel);
        nameFormLabel = new FormLabel();
        nameBox.getChildren().add(nameFormLabel);

        createBtn = new MaterialButton();
        createBtn.setText("Create");
        createBtn.setDefaultButton(true); // trigger when Enter is pressed
        createBtn.getStyleClass().add("paper-pink-button");
        createBtn.setDisable(true);
        createBtn.setOnAction(evt -> onSaveAction());
        HBox.setHgrow(createBtn, Priority.ALWAYS);

        cancelBtn = new MaterialButton();
        cancelBtn.setText("Cancel");
        cancelBtn.getStyleClass().add("paper-pink-button");
        cancelBtn.setOnAction(evt -> onCancelAction());
        HBox.setHgrow(cancelBtn, Priority.ALWAYS);

        actionBox.getChildren().addAll(createBtn, cancelBtn);
    }
    
    /**
     * Initialize component data (for example, the provinceComboBox, the
     * statusComboBox etc.). This method also sets up the behavior of the UI
     * components according to the events incorporated to it (like a click, a
     * selection for the combo box etc).
     */
    private void initComponentData(){
        projectCodeField.textProperty().addListener((ObservableValue<? extends String> observable,
                String oldValue, String newValue) -> {
                    
            // Project Code must not be empty and atleas 4 characters long
            if (newValue.equals("")){
                projectCodeAllowed = false;
                codeFormLabel.setGraphic(FormLabel.NONE);
            } else if(newValue.length() < 4) {
                projectCodeAllowed = false;
                codeFormLabel.setGraphic(FormLabel.TOO_SHORT);
            } else if (projects != null && !isEditMode) {
                // No duplicates EXCEPT in edit mode
                // NOTE: In edit mode, project code doesn't change. Thus, this
                // check is ignored.
                boolean exist = false;
                for (Project p : projects) {
                    if (newValue.equalsIgnoreCase(p.getId())) {
                        exist = true;
                        break;
                    }
                }
                if (exist) {
                    projectCodeAllowed = false;
                    codeFormLabel.setGraphic(FormLabel.ALREADY_EXISTS);
                } else {
                    projectCodeAllowed = true;
                    codeFormLabel.setGraphic(FormLabel.ALLOWED);
                }
            } else {
                projectCodeAllowed = true;
                codeFormLabel.setGraphic(FormLabel.ALLOWED);
            }
            checkIfCanCreate();
        });
        
        projectNameField.textProperty().addListener((ObservableValue<? extends String> observable,
                String oldValue, String newValue) -> {

            // Project name must not be empty and not less than 6 characters.
            // A project name that already exist is not allowed EXCEPT when in
            // edit mode (defined by isEditMode variable.
                    
            if (newValue.equals("")) {
                projectNameAllowed = false;
                nameFormLabel.setGraphic(FormLabel.NONE);
            } else if(newValue.length() < 6) {
                // Second Check: Project name is at least 6 characters long.
                projectNameAllowed = false;
                nameFormLabel.setGraphic(FormLabel.TOO_SHORT);
            } else if (projects != null && !projects.isEmpty() && !isEditMode) {
                // No duplicateds EXCEPT in edit mode
                boolean exist = false;
                for (Project p : projects) {
                    if (newValue.equalsIgnoreCase(p.getName())) {
                        exist = true;
                        break;
                    }
                }
                if (exist) {
                    projectNameAllowed = false;
                    nameFormLabel.setGraphic(FormLabel.ALREADY_EXISTS);
                } else {
                    projectNameAllowed = true;
                    nameFormLabel.setGraphic(FormLabel.ALLOWED);
                }
            } else {
                // Here, project name is not empty or less that 6 characters long
                // and either has no duplicate or (even if has a duplicate) is in
                // edit mode.
                projectNameAllowed = true;
                nameFormLabel.setGraphic(FormLabel.ALLOWED);
            }
            checkIfCanCreate();
        });

        provinceComboBox.setItems(provinces);
        // This code sets the items of the cities ObservableList according to the
        // selected value of the provinceComboBox. Since cities is an instance of
        // ObservableList, the items of cityComboBox is automatically updated whenever
        // an item is added, updated or removed on the list.
        provinceComboBox.getSelectionModel().selectedIndexProperty()
                .addListener((ObservableValue<? extends Number> o, 
                        Number old, Number index) -> {
                    if (index.intValue() > -1) {
                        String selectedProvinceStr = provinceComboBox.getItems()
                                .get(index.intValue());
                        if (!selectedProvinceStr.equals("")) {
                            cities.setAll(locations
                                    .stream()
                                    .distinct()
                                    .filter(loc -> loc.getProvince()
                                            .equals(selectedProvinceStr))
                                    .map(Location::getCity)
                                    .collect(Collectors.toList()));
                        }
                    }
                });
        
        cityComboBox.setItems(cities);
        // load all DISTINCT street or barangay of the city selected
        cityComboBox.getSelectionModel().selectedIndexProperty()
                .addListener((ObservableValue<? extends Number> o,
                        Number old, Number index) -> {
                    if (index.intValue() > -1) {
                        String selectedCityStr = cityComboBox.getItems()
                                .get(index.intValue());
                        if (!selectedCityStr.equals("")) {
                            streets.setAll(locations
                                    .stream()
                                    .distinct()
                                    .filter(loc -> loc.getCity()
                                            .equals(selectedCityStr))
                                    .map(Location::getStreet)
                                    .collect(Collectors.toList()));
                        }
                    }
                });
        
        streetComboBox.setItems(streets);
        statusComboBox.setItems(FXCollections.observableArrayList(
                Project.ON_GOING, Project.POSTPONED, Project.TERMINATED, Project.FINISHED
        ));
    }

    public void editProject(Project project) {
        // Assumes that project is not null. The calling screen ensures the
        // the passed Project object is not null.
        isEditMode = true;
        
        // DO NOT let the user change the project code!
        projectCodeField.setText(project.getId());
        projectCodeField.setEditable(false);
        projectCodeAllowed = true; // just to be sure
        codeFormLabel.setGraphic(FormLabel.ALLOWED);
        
        projectNameField.setText(project.getName());

        // Set the location combo boxes. ComboBoxes are assumed to contain the
        // values of the location of the project. It is loaded in the start of
        // the application or when the project was added within the application.
        
        provinceComboBox.getSelectionModel().select(project.getLocation().getProvince());
        cityComboBox.getSelectionModel().select(project.getLocation().getCity());
        streetComboBox.getSelectionModel().select(project.getLocation().getStreet());

        // Set the Date values
        DateTimeFormatterBuilder dfb = new DateTimeFormatterBuilder();
        dfb.appendPattern("M/dd/yyyy");
        DateTimeFormatter df = dfb.toFormatter();
        
        startedDatePicker.setValue(project.getDateCreated());
        if(startedDatePicker.getEditor().getText().equals("")){
            startedDatePicker.getEditor().setText(project.getDateCreated().format(df));
        }
        completionDatePicker.setValue(project.getDateToFinish());
        if(completionDatePicker.getEditor().getText().equals("")){
            completionDatePicker.getEditor().setText(project.getDateToFinish().format(df));
        }

        // Set the status
        statusComboBox.getSelectionModel().select(project.getStatus());
        
        // change create button text to update
        createBtn.setText("Update");
        
        tempProject = project;
    }

    /**
     * onStart retrieves the projects and locations data and sets provinces list
     * items. No need to worry about the cities and streets list, they will be
     * set accordingly when an item from provinces is selected.
     */
    @Override
    public void onStart() {
        super.onStart();
        projects = ((MainActivityScreen) screenController).getProjects();
        locations = ((MainActivityScreen) screenController).getLocations();
        contractors = ((MainActivityScreen) screenController).getContractors();
        
        // fill provinces list of all t
        provinces.setAll(locations
                .stream()
                .distinct()
                .map(Location::getProvince)
                .collect(Collectors.toList()));
    }

    /**
     * onPause clears the text fields and ComboBoxes.
     */
    @Override
    public void onPause() {
        super.onPause();
        clearFields();
    }

    private void clearFields() {
        projectCodeField.clear();
        projectNameField.clear();
        // clear all form labels
        statusComboBox.getSelectionModel().select(0);
        provinceComboBox.getEditor().clear();
        cityComboBox.getEditor().clear();
        streetComboBox.getEditor().clear();
        // clear the lists as well
        provinces.clear();
        cities.clear();
        streets.clear();
        startedDatePicker.getEditor().clear();
        completionDatePicker.getEditor().clear();
        
        codeFormLabel.setGraphic(FormLabel.NONE);
        nameFormLabel.setGraphic(FormLabel.NONE);
    }

    /**
     * onResume resets the boolean values
     */
    @Override
    public void onResume() {
        super.onResume();
        resetValues();
        isEditMode = false;
        if(!projectCodeField.isEditable()){
            projectCodeField.setEditable(true);
        }
        createBtn.setText("Create");
        statusComboBox.getSelectionModel().select(0);
    }

    private void resetValues() {
        projectCodeAllowed = false;
        projectNameAllowed = false;
        /*
        provinceValueAllowed = false;
        cityValueAllowed = false;
        streetValueAllowed = false;
        dateStartedAllowed = false;
        dateToFinishAllowed = false; */

        // Refresh provinces list
        provinces.setAll(locations
                .stream()
                .distinct()
                .map(Location::getProvince)
                .collect(Collectors.toList()));
    }

    /**
     * Checks if has pending operation. This method will return true if at least
     * one of the fields contains an input value.
     *
     * @return
     */
    private boolean hasPendingOperation() {
        return !projectCodeField.getText().equals("")
                || !projectNameField.getText().equals("")
                || !provinceComboBox.getEditor().getText().equals("")
                || !cityComboBox.getEditor().getText().equals("")
                || !streetComboBox.getEditor().getText().equals("")
                || !startedDatePicker.getEditor().getText().equals("")
                || !completionDatePicker.getEditor().getText().equals("");
    }

    private void checkIfCanCreate() {
        if (projectCodeAllowed && projectNameAllowed /* && provinceValueAllowed
                && cityValueAllowed && streetValueAllowed
                && dateStartedAllowed && dateToFinishAllowed */) {
            createBtn.setDisable(false);
        }
    }

    private void onSaveAction() {
        // Assumed that all field values are validated
        Project newProject = new Project();
        newProject.setId(projectCodeField.getText());
        newProject.setName(projectNameField.getText());
        
        // retrieve location text values
        String street = streetComboBox.getEditor().getText();
        String city = cityComboBox.getEditor().getText();
        String province = provinceComboBox.getEditor().getText();

        // check if location already exist
        boolean locationExists = false;
        for (Location loc : locations) {
            // if location already exist, set it as new project's location
            if (loc.getStreet().equalsIgnoreCase(street)
                    && loc.getCity().equalsIgnoreCase(city)
                    && loc.getProvince().equalsIgnoreCase(province)) {
                newProject.setLocation(loc);
                newProject.setLocationId(loc.getId());
                locationExists = true;
                break;
            }
        }

        // If the location does not exist, create a new one and set it as the
        // new Project's location. Add it to the database and Location's observable list.
        if (!locationExists) {
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
        }
        
        // TODO: SET PROJECT'S CONTRACTOR
        // add or update the database if necessary

        // Retrieve the Date the project has created or started.
        newProject.setDateCreated(startedDatePicker.getValue());

        // Retrieve the Date the project's deadline.
        newProject.setDateToFinish(completionDatePicker.getValue());

        // Retrieve the initial status of the project.
        newProject.setStatus(statusComboBox.getSelectionModel().getSelectedItem());

        if(isEditMode){
            boolean updated = DBManager.updateProject(newProject);
            if(updated){
                // NOTE: When on edit mode, tempProject is ALWAYS assumed to be
                // not equal to null, so no need to check.
                for(int i=0; i<projects.size(); i++){
                    Project p = projects.get(i);
                    if(p.getId().equalsIgnoreCase(tempProject.getId())){
                        projects.set(i, newProject);
                        tempProject = null;
                        break;
                    }
                }
            }else{
                Alert alert = new Alert(AlertType.ERROR, "Failed to add project");
                alert.showAndWait();
            }
            
            // Clear the "on set screen event" set when this screen is called
            // on edit mode.
            screenController.setOnSetScreenEvent(null);
        }else{
            // Add new project to the database and to the list as well.
            boolean added = DBManager.addProject(newProject);
            if (added) {
                projects.add(newProject);
            } else {
                Alert alert = new Alert(AlertType.ERROR, "Failed to add project");
                alert.showAndWait();
            }
        }
        ControlledScreen previous = screenController.getBackStack().pull();
        screenController.setScreen(previous.getName());
    }

    private void onCancelAction() {
        ControlledScreen previous = screenController.getBackStack().pull();
        // confirm action
        if (hasPendingOperation() && !isEditMode) {
            Alert alertDialog = new Alert(AlertType.CONFIRMATION,
                                          "Are you sure you want to cancel adding new project?",
                                          ButtonType.YES, ButtonType.NO);
            alertDialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    screenController.setOnSetScreenEvent(null);
                    screenController.setScreen(previous.getName());
                }
            });
        } else {
            screenController.setOnSetScreenEvent(null);
            screenController.setScreen(previous.getName());
        }
    }
}
