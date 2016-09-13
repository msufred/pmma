package com.gemseeker.pmma.controllers;

import com.gemseeker.pmma.animations.interpolators.EasingMode;
import com.gemseeker.pmma.animations.interpolators.QuarticInterpolator;
import com.gemseeker.pmma.data.Attachment;
import com.gemseeker.pmma.data.DBManager;
import com.gemseeker.pmma.data.History;
import com.gemseeker.pmma.data.Project;
import com.gemseeker.pmma.data.ProjectUpdate;
import com.gemseeker.pmma.fxml.ScreenLoader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Optional;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Duration;

/**
 *
 * @author Gem Seeker
 */
public class UpdatesTab extends ViewProjectScreen.TabScreen {
    
    public static final String NAME = "UpdatesTab";
    
    private static final Interpolator IN_INTERPOLATOR = new QuarticInterpolator(EasingMode.EASE_OUT);
    private static final Interpolator OUT_INTERPOLATOR = new QuarticInterpolator(EasingMode.EASE_IN);
    private static final Duration ANIM_DURATION_LONG = new Duration(400);
    private static final Duration ANIM_DURATION_SHORT = new Duration(200);
    
    @FXML Button addBtn;
    @FXML Button editBtn;
    @FXML Button deleteBtn;
    @FXML Button viewBtn;
    @FXML TableView<ProjectUpdate> updatesTable;
    @FXML TableColumn<ProjectUpdate, String> levelCol;
    @FXML TableColumn<ProjectUpdate, String> dateCol;
    @FXML TableColumn<ProjectUpdate, String> remarksCol;
    @FXML TableColumn<ProjectUpdate, Boolean> attachmentCol;
    @FXML TableColumn<ProjectUpdate, String> statusCol;
    
    private final UpdateForm updateForm;
    private final ViewProjectUpdate viewUpdate;
    private final Pane overlay;
    private ObservableList<ProjectUpdate> updates;
    private FilteredList<ProjectUpdate> filteredList; // for search function
    
    private final DateTimeFormatter dateFormatter;
    
    private SimpleBooleanProperty loadingDataProperty = new SimpleBooleanProperty(false);
    
    public UpdatesTab(){
        super(NAME);
        initComponents();
        
        DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
        builder.appendPattern("MMM dd, yyyy");
        dateFormatter = builder.toFormatter();
        
        updateForm = new UpdateForm();
        viewUpdate = new ViewProjectUpdate();
        
        overlay = new Pane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4);");
    }

    private void initComponents(){
        mContentView = ScreenLoader.loadScreen(this, "project_updates_tabview.fxml");

        // bind action buttons
        addBtn.disableProperty().bind(loadingDataProperty);
        
        // date col
        dateCol.setCellValueFactory((TableColumn.CellDataFeatures<ProjectUpdate, String> value) -> {
            String timeStr = dateFormatter.format(value.getValue().getDateCreated());
            return new SimpleStringProperty(timeStr);
        });
        dateCol.setSortType(TableColumn.SortType.DESCENDING);
        
        remarksCol.setCellValueFactory(cellData -> cellData.getValue().getNotes());
        levelCol.setCellValueFactory(cellData -> cellData.getValue().getLevel());
        statusCol.setCellValueFactory(cellData -> cellData.getValue().getStatus());
        attachmentCol.setCellValueFactory(cellData -> cellData.getValue().hasAttachment());
        
        // attachment col
        attachmentCol.setCellFactory((TableColumn<ProjectUpdate, Boolean> value) -> {
            return new TableCell<ProjectUpdate, Boolean>(){
                final Label attachmentIcon = new Label();
                @Override
                protected void updateItem(Boolean hasAttachment, boolean empty) {
                    super.updateItem(hasAttachment, empty);
                    setText(null);
                    attachmentIcon.getStyleClass().add("attachment-button");
                    attachmentIcon.setPrefSize(24, 24);
                    attachmentIcon.setTooltip(new Tooltip("Attachment Available"));
                    if(empty || !hasAttachment){
                        setGraphic(null);
                    }else{
                        setGraphic(attachmentIcon);
                    }
                }
            };
        });
        updatesTable.getSelectionModel().selectedItemProperty()
                .addListener((ObservableValue<? extends ProjectUpdate> observable, 
                        ProjectUpdate oldValue, ProjectUpdate newValue) -> {
            editBtn.setDisable(newValue == null);
            deleteBtn.setDisable(newValue == null);
            viewBtn.setDisable(newValue == null);
        });
    }
    
    private void showUpdateForm(){
        overlay.setOpacity(0.0);
        updateForm.contentView.setScaleX(0.0);
        updateForm.contentView.setScaleY(0.0);
        updateForm.contentView.setOpacity(0.0);
        
        parentScreen.container.getChildren().add(overlay);
        parentScreen.container.getChildren().add(updateForm.contentView);
        FadeTransition fadeIn = new FadeTransition(ANIM_DURATION_SHORT, overlay);
        fadeIn.setToValue(1.0);
        fadeIn.setInterpolator(Interpolator.EASE_IN);
        fadeIn.setOnFinished(evt -> {
            ScaleTransition scaleIn = new ScaleTransition(ANIM_DURATION_LONG, updateForm.contentView);
            scaleIn.setInterpolator(IN_INTERPOLATOR);
            scaleIn.setToX(1);
            scaleIn.setToY(1);
            
            FadeTransition fade = new FadeTransition(ANIM_DURATION_SHORT, updateForm.contentView);
            fade.setToValue(1.0);
            fade.setInterpolator(IN_INTERPOLATOR);
            
            ParallelTransition trans = new ParallelTransition();
            trans.getChildren().addAll(scaleIn, fade);
            trans.play();
            
            // inform the MainScreenActivity, set pending operation state to true
            ((MainActivityScreen)parentScreen.getScreenController()).setPendingOperationState(true);
        });
        fadeIn.play();
    }
    
    private void hideUpdateForm(){
        ScaleTransition scaleOut = new ScaleTransition(ANIM_DURATION_LONG, updateForm.contentView);
        scaleOut.setToX(0.0);
        scaleOut.setToY(0.0);
        scaleOut.setInterpolator(OUT_INTERPOLATOR);
        
        scaleOut.setOnFinished(evt -> {
            FadeTransition fadeOut = new FadeTransition(ANIM_DURATION_SHORT, overlay);
            fadeOut.setToValue(0.0);
            fadeOut.setInterpolator(OUT_INTERPOLATOR);
            fadeOut.setOnFinished(e -> {
                parentScreen.container.getChildren().remove(overlay);
                parentScreen.container.getChildren().remove(updateForm.contentView);
                
                // inform the MainScreenActivity, set pending operation state to false
                ((MainActivityScreen)parentScreen.getScreenController()).setPendingOperationState(false);
            });
            fadeOut.play();
        });
        scaleOut.play();
    }
    
    @FXML
    public void onAddAction(ActionEvent event){
        showUpdateForm();
    }
    
    @FXML
    public void onEditAction(ActionEvent event){
        
    }
    
    @FXML
    public void onDeleteAction(ActionEvent event){
        ProjectUpdate pu = updatesTable.getSelectionModel().getSelectedItem();
        if(pu != null){
            Alert alert = new Alert(Alert.AlertType.WARNING, "Are you sure you want to delete this update? All attachments"
                    + " related to this update will be removed.", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> result = alert.showAndWait();
            result.ifPresent((ButtonType t) -> {
                if(t.equals(ButtonType.YES)){
                    boolean deleted = DBManager.deleteProjectUpdate(pu);
                    if(deleted){
                        updatesTable.getItems().remove(pu);
                        
                        // record history
                        History hist = new History();
                        hist.setDate(LocalDateTime.now());
                        hist.setNotes("Deleted update for project \"" + project.getNameProperty().get() + "\": " + pu.getNotes().get());
                        DBManager.addHistory(hist);
                        ((MainActivityScreen)parentScreen.getScreenController()).getHistories().add(hist);
                    }
                }
            });
        }
    }
    
    @FXML
    public void onViewAction(ActionEvent event){
        
    }

    @Override
    public void onResume() {
        super.onResume();
//        updates = FXCollections.observableArrayList(DBManager.getProjectUpdates(project.getIdProperty().get()));
//        updatesTable.setItems(updates);
        Task loadUpdatesTask = new Task(){
            @Override
            protected Object call() throws Exception {
                updates = FXCollections.observableArrayList(DBManager.getProjectUpdates(project.getIdProperty().get()));
                return null;
            }

            @Override
            protected void running() {
                super.running();
                loadingDataProperty.set(true);
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                filteredList = new FilteredList<>(updates);
                SortedList<ProjectUpdate> sortedList = new SortedList<>(filteredList); // remove if using a list instead of a table
                updatesTable.setItems(sortedList);
                loadingDataProperty.set(false);
            }

            @Override
            protected void failed() {
                super.failed();
                Alert alert = new Alert(AlertType.ERROR, "Failed to load project updates.");
                alert.setTitle("Error");
                alert.showAndWait();
            }
        };
        Thread thread = new Thread(loadUpdatesTask);
        thread.setDaemon(true);
        thread.start();
    }

    /***************************************************************************
     *                                                                         *
     *                              UpdateForm class                           *
     *                                                                         *
     ***************************************************************************/
    
    private class UpdateForm {
        
        final Parent contentView;
        @FXML ComboBox<String> level;
        @FXML ComboBox<String> status;
        @FXML TextArea textArea;
        @FXML HBox attachmentBox;
        @FXML Label emptyLabel;
        private ProjectUpdate update;
        private ObservableList<Attachment> attachments;
        
        public UpdateForm(){
            contentView = ScreenLoader.loadScreen(UpdateForm.this, "project_update_form.fxml");
            level.setItems(FXCollections.observableArrayList(
                    ProjectUpdate.LEVEL_LOW, ProjectUpdate.LEVEL_NORMAL, ProjectUpdate.LEVEL_HIGH
            ));
            level.getSelectionModel().select(0);
            status.setItems(FXCollections.observableArrayList(
                    Project.NOT_DEFINED, Project.ON_GOING, Project.POSTPONED, Project.TERMINATED, Project.FINISHED
            ));
            status.getSelectionModel().select(0);
        }
        
        @FXML
        public void onCancelAction(ActionEvent event){
            hideUpdateForm();
            clearFields();
        }
        
        @FXML
        public void onSaveAction(ActionEvent event){
            if(textArea.getText().isEmpty()){
                hideUpdateForm();
            }else{
                update = new ProjectUpdate();
                update.setProjectId(parentScreen.getProject().getIdProperty().get());
                update.setDateCreated(LocalDateTime.now());
                update.setNotes(textArea.getText());
                update.setLevel(level.getSelectionModel().getSelectedItem());
                update.setStatus(status.getSelectionModel().getSelectedItem());
                update.setHasAttachment(attachments != null && !attachments.isEmpty());
                
                // save update to database
                boolean added = DBManager.addProjectUpdate(update);
                if(added){
                    // add to local list
                    updates.add(update);
                    
                    // add attachments to database
                    int id = DBManager.getIdOf(update);
                    if(id > -1){
                        boolean addedAttachments = DBManager.addAttachments(id, attachments);
                        if(addedAttachments){
                            // add attachments to create update
                            update.setAttachments(attachments);
                            update.setId(id);
                        }
                    }
                    
                    // record history
                    History hist = new History();
                    hist.setDate(LocalDateTime.now());
                    hist.setNotes("Updated project \"" + project.getNameProperty().get() + "\": " + update.getNotes().get());
                    DBManager.addHistory(hist);
                    ((MainActivityScreen)parentScreen.getScreenController()).getHistories().add(hist);
                }
            }
            hideUpdateForm();
            clearFields();
        }
        
        @FXML
        public void onAddPhotoAction(ActionEvent event){
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Image File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image File", "*.jpg", "*.jpeg", "*.png", "*.bmp", "*.gif"));
            File file = fileChooser.showOpenDialog(mContentView.getScene().getWindow());
            if(file != null){
                try {
                    if(attachments == null){
                        attachments = FXCollections.observableArrayList();
                    }
                    Attachment at = new Attachment();
                    at.setPath(file.getPath());
                    at.setType(Attachment.TYPE_IMAGE);
                    attachments.add(at);
                    
                    // create attachment view
                    AttachmentNode an = new AttachmentNode();
                    an.iv.setImage(new Image(new FileInputStream(file)));
                    an.fileName.setText(file.getName());
                    
                    // hide empty label
                    if(emptyLabel.isVisible()){
                        emptyLabel.setVisible(false);
                    }
                    attachmentBox.getChildren().add(an);
                } catch (FileNotFoundException ex) {
                    System.err.println(ex);
                }
            }
        }
        
        @FXML
        public void onAddVideoAction(ActionEvent event){
            
        }
        
        @FXML
        public void onAddAttachmentAction(ActionEvent event){
            
        }
        
        private void clearFields(){
            textArea.clear();
            attachmentBox.getChildren().clear();
            emptyLabel.setVisible(true);
            level.getSelectionModel().select(0);
            status.getSelectionModel().select(0);
            update = null;
            attachments = null;
        }        
    }
    
    /***************************************************************************
     *                                                                         *
     *                          AttachmentNode class                           *
     *                                                                         *
     ***************************************************************************/
    
    private class AttachmentNode extends VBox {
        final ImageView iv;
        final Label fileName;

        public AttachmentNode(){
            setPrefSize(100, 124);
            setMaxSize(100, 124);
            setAlignment(Pos.CENTER);
            iv = new ImageView();
            iv.setFitWidth(100);
            iv.setFitHeight(100);
            fileName = new Label();
            fileName.setPrefHeight(24);
            fileName.getStyleClass().add("label-details-sub");
            getChildren().addAll(iv, fileName);
        }
    }
}
