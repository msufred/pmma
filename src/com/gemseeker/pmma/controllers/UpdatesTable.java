/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemseeker.pmma.controllers;

import com.gemseeker.pmma.ui.*;
import com.gemseeker.pmma.data.History;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import javax.swing.text.DateFormatter;

/**
 *
 * @author u
 */
public class UpdatesTable extends TableView<History> {

    public UpdatesTable(){
        TableColumn<History, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory((TableColumn.CellDataFeatures<History, String> param) -> {
            DateTimeFormatterBuilder dfb = new DateTimeFormatterBuilder();
            dfb.appendPattern("MMM dd, yyyy");
            DateTimeFormatter df = dfb.toFormatter();
            return new SimpleStringProperty(df.format(param.getValue().getDateCreated()));
        });
        
        TableColumn<History, String> notesCol = new TableColumn<>("Notes");
        dateCol.setCellValueFactory((TableColumn.CellDataFeatures<History, String> param) -> {
            return new SimpleStringProperty(param.getValue().getNotes());
        });
        
        getColumns().addAll(dateCol, notesCol);
        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

}
