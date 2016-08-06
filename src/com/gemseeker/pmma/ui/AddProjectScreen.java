package com.gemseeker.pmma.ui;

import com.gemseeker.pmma.ControlledScreen;
import com.gemseeker.pmma.data.DBManager;
import com.gemseeker.pmma.data.Location;
import com.gemseeker.pmma.data.Project;
import com.gemseeker.pmma.ui.components.FormLabel;
import com.gemseeker.pmma.ui.components.IconButton;
import com.gemseeker.pmma.ui.components.MaterialButton;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.stream.Collectors;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 *
 * @author RAFIS-FRED
 */
public class AddProjectScreen extends ControlledScreen {

    public static final String NAME = "addproject";

    private final VBox contentView;
    private final TextField projectCodeField;
    private final TextField projectNameField;
    private final ComboBox<String> provinceComboBox;
    private final ComboBox<String> cityComboBox;
    private final ComboBox<String> streetComboBox;
    private final DatePicker dateStarted, dateToFinish;
    private final ComboBox<String> statusComboBox;
    private final MaterialButton createBtn, cancelBtn;

    // data
    private ObservableList<Project> projects;
    private ObservableList<Location> locations;

    private ObservableList<String> provinces = FXCollections.observableArrayList();
    private ObservableList<String> cities = FXCollections.observableArrayList();
    private ObservableList<String> streets = FXCollections.observableArrayList();

    // These values are used to validate the form values. For now, the required
    // fields are the project code and name.
    private boolean projectCodeAllowed = false;
    private boolean projectNameAllowed = false;
    private boolean provinceValueAllowed = true;
    private boolean cityValueAllowed = true;
    private boolean streetValueAllowed = true;
    private boolean dateStartedAllowed = true;
    private boolean dateToFinishAllowed = true;

    public AddProjectScreen() {
        super(NAME);
        setAsChild(true); // this screen is a child of projects screen
        contentView = new VBox();
        contentView.setAlignment(Pos.TOP_CENTER);
        contentView.getStyleClass().add("projects-bg");

        HBox leftBox = new HBox();
        HBox.setHgrow(leftBox, Priority.ALWAYS);
        leftBox.setAlignment(Pos.CENTER_LEFT);
        leftBox.setPadding(new Insets(0, 0, 0, 8));
        leftBox.setSpacing(8);

        // back arrow icon button
        IconButton backBtn = new IconButton();
        backBtn.setIcon(getClass().getResourceAsStream("back_arrow.svg"));
        backBtn.setOnAction(evt -> onCancelAction());
        leftBox.getChildren().add(backBtn);

        HBox rightBox = new HBox();
        HBox.setHgrow(rightBox, Priority.ALWAYS);
        rightBox.setAlignment(Pos.CENTER_RIGHT);
        rightBox.setPadding(new Insets(0, 16, 0, 0));
        rightBox.setSpacing(8);

        HBox toolbar = new HBox();
        toolbar.setPrefHeight(40);
        toolbar.getStyleClass().add("menu-panel");
        toolbar.getChildren().addAll(leftBox, rightBox);

        contentView.getChildren().add(toolbar);

        // Project Code Group
        Label idAndName = new Label("Project Code & Name");
        FormLabel codeFormLabel = new FormLabel();
        projectCodeField = new TextField();
        projectCodeField.setPromptText("Project Code");
        projectCodeField.setPrefWidth(350);
        projectCodeField.textProperty().addListener((ObservableValue<? extends String> observable,
                String oldValue, String newValue) -> {
                    if (newValue.equals("")) {
                        projectCodeAllowed = false;
                        codeFormLabel.setGraphic(-1);
                    } else if (newValue.length() < 4) {
                        projectCodeAllowed = false;
                        codeFormLabel.setGraphic(FormLabel.TOO_SHORT);
                    } else if (projects != null) {
                        // check if exists
                        projects.stream().forEach((p) -> {
                            if (newValue.equalsIgnoreCase(p.getId())) {
                                projectCodeAllowed = false;
                                codeFormLabel.setGraphic(FormLabel.ALREADY_EXISTS);
                            } else {
                                projectCodeAllowed = true;
                                codeFormLabel.setGraphic(FormLabel.ALLOWED);
                            }
                        });
                    }
                    checkIfCanCreate();
                });
        HBox projectCodeBox = new HBox();
        projectCodeBox.setSpacing(8);
        projectCodeBox.setAlignment(Pos.CENTER_LEFT);
        projectCodeBox.getChildren().addAll(projectCodeField, codeFormLabel);

        // Project Name group
        FormLabel nameFormLabel = new FormLabel();
        projectNameField = new TextField();
        projectNameField.setPrefWidth(350);
        projectNameField.setPromptText("Project Name");
        projectNameField.textProperty().addListener((ObservableValue<? extends String> observable,
                String oldValue, String newValue) -> {
                    if (newValue.equals("")) {
                        projectNameAllowed = false;
                        nameFormLabel.setGraphic(-1);
                    } else if (newValue.length() < 6) {
                        projectNameAllowed = false;
                        nameFormLabel.setGraphic(FormLabel.TOO_SHORT);
                    } else if (projects != null) {
                        // check if exists
                        projects.stream().forEach((p) -> {
                            if (newValue.equalsIgnoreCase(p.getName())) {
                                projectNameAllowed = false;
                                nameFormLabel.setGraphic(FormLabel.ALREADY_EXISTS);
                            } else {
                                projectNameAllowed = true;
                                nameFormLabel.setGraphic(FormLabel.ALLOWED);
                            }
                        });
                    }
                    checkIfCanCreate();
                });
        HBox projectNameBox = new HBox();
        projectNameBox.setSpacing(8);
        projectNameBox.setAlignment(Pos.CENTER_LEFT);
        projectNameBox.getChildren().addAll(projectNameField, nameFormLabel);

        // Province group
        Label location = new Label("Project Location");
        provinceComboBox = new ComboBox<>();
        provinceComboBox.setEditable(true);
        provinceComboBox.setPrefWidth(350);
        provinceComboBox.setPromptText("Province");
        provinceComboBox.getEditor().textProperty().addListener((ObservableValue<? extends String> o,
                String old, String newValue) -> {
                    // provinceValueAllowed = !newValue.equals("");
                    checkIfCanCreate();
                });
        provinceComboBox.setItems(provinces);

        // This code sets the items of the cities ObservableList according to the
        // selected value of the provinceComboBox. Since cities is an instance of
        // ObservableList, the items of cityComboBox is automatically updated whenever
        // an item is added, updated or removed on the list.
        provinceComboBox.getSelectionModel().selectedIndexProperty()
                .addListener((ObservableValue<? extends Number> o, Number old, Number index) -> {
                    if(index.intValue() > -1){
                        String selectedProvinceStr = provinceComboBox.getItems().get(index.intValue());
                        if (!selectedProvinceStr.equals("")) {
                            cities.setAll(locations
                                    .stream()
                                    .distinct()
                                    .filter(loc -> loc.getProvince().equals(selectedProvinceStr))
                                    .map(Location::getCity)
                                    .collect(Collectors.toList()));
                        }
                    }
                });

        cityComboBox = new ComboBox<>();
        cityComboBox.setEditable(true);
        cityComboBox.setPrefWidth(350);
        cityComboBox.setPromptText("City or Municipality");
        cityComboBox.getEditor().textProperty().addListener((ObservableValue<? extends String> o,
                String old, String newValue) -> {
                    // cityValueAllowed = !newValue.equals("");
                    checkIfCanCreate();
                });
        cityComboBox.setItems(cities);

        // load all DISTINCT street or barangay of the city selected
        cityComboBox.getSelectionModel().selectedIndexProperty()
                .addListener((ObservableValue<? extends Number> o, Number old, Number index) -> {
                    if(index.intValue() > -1){
                        String selectedCityStr = cityComboBox.getItems().get(index.intValue());
                        if (!selectedCityStr.equals("")) {
                            streets.setAll(locations
                                    .stream()
                                    .distinct()
                                    .filter(loc -> loc.getCity().equals(selectedCityStr))
                                    .map(Location::getStreet)
                                    .collect(Collectors.toList()));
                        }
                    }
                });

        streetComboBox = new ComboBox<>();
        streetComboBox.setEditable(true);
        streetComboBox.setPrefWidth(350);
        streetComboBox.setPromptText("Street or Barangay");
        streetComboBox.getEditor().textProperty().addListener((ObservableValue<? extends String> o,
                String old, String newValue) -> {
                    // streetValueAllowed = !newValue.equals("");
                    checkIfCanCreate();
                });
        streetComboBox.setItems(streets);

        Label dates = new Label("Date Started & Estimated Date of Completion");
        dateStarted = new DatePicker();
        dateStarted.setPromptText("Date Started");
        dateStarted.getEditor().textProperty().addListener((ObservableValue<? extends String> o,
                String old, String newValue) -> {
                    // dateStartedAllowed = !newValue.equals("");
                    checkIfCanCreate();
                });

        dateToFinish = new DatePicker();
        dateToFinish.setPromptText("Date of Completion");
        dateToFinish.getEditor().textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            // dateToFinishAllowed = !newValue.equals("");
            checkIfCanCreate();
        });

        HBox dateBox = new HBox();
        dateBox.setSpacing(8);
        dateBox.setMaxWidth(350);
        HBox.setHgrow(dateStarted, Priority.ALWAYS);
        HBox.setHgrow(dateToFinish, Priority.ALWAYS);
        dateBox.getChildren().addAll(dateStarted, dateToFinish);

        Label status = new Label("Project Status");

        statusComboBox = new ComboBox<>(FXCollections.observableArrayList(
                Arrays.asList(Project.ON_GOING, Project.POSTPONED,
                        Project.TERMINATED, Project.FINISHED)
        ));
        statusComboBox.getSelectionModel().select(0); // always select On Going as initial value
        statusComboBox.setPrefWidth(350);

        createBtn = new MaterialButton();
        createBtn.setText("Create");
        createBtn.setPrefSize(120, 30);
        createBtn.setDefaultButton(true); // trigger when Enter is pressed
        createBtn.getStyleClass().add("paper-pink-button");
        createBtn.setDisable(true);
        createBtn.setOnAction(evt -> onSaveAction());

        cancelBtn = new MaterialButton();
        cancelBtn.setText("Cancel");
        cancelBtn.setPrefSize(120, 30);
        cancelBtn.getStyleClass().add("paper-pink-button");
        cancelBtn.setOnAction(evt -> onCancelAction());

        HBox actionBox = new HBox();
        actionBox.setSpacing(8);
        actionBox.setPrefWidth(350);
        actionBox.setPadding(new Insets(32, 0, 0, 0));
        actionBox.setAlignment(Pos.CENTER_LEFT);
        actionBox.getChildren().addAll(createBtn, cancelBtn);

        VBox formBox = new VBox();
        formBox.setSpacing(8);
        formBox.setPrefWidth(450);
        formBox.setPrefHeight(560);
        formBox.setAlignment(Pos.CENTER_LEFT);
        formBox.setPadding(new Insets(16));
        formBox.getChildren().addAll(
                idAndName,
                projectCodeBox,
                projectNameBox,
                location,
                provinceComboBox,
                cityComboBox,
                streetComboBox,
                dates,
                dateBox,
                status,
                statusComboBox,
                actionBox);

        BorderPane pane = new BorderPane();
        pane.setCenter(formBox);
        pane.setPadding(new Insets(16, 16, 16, 32));
        pane.getStyleClass().add("mug-bg");
        contentView.getChildren().add(pane);
    }

    @Override
    public Parent getContentView() {
        return contentView;
    }

    /**
     * onStart retrieves the projects and locations data and initialize the
     * ComboBoxes.
     */
    @Override
    public void onStart() {
        super.onStart();
        projects = ((MainActivityScreen) screenController).getProjects();
        locations = ((MainActivityScreen) screenController).getLocations();
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
        provinces.clear();
        cities.clear();
        streets.clear();
    }

    private void clearFields() {
        projectCodeField.clear();
        projectNameField.clear();
        statusComboBox.getSelectionModel().select(0);
        provinceComboBox.getEditor().clear();
        cityComboBox.getEditor().clear();
        streetComboBox.getEditor().clear();
    }

    /**
     * onResume resets the boolean values
     */
    @Override
    public void onResume() {
        super.onResume();
        projectCodeAllowed = false;
        projectNameAllowed = false;
        /*
         provinceValueAllowed = false;
         cityValueAllowed = false;
         streetValueAllowed = false;
         dateStartedAllowed = false;
         dateToFinishAllowed = false; */
        
        projects = ((MainActivityScreen) screenController).getProjects();
        locations = ((MainActivityScreen) screenController).getLocations();
        // Refresh provinces items
        provinces.setAll(locations
                .stream()
                .distinct()
                .map(Location::getProvince)
                .collect(Collectors.toList()));
    }

    private boolean hasPendingOperation() {
        return !projectCodeField.getText().equals("")
                || !projectNameField.getText().equals("")
                || !provinceComboBox.getEditor().getText().equals("")
                || !cityComboBox.getEditor().getText().equals("")
                || !streetComboBox.getEditor().getText().equals("")
                || !dateStarted.getEditor().getText().equals("")
                || !dateToFinish.getEditor().getText().equals("");
    }

    private void checkIfCanCreate() {
        if (projectCodeAllowed && projectNameAllowed && provinceValueAllowed
                && cityValueAllowed && streetValueAllowed
                && dateStartedAllowed && dateToFinishAllowed) {
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
        for (Location loc : ((MainActivityScreen) screenController).getLocations()) {
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
        // new Project's location. Do not forget to add it to the database and
        // Location's observable list.
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
            
            System.out.println("Local Locations list contains " + locations.size());
            System.out.println("Main Locations list contains " + ((MainActivityScreen)screenController).getLocations().size());
        }

        // Retrieve the Date the project has created or started.
        LocalDate started = dateStarted.getValue();
        Calendar calStarted = Calendar.getInstance();
        calStarted.set(Calendar.MONTH, started.getMonthValue());
        calStarted.set(Calendar.DAY_OF_MONTH, started.getDayOfMonth());
        calStarted.set(Calendar.YEAR, started.getYear());
        newProject.setDateCreated(calStarted.getTime());

        // Retrieve the Date the project's deadline.
        LocalDate toFinish = dateToFinish.getValue();
        Calendar toFinishCal = Calendar.getInstance();
        toFinishCal.set(Calendar.MONTH, toFinish.getMonthValue());
        toFinishCal.set(Calendar.DAY_OF_MONTH, toFinish.getDayOfMonth());
        toFinishCal.set(Calendar.YEAR, toFinish.getYear());
        newProject.setDateToFinish(toFinishCal.getTime());

        // Retrieve the initial status of the project.
        newProject.setStatus(statusComboBox.getSelectionModel().getSelectedItem());

        // Add new project to the database and to the list as well.
        DBManager.addProject(newProject);
        projects.add(newProject);

        System.out.println("Main list contains " + ((MainActivityScreen) screenController).getProjects().size());
        System.out.println("Class list contains " + projects.size());

        ControlledScreen previous = screenController.getBackStack().pull();
        screenController.setScreen(previous.getName());
    }

    private void onCancelAction() {
        ControlledScreen previous = screenController.getBackStack().pull();
        // confirm action
        if (hasPendingOperation()) {
            Alert alertDialog = new Alert(AlertType.CONFIRMATION,
                    "Are you sure you want to cancel adding new project?",
                    ButtonType.YES, ButtonType.NO);
            alertDialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    screenController.setScreen(previous.getName());
                }
            });
        } else {
            screenController.setScreen(previous.getName());
        }
    }
}
