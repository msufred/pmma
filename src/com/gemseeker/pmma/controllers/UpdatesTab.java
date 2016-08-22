package com.gemseeker.pmma.controllers;

import com.gemseeker.pmma.animations.EasingMode;
import com.gemseeker.pmma.animations.ExponentialInterpolator;
import com.gemseeker.pmma.data.Project;
import com.gemseeker.pmma.fxml.ScreenLoader;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/**
 *
 * @author Gem Seeker
 */
public class UpdatesTab extends ViewProjectScreen.TabScreen {
    
    public static final String NAME = "UpdatesTab";
    
    @FXML TableColumn levelCol;
    @FXML TableColumn dateCol;
    @FXML TableColumn remarksCol;
    @FXML TableColumn attachmentCol;
    @FXML TableColumn statusCol;
    @FXML ComboBox filter;
    
    private final UpdateForm updateForm;
    private final Pane overlay;
    
    public UpdatesTab(){
        super(NAME);
        initComponents();
        updateForm = new UpdateForm();
        overlay = new Pane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
    }

    private void initComponents(){
        mContentView = ScreenLoader.loadScreen(this, "project_updates_tabview.fxml");
    }
    
    private void showUpdateForm(){
        overlay.setOpacity(0.0);
        updateForm.contentView.setScaleX(0.0);
        updateForm.contentView.setScaleY(0.0);
        parentScreen.container.getChildren().add(overlay);
        parentScreen.container.getChildren().add(updateForm.contentView);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), overlay);
        fadeIn.setToValue(1.0);
        fadeIn.setInterpolator(Interpolator.EASE_IN);
        fadeIn.setOnFinished(evt -> {
            ScaleTransition scaleIn = new ScaleTransition(Duration.millis(400), updateForm.contentView);
            scaleIn.setInterpolator(new ExponentialInterpolator(EasingMode.EASE_OUT));
            scaleIn.setByX(1.0);
            scaleIn.setByY(1.0);
            scaleIn.play();
            // inform the MainScreenActivity, set pending operation state to true
            ((MainActivityScreen)parentScreen.getScreenController()).setPendingOperationState(true);
        });
        fadeIn.play();
    }
    
    private void hideUpdateForm(){
        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(400), updateForm.contentView);
        scaleOut.setToX(0.0);
        scaleOut.setToY(0.0);
        scaleOut.setInterpolator(new ExponentialInterpolator(EasingMode.EASE_IN));
        scaleOut.setOnFinished(evt -> {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(200), overlay);
            fadeOut.setToValue(0.0);
            fadeOut.setInterpolator(Interpolator.EASE_OUT);
            fadeOut.setOnFinished(e -> {
                parentScreen.container.getChildren().remove(overlay);
                parentScreen.container.getChildren().remove(updateForm.contentView);
                
                // inform the MainScreenActivity, set pending operation state to false
                ((MainActivityScreen)parentScreen.getScreenController()).setPendingOperationState(true);
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
    public void onViewAction(ActionEvent event){
        
    }
    
    @Override
    public void onStart() {
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onFinish() {
    }

    private class UpdateForm {
        
        final Parent contentView;
        @FXML ComboBox level;
        @FXML ComboBox status;
        
        public UpdateForm(){
            contentView = ScreenLoader.loadScreen(UpdateForm.this, "project_update_form.fxml");
            level.setItems(FXCollections.observableArrayList("Low", "Medium", "High"));
            status.setItems(FXCollections.observableArrayList(
                    Project.ON_GOING, Project.POSTPONED, Project.TERMINATED, Project.FINISHED
            ));
        }
        
        @FXML
        public void onCancelAction(ActionEvent event){
            hideUpdateForm();
        }
        
        @FXML
        public void onSaveAction(ActionEvent event){
            
        }
        
        @FXML
        public void onAddPhotoAction(ActionEvent event){
            
        }
        
        @FXML
        public void onAddVideoAction(ActionEvent event){
            
        }
        
        @FXML
        public void onAddAttachmentAction(ActionEvent event){
            
        }
    }
}
