package com.gemseeker.pmma.controllers;

import com.gemseeker.pmma.ControlledScreen;
import com.gemseeker.pmma.data.Contact;
import com.gemseeker.pmma.data.DBManager;
import com.gemseeker.pmma.data.Location;
import com.gemseeker.pmma.data.Project;
import com.gemseeker.pmma.fxml.ScreenLoader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.stream.Collectors;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author RAFIS-FRED
 */
public class AddProjectScreen extends ControlledScreen {

    public static final String NAME = "AddProjectScreen";
    private static final int PROJECT_FORM_GROUP = 11;
    private static final int CONTACT_FORM_GROUP = 22;
    private int currentFormGroup;
    
    // Project Form Group
    @FXML VBox projectFormGroup;
    @FXML TextField projectCodeField;
    @FXML TextField projectNameField;
    @FXML ComboBox<String> provinceComboBox;
    @FXML ComboBox<String> cityComboBox;
    @FXML ComboBox<String> streetComboBox;
    @FXML DatePicker startedDatePicker;
    @FXML DatePicker completionDatePicker;
    @FXML ComboBox<String> statusComboBox;
    @FXML HBox actionBox;
    @FXML Button nextBtn;
    @FXML Button finishBtn;
    @FXML Button cancelBtn;
    
    // Contact Form Group
    @FXML VBox contactFormGroup;
    @FXML ComboBox contactComboBox;
    @FXML TextField companyField;
    @FXML VBox phoneBox;
    @FXML TextField defaultPhoneField;
    @FXML Button addPhoneBtn;
    @FXML VBox emailBox;
    @FXML TextField defaultEmailField;
    @FXML Button addEmailBtn;
    @FXML Button cancelBtn1;
    @FXML Button backBtn;
    @FXML Button finishBtn1;

    // Forms Container
    @FXML StackPane stackPane;
    
    // data
    private ObservableList<Project> projects;
    private ObservableList<Location> locations;
    private ObservableList<Contact> contacts;
    
    private final ObservableList<String> provinces = FXCollections.observableArrayList();
    private final ObservableList<String> cities = FXCollections.observableArrayList();
    private final ObservableList<String> streets = FXCollections.observableArrayList();

    // These values are used to validate the form values. For now, the required
    // fields are the project code and name.
    private boolean projectCodeAllowed = false;
    private boolean projectNameAllowed = false;
    private boolean dateStartedAllowed = false;
    private boolean dateOfCompletionAllowed = false;

    /***************************************************************************
     * Edit Mode Member                                                        *
     ***************************************************************************/
    private boolean isEditMode = false;
    private Project tempProject = null;
    
    public AddProjectScreen(){
        super(NAME);
        setAsChild(true);
        initComponents();
    }
    
    private void initComponents() {
        setContentView(ScreenLoader.loadScreen(this, "add_project_view.fxml"));
        
        /**********************
         * Project Form Group *
         **********************/
        
        projectCodeField.textProperty()
                .addListener((ObservableValue<? extends String> ov,String old, 
                        String newValue) -> {
            validateProjectCode(newValue);
        });
        
        projectNameField.textProperty()
                .addListener((ObservableValue<? extends String> ov,
                        String old, String newValue) -> {
            validateProjectName(newValue);
        });
        
        provinceComboBox.setItems(provinces);
        provinceComboBox.getSelectionModel().selectedIndexProperty()
                .addListener((ObservableValue<? extends Number> o, 
                        Number old, Number index) -> {
                    if (index.intValue() > -1) {
                        String selectedProvinceStr = provinceComboBox.getItems().get(index.intValue());
                        cities.setAll(locations
                            .stream()
                            .distinct()
                            .filter(loc -> loc.getProvince().equals(selectedProvinceStr))
                            .map(Location::getCity)
                            .collect(Collectors.toList()));
                    }
                });
        
        cityComboBox.setItems(cities);
        // load all DISTINCT street or barangay of the city selected
        cityComboBox.getSelectionModel().selectedIndexProperty()
                .addListener((ObservableValue<? extends Number> o,
                        Number old, Number index) -> {
                    if (index.intValue() > -1) {
                        String selectedCityStr = cityComboBox.getItems().get(index.intValue());
                        streets.setAll(locations
                            .stream()
                            .distinct()
                            .filter(loc -> loc.getCity().equals(selectedCityStr))
                            .map(Location::getStreet)
                            .collect(Collectors.toList()));
                    }
                });
        
        startedDatePicker.valueProperty().addListener((ObservableValue<? extends LocalDate> o, LocalDate old, LocalDate newValue) -> {
            dateStartedAllowed = newValue != null;
            checkIfCanCreate();
        });
        
        completionDatePicker.valueProperty().addListener((ObservableValue<? extends LocalDate> o, LocalDate old, LocalDate newValue) -> {
            dateOfCompletionAllowed = newValue != null;
            checkIfCanCreate();
        });
        
        streetComboBox.setItems(streets);
        statusComboBox.setItems(FXCollections.observableArrayList(
                Project.ON_GOING, Project.POSTPONED, Project.TERMINATED, Project.FINISHED
        ));
        statusComboBox.getSelectionModel().select(0);
        
        nextBtn.setOnAction(evt -> onNextAction());
        cancelBtn.setOnAction(evt -> onCancelAction());
        finishBtn.setOnAction(evt -> {
            saveProject();
            onCancelAction();
        });
        
        /**********************
         * Contact Form Group *
         **********************/
        
        // enable 'Add Phone' button if defaultPhoneField has valid input
        defaultPhoneField.textProperty().addListener((ObservableValue<? extends String> ov, String old, String newValue) -> {
            addPhoneBtn.setDisable(!phoneIsValid(newValue));
        });
        
        // enable 'Add Email' button if defaultEmailField has valid input
        defaultEmailField.textProperty().addListener((ObservableValue<? extends String> ov, String old, String newValue) -> {
            addEmailBtn.setDisable(!emailIsValid(newValue));
        });
        
        backBtn.setOnAction(evt -> onBackAction());
        cancelBtn1.setOnAction(evt -> onCancelAction());
        finishBtn1.setOnAction(evt -> {
            saveProject();
            saveContact();
            onCancelAction();
        });
        
        
        // Now that all components are set. Turn the Contact Form Group visibility
        // to false or remove it from the StackPane.
        stackPane.getChildren().remove(contactFormGroup);
        currentFormGroup = PROJECT_FORM_GROUP;
    }
    
    private void validateProjectCode(String projectCode){
        final ObservableList<String> styles = projectCodeField.getStyleClass();
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
        final ObservableList<String> styles = projectNameField.getStyleClass();
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
        boolean hasMatch = projects.stream().anyMatch((p) -> (p.getId().equals(codeStr)));
        if(isEditMode && hasMatch){
            return true;
        }else{
            return codeStr.length() >= 4 
                    && (projects != null && !isEditMode) 
                    && !hasMatch;
        }
    }
    
    private boolean isProjectNameValid(String nameStr){
        boolean hasMatch = projects.stream().anyMatch((p) -> (nameStr.equalsIgnoreCase(p.getName())));
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
    
    private boolean phoneIsValid(String phoneNumStr){
        return !phoneNumStr.equals("") && phoneNumStr.length() >= 10;
        // TODO: add proper phone validation here
    }
    
    private boolean emailIsValid(String emailStr){
        return !emailStr.equals("");
        // TODO: add proper email validation here
    }
    
    public void editProject(Project project) {
        onEdit();
        
        // DO NOT let the user change the project code!
        projectCodeField.setText(project.getId());
        projectCodeField.setEditable(false);
        projectCodeAllowed = true; // just to be sure
        
        projectNameField.setText(project.getName());
        projectNameAllowed = true;

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
        tempProject = project;
    }
    
    private void onEdit() {
        isEditMode = true;
        projectCodeField.setEditable(false);
        finishBtn.setText("Update");
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
   }

    private void resetValues() {
       projectCodeAllowed = false;
       projectNameAllowed = false;
       /*
       provinceValueAllowed = false;
       cityValueAllowed = false;
       streetValueAllowed = false; */

       dateStartedAllowed = false;
       dateOfCompletionAllowed = false;

       // Refresh provinces list
       provinces.setAll(locations
               .stream()
               .distinct()
               .map(Location::getProvince)
               .collect(Collectors.toList()));
   }

    private void checkIfCanCreate() {
       if (projectCodeAllowed && projectNameAllowed /* && provinceValueAllowed
               && cityValueAllowed && streetValueAllowed */
               && dateStartedAllowed && dateOfCompletionAllowed) {
           nextBtn.setDisable(false);
           finishBtn.setDisable(false);
       }
   }

    private void saveProject() {
        // Assumed that all field values are validated
        Project newProject = new Project();
        newProject.setId(projectCodeField.getText());
        newProject.setName(projectNameField.getText());
        
        // retrieve location text values
        String street = streetComboBox.getEditor().getText();
        String city = cityComboBox.getEditor().getText();
        String province = provinceComboBox.getEditor().getText();

        // check if location already exist
        boolean locationExists = locations
                .stream()
                .anyMatch(loc -> (loc.getProvince().equals(province) 
                        && loc.getCity().equals(city) 
                        && loc.getStreet().equals(street)));
        
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
        }else{
            Location location = locations
                    .stream()
                    .filter(loc -> (loc.getProvince().equals(province) 
                            && loc.getCity().equals(city) 
                            && loc.getStreet().equals(street)))
                    .findFirst()
                    .get();
            newProject.setLocationId(location.getId());
            newProject.setLocation(location);
        }

        newProject.setDateCreated(startedDatePicker.getValue());
        newProject.setDateToFinish(completionDatePicker.getValue());
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
    }
    
    private void saveContact(){
        
    }

    private void onCancelAction() {
        ControlledScreen screen = screenController.getBackStack().pull();
        screenController.setScreen(screen.getName());
    }
    
    private void onNextAction(){
        if(currentFormGroup == PROJECT_FORM_GROUP){
            stackPane.getChildren().remove(projectFormGroup);
            stackPane.getChildren().add(contactFormGroup);
            currentFormGroup = CONTACT_FORM_GROUP;
        }
    }
    
    private void onBackAction(){
        if(currentFormGroup == CONTACT_FORM_GROUP){
            stackPane.getChildren().remove(contactFormGroup);
            stackPane.getChildren().add(projectFormGroup);
            currentFormGroup = PROJECT_FORM_GROUP;
        }
    }

    /***************************************************************************
     *                                                                         *
     * ControlledScreen Overridden Methods                                     *
     *                                                                         *
     ***************************************************************************/
    
    /**
     * onStart sets the list of Projects, Locations and Contractors
     */
    @Override
    public void onStart() {
        projects = ((MainActivityScreen) screenController).getProjects();
        locations = ((MainActivityScreen) screenController).getLocations();
        
        // extract all the Provinces from Locations list
        provinces.setAll(locations
                .stream()
                .distinct()
                .map(Location::getProvince)
                .collect(Collectors.toList()));
    }
    
    @Override
    public void onResume(){
        clearFields();
        resetValues();
        isEditMode = false;
        if(!projectCodeField.isEditable()){
            projectCodeField.setEditable(true);
        }
//        createBtn.setText("Create");
    }

    @Override
    public void onPause() {
        super.onPause();
        Task<Void> task = new Task() {
            @Override
            protected Object call() throws Exception {
                if(currentFormGroup == CONTACT_FORM_GROUP){
                    stackPane.getChildren().remove(contactFormGroup);
                    stackPane.getChildren().add(projectFormGroup);
                    currentFormGroup = PROJECT_FORM_GROUP;
                    System.out.println("set current form group to project form");
                }
                return null;
            }
        };
        Thread t = new Thread(task);
        t.start();
    }
}
