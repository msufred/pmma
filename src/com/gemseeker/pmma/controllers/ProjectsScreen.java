package com.gemseeker.pmma.controllers;

import static com.gemseeker.pmma.AppConstants.IN_ANIM_INTERPOLATOR;
import static com.gemseeker.pmma.AppConstants.OUT_ANIM_INTERPOLATOR;
import com.gemseeker.pmma.controllers.viewprojects.ViewProjectScreen;
import com.gemseeker.pmma.ControlledScreen;
import com.gemseeker.pmma.data.DBManager;
import com.gemseeker.pmma.data.History;
import com.gemseeker.pmma.data.Location;
import com.gemseeker.pmma.data.Project;
import com.gemseeker.pmma.fxml.ScreenLoader;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import com.gemseeker.pmma.utils.Log;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 *
 * @author RAFIS-FRED
 */
public class ProjectsScreen extends ControlledScreen {

    public static final String NAME = "projects";
    
    static final Duration ANIM_DURATION_LONG = new Duration(400);
    static final Duration ANIM_DURATION_SHORT = new Duration(200);
    
    // sidebar components
    @FXML Rectangle rectangle;
    @FXML ToggleButton toggleAllProjects;
    @FXML ToggleButton toggleOnGoing;
    @FXML ToggleButton togglePostponed;
    @FXML ToggleButton toggleTerminated;
    @FXML ToggleButton toggleFinished;
    @FXML Label onGoingLabel;
    @FXML Label postponedLabel;
    @FXML Label terminatedLabel;
    @FXML Label finishedLabel;
    @FXML Button addCategoryBtn;
    
    @FXML Button viewBtn;
    @FXML Button addBtn;
    @FXML Button deleteBtn;
    @FXML StackPane projectStackPane;
    @FXML StackPane tableContainer;
    @FXML TableView<Project> table;
    @FXML TableColumn<Project, String> nameCol;
    @FXML TableColumn<Project, String> locationCol;
    @FXML TableColumn<Project, String> dateStartedCol;
    @FXML TableColumn<Project, String> dateCompletionCol;
    @FXML TableColumn<Project, String> statusCol;
    @FXML TextField searchField;

    private ProgressIndicator progressIndicator;
    
    private DateTimeFormatter DEFAULT_DATE_FORMAT;
    
    private ProjectForm projectForm;
    private Pane overlay;
    private ObservableList<Project> projects;
    private FilteredList<Project> filteredList;
    
    // String properties used to bind project count values by status to a Label.
    private final SimpleStringProperty onGoingCount = new SimpleStringProperty("0");
    private final SimpleStringProperty postponedCount = new SimpleStringProperty("0");
    private final SimpleStringProperty terminatedCount = new SimpleStringProperty("0");
    private final SimpleStringProperty finishedCount = new SimpleStringProperty("0");

    // -- tracks if the data has been loaded from the database
    // -- We don't have to always access the database when onResume is invoked (it happens when the application changes
    // current screen). We only have to query the database when the data is not loaded or when data changes e.i., new
    // entry has been added, removed or updated.
    private boolean dataLoaded = false;
    
    public ProjectsScreen(){
        super(NAME);
        initComponents();
    }

    private void initComponents() {
        // Create the DateTimeFormatter
        DateTimeFormatterBuilder formatterBuilder = new DateTimeFormatterBuilder();
        formatterBuilder.appendPattern("MMM dd, yyyy");
        DEFAULT_DATE_FORMAT = formatterBuilder.toFormatter();

        // set content view
        setContentView(ScreenLoader.loadScreen(ProjectsScreen.this, "projects.fxml"));

        // Sidebar Components
        ToggleGroup sidebarToggles = new ToggleGroup();
        sidebarToggles.getToggles().setAll(toggleAllProjects, toggleOnGoing,
                togglePostponed, toggleTerminated, toggleFinished);
        
        // consume mouse pressed event on already selected Toggle
        sidebarToggles.getToggles().forEach(toggle -> {
            ((ToggleButton) toggle).addEventFilter(MouseEvent.MOUSE_PRESSED, evt -> {
                if(toggle.isSelected()){
                    evt.consume();
                }
            });
        });
        
        // bind String properties to count labels
        onGoingLabel.textProperty().bind(onGoingCount);
        postponedLabel.textProperty().bind(postponedCount);
        terminatedLabel.textProperty().bind(terminatedCount);
        finishedLabel.textProperty().bind(finishedCount);

        // init TableColumns
        nameCol.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
        
        locationCol.setCellValueFactory((value) -> {
            Location location = value.getValue().getLocation().get();
            if (location == null) {
                return new SimpleStringProperty("");
            } else {
                return new SimpleStringProperty(value.getValue().getLocation().get().toString());
            }
        });
        
        dateStartedCol.setCellValueFactory((value) -> {
            return new SimpleStringProperty(value.getValue().getDateCreated().format(DEFAULT_DATE_FORMAT));
        });
        
        dateCompletionCol.setCellValueFactory((value) -> {
            return new SimpleStringProperty(value.getValue().getDateToFinish().format(DEFAULT_DATE_FORMAT));
        });
        
        statusCol.setCellValueFactory(cellData -> cellData.getValue().getStatusProperty());
        
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
        table.getSelectionModel().selectedIndexProperty().addListener((observableValue, oldValue, newValue) -> {
            disableActionButtons(newValue.intValue() < 0);
        });
        
        // initialize search field
        searchField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue.isEmpty()){
                Toggle toggle = sidebarToggles.getSelectedToggle();
                if(toggle.equals(toggleAllProjects)){
                    allProjectsSelectedAction(new ActionEvent());
                }else if(toggle.equals(toggleOnGoing)){
                    onGoingProjectsSelectedAction(new ActionEvent());
                }else if(toggle.equals(togglePostponed)){
                    postponedProjectsSelectedAction(new ActionEvent());
                }else if(toggle.equals(toggleTerminated)){
                    terminatedProjectsSelectedAction(new ActionEvent());
                }else if(toggle.equals(toggleFinished)){
                    finishedProjectsSelectedAction(new ActionEvent());
                }
            }else{
                if(filteredList != null){
                    filteredList.setPredicate((Project p) -> {
                        return p.getNameProperty().get().contains(newValue) ||
                                p.getLocation().toString().contains(newValue);
                    });
                }
            }
        });

        projectForm = new ProjectForm();
        overlay = new Pane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
        overlay.setOnMouseClicked(evt -> hideProjectForm());

        progressIndicator = new ProgressIndicator();
        progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        progressIndicator.setMaxSize(48, 48);
    }

    private void disableActionButtons(boolean disable){
        deleteBtn.setDisable(disable);
        viewBtn.setDisable(disable);
    }
    
    private void moveIndicator(double to){
        Timeline anim = new Timeline(new KeyFrame(ANIM_DURATION_LONG,
                new KeyValue(rectangle.translateYProperty(), to, IN_ANIM_INTERPOLATOR)
        ));
        anim.play();
    }
    
    /***************************************************************************
     *                                                                         *
     *                  Actions set within the FXML file.                      *
     *                                                                         *
     ***************************************************************************/
    
    // Sidebar Actions
    @FXML
    public void allProjectsSelectedAction(ActionEvent e){
        filteredList.setPredicate(p -> true);
        moveIndicator(toggleAllProjects.getBoundsInParent().getMinY());
        onGoingLabel.setVisible(false);
        postponedLabel.setVisible(false);
        terminatedLabel.setVisible(false);
        finishedLabel.setVisible(false);
    }

    @FXML
    public void onGoingProjectsSelectedAction(ActionEvent e){
        filteredList.setPredicate(p -> p.getStatusProperty().get().equals(Project.ON_GOING));
        moveIndicator(toggleOnGoing.getParent().getBoundsInParent().getMinY());
        onGoingLabel.setVisible(true);
        postponedLabel.setVisible(false);
        terminatedLabel.setVisible(false);
        finishedLabel.setVisible(false);
    }
    
    @FXML
    public void postponedProjectsSelectedAction(ActionEvent e){
        filteredList.setPredicate(p -> p.getStatusProperty().get().equals(Project.POSTPONED));
        moveIndicator(togglePostponed.getParent().getBoundsInParent().getMinY());
        onGoingLabel.setVisible(false);
        postponedLabel.setVisible(true);
        terminatedLabel.setVisible(false);
        finishedLabel.setVisible(false);
    }
    
    @FXML
    public void terminatedProjectsSelectedAction(ActionEvent e){
        filteredList.setPredicate(p -> p.getStatusProperty().get().equals(Project.TERMINATED));
        moveIndicator(toggleTerminated.getParent().getBoundsInParent().getMinY());
        onGoingLabel.setVisible(false);
        postponedLabel.setVisible(false);
        terminatedLabel.setVisible(true);
        finishedLabel.setVisible(false);
    }
    
    @FXML
    public void finishedProjectsSelectedAction(ActionEvent e){
        filteredList.setPredicate(p -> p.getStatusProperty().get().equals(Project.FINISHED));
        moveIndicator(toggleFinished.getParent().getBoundsInParent().getMinY());
        onGoingLabel.setVisible(false);
        postponedLabel.setVisible(false);
        terminatedLabel.setVisible(false);
        finishedLabel.setVisible(true);
    }
    
    @FXML
    public void onAddAction(ActionEvent e){
        projectForm.clearFields();
        showProjectForm();
    }
    
    @FXML
    public void onDeleteAction(ActionEvent e){
        Project p = table.getSelectionModel().getSelectedItem();
        if(p != null){
            Alert alert = new Alert(AlertType.CONFIRMATION, "You are about to delete project \"" + p.getNameProperty().get() + "\". Do you want to continue?");
            Optional<ButtonType> result = alert.showAndWait();
            result.ifPresent(btn -> {
                if(btn.equals(ButtonType.OK)){
                    boolean deleted = DBManager.deleteProject(p.getIdProperty().get());
                    if(deleted){
                        // remove from projects list
                        projects.remove(p);

                        // record action
                        History hist = new History();
                        hist.setDate(LocalDateTime.now());
                        hist.setNotes("Delete project \"" + p.getNameProperty().get() + "\".");
//                        ((MainActivityScreen) screenController).getHistories().add(hist);
                        DBManager.addHistory(hist);

                        if(projects.isEmpty()){
                            deleteBtn.setDisable(true);
                            viewBtn.setDisable(true);
                        }
                    }
                }
            });
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
        
        FadeTransition fadeIn = new FadeTransition(ANIM_DURATION_SHORT, overlay);
        fadeIn.setToValue(1.0);
        fadeIn.setInterpolator(IN_ANIM_INTERPOLATOR);
        fadeIn.setOnFinished(evt -> {
            ScaleTransition scaleIn = new ScaleTransition(ANIM_DURATION_LONG, projectForm.contentView);
            scaleIn.setInterpolator(IN_ANIM_INTERPOLATOR);
            scaleIn.setByX(1.0);
            scaleIn.setByY(1.0);
            scaleIn.play();
        });
        fadeIn.play();
    }
    
    private void hideProjectForm(){
        ScaleTransition scaleOut = new ScaleTransition(ANIM_DURATION_LONG, projectForm.contentView);
        scaleOut.setToX(0.0);
        scaleOut.setToY(0.0);
        scaleOut.setInterpolator(OUT_ANIM_INTERPOLATOR);
        scaleOut.setOnFinished(evt -> {
            FadeTransition fadeOut = new FadeTransition(ANIM_DURATION_SHORT, overlay);
            fadeOut.setToValue(0.0);
            fadeOut.setInterpolator(OUT_ANIM_INTERPOLATOR);
            fadeOut.setOnFinished(e -> {
                projectStackPane.getChildren().remove(overlay);
                projectStackPane.getChildren().remove(projectForm.contentView);
            });
            fadeOut.play();
        });
        scaleOut.play();
    }

    private void recount(){
        Platform.runLater(()->{
            onGoingCount.set(getProjectCount(Project.ON_GOING) + "");
            postponedCount.set(getProjectCount(Project.POSTPONED) + "");
            terminatedCount.set(getProjectCount(Project.TERMINATED) + "");
            finishedCount.set(getProjectCount(Project.FINISHED) + "");
        });
    }

    private long getProjectCount(String projectStatus) {
        return projects.stream()
                .filter(p -> p.getStatusProperty().isEqualTo(projectStatus).get())
                .count();
    }
    
    /***************************************************************************
     *                                                                         *
     * ControlledScreen Overridden Methods                                     *
     *                                                                         *
     ***************************************************************************/
    
    @Override
    public void onStart() {
        projects = FXCollections.observableArrayList();
        // wrap projects list to a FilteredList (initially display all Projects)
        filteredList = new FilteredList<>(projects, p -> true);
        SortedList<Project> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedList);
    }

    /**
     * onResume refreshes the table.
     */
    @Override
    public void onResume() {
        if (!dataLoaded) {
            // we only need to access (or query) the database when the data is not yet loaded
            Task loadProjectTask = new Task() {
                @Override
                protected Object call() throws Exception {
                    Log.v("ProjectScreen", "loading projects from database");

                    tableContainer.getChildren().add(progressIndicator);
                    projects.setAll(DBManager.getProjects());
                    return null;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    Log.v("ProjectScreen", "Succeeded! Refreshing table");
                    recount();
                    if (table != null) {
                        table.refresh();
                    }
                    tableContainer.getChildren().remove(progressIndicator);
                    dataLoaded = true;
                }

                @Override
                protected void failed() {
                    super.failed();
                    Log.e("ProjectScreen", "Failed to load projects from database");
                    tableContainer.getChildren().remove(progressIndicator);
                }
            };

            Thread thread = new Thread(loadProjectTask);
            thread.setDaemon(true);
            thread.start();
        }
    }
    
    /**************************************************************************/
    
    private class ProjectForm {
        
        Parent contentView;
        @FXML TextField nameField;
        @FXML DatePicker dateStarted;
        @FXML DatePicker dateCompletion;
        @FXML ComboBox<String> status;
        @FXML Button doneBtn;
        @FXML ProgressBar saveProgressIndicator;

        private boolean projectNameAllowed = false;
        private boolean dateStartedAllowed = false;
        private boolean dateCompletionAllowed = false;
        private final DateTimeFormatter dateFormatter;
        
        public ProjectForm(){
            contentView = ScreenLoader.loadScreen(ProjectForm.this, "project_form.fxml");

            // validates if project name is available as the user types on the field
            nameField.textProperty().addListener((observableValue,  oldvalue, newValue) -> {
                validateProjectName(newValue);
            });
            
            dateStarted.valueProperty().addListener((observableValue,  oldvalue, newValue) -> {
                dateStartedAllowed = newValue != null;
                checkIfCanCreate();
            });

            dateCompletion.valueProperty().addListener((observableValue,  oldvalue, newValue) -> {
                dateCompletionAllowed = newValue != null;
                checkIfCanCreate();
            });
            
            status.setItems(FXCollections.observableArrayList(Arrays.asList(
                    Project.FOR_FUNDING,
                    Project.FUNDED,
                    Project.ON_GOING,
                    Project.POSTPONED,
                    Project.TERMINATED,
                    Project.FINISHED
            )));
            status.getSelectionModel().select(0);
            
            DateTimeFormatterBuilder dfb = new DateTimeFormatterBuilder();
            dfb.appendPattern("M/dd/yyyy");
            dateFormatter = dfb.toFormatter();
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

        private boolean isProjectNameValid(String nameStr) {
            boolean hasMatch = projects.stream().anyMatch((p) -> (nameStr.equalsIgnoreCase(p.getNameProperty().get())));
            return hasMatch || nameStr.length() >= 6 && projects != null;
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
            if (projectNameAllowed && dateStartedAllowed && dateCompletionAllowed) {
                doneBtn.setDisable(false);
            }
        }


        private void clearFields(){
            nameField.clear();
            dateStarted.getEditor().clear();
            dateCompletion.getEditor().clear();
            status.getSelectionModel().select(0);
        }
        
        @FXML
        public void onCancelAction(ActionEvent event){
            hideProjectForm();
        }
        
        @FXML
        public void onSaveAction(ActionEvent event){
            // Assumed that all field values are validated
            Project newProject = new Project();
            newProject.setName(nameField.getText());
            newProject.setDateCreated(dateStarted.getValue());
            newProject.setDateToFinish(dateCompletion.getValue());
            newProject.setStatus(status.getSelectionModel().getSelectedItem());

            // save on new thread
            Thread t = new Thread(new Task<Integer>() {
                @Override
                protected Integer call() throws Exception {
                    saveProgressIndicator.setVisible(true);
                    return DBManager.addProject(newProject);
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    int projectId = getValue();
                    if (projectId == -1) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to add project");
                        alert.showAndWait();
                    } else {
                        projects.add(newProject);
                        recount();

                        // add History entry from another Thread
                        new Thread(new Task() {
                            @Override
                            protected Object call() throws Exception {
                                History hist = new History();
                                hist.setDate(LocalDateTime.now());
                                hist.setNotes("Added new project \"" + newProject.getNameProperty().get() + "\".");
                                DBManager.addHistory(hist);
                                return null;
                            }
                        }).start();
                    }
                    saveProgressIndicator.setVisible(false);
                    hideProjectForm();
                }
            });

            t.setDaemon(true);
            t.start();
        }
    }
}
