package com.gemseeker.pmma.ui;

import com.gemseeker.pmma.ControlledScreen;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
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
    
    public ProjectsScreen(){
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
        
        addBtn = new IconButton();
        addBtn.setIcon(getClass().getResourceAsStream("add.svg"));
        addBtn.getStyleClass().add("icon-button");
        
        editBtn = new IconButton();
        editBtn.setIcon(getClass().getResourceAsStream("edit.svg"));
        editBtn.getStyleClass().add("icon-button");
        editBtn.setDisable(true);
        
        deleteBtn = new IconButton();
        deleteBtn.setIcon(getClass().getResourceAsStream("delete.svg"));
        deleteBtn.getStyleClass().add("icon-button");
        deleteBtn.setDisable(true);
        
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
    }

    @Override
    public Parent getContentView() {
        return contentView;
    }
    
}
