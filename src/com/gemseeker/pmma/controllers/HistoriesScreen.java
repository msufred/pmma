package com.gemseeker.pmma.controllers;

import com.gemseeker.pmma.ControlledScreen;
import com.gemseeker.pmma.data.DBManager;
import com.gemseeker.pmma.data.DBUtil;
import com.gemseeker.pmma.data.History;
import com.gemseeker.pmma.fxml.ScreenLoader;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Optional;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 *
 * @author Gem Seeker
 */
public class HistoriesScreen extends ControlledScreen {

    public static final String NAME = "HistoriesScreen";
    
    @FXML TableView<History> table;
    @FXML TableColumn<History, String> dateCol;
    @FXML TableColumn<History, String> timeCol;
    @FXML TableColumn<History, String> detailsCol;
    @FXML TextField searchField;
    
    private final DateTimeFormatter dateFormatter;
    private final DateTimeFormatter timeFormatter;

    private ObservableList<History> histories;
    private FilteredList<History> filteredList;
    
    public HistoriesScreen(){
        super(NAME);
        setContentView(ScreenLoader.loadScreen(HistoriesScreen.this, "histories.fxml"));
        
        DateTimeFormatterBuilder dateFormatBuilder = new DateTimeFormatterBuilder();
        dateFormatBuilder.appendPattern("MMM dd, yyyy");
        dateFormatter = dateFormatBuilder.toFormatter();
        
        DateTimeFormatterBuilder timeFormatBuilder = new DateTimeFormatterBuilder();
        timeFormatBuilder.appendPattern("hh:mm a");
        timeFormatter = timeFormatBuilder.toFormatter();
        
        searchField.textProperty().addListener((ObservableValue<? extends String> ov, String old, String newValue) -> {
            if(filteredList != null){
                filteredList.setPredicate(hist -> {
                    return hist.getNotes().get().toLowerCase().contains(newValue.toLowerCase()) 
                            || dateFormatter.format(hist.getDateCreated()).toLowerCase().contains(newValue.toLowerCase());
                });
            }
        });
        initComponents();
    }
    
    private void initComponents(){
        dateCol.setCellValueFactory(cellData -> new SimpleStringProperty(dateFormatter.format(cellData.getValue().getDateCreated())));
        timeCol.setCellValueFactory(cellData -> new SimpleStringProperty(timeFormatter.format(cellData.getValue().getDateCreated())));
        detailsCol.setCellValueFactory(cellData -> cellData.getValue().getNotes());
    }
    
    @Override
    public void onStart() {
        super.onStart();
        histories = FXCollections.observableArrayList();
        histories.addListener((ListChangeListener<History>) c -> {
            table.refresh();
        });
        filteredList = new FilteredList<>(histories, hist -> true);
        SortedList<History> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedList);
    }

    @Override
    public void onResume() {
        super.onResume();
        Thread t = new Thread(new Task() {
            @Override
            protected Object call() throws Exception {
                histories.setAll(DBManager.getHistories());
                return null;
            }
        });
        t.setDaemon(true);
        t.start();
    }

    @FXML
    public void onClearHistoryAction(ActionEvent event){
        if(table.getItems().isEmpty()){
            Alert dialog = new Alert(AlertType.INFORMATION, "No History to be cleared.");
            dialog.setTitle("Clear History Action");
            dialog.showAndWait();
        }else{
            Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to clear all history?", ButtonType.YES, ButtonType.NO);
            alert.setTitle("Confirm Action");
            Optional<ButtonType> result = alert.showAndWait();
            result.ifPresent(btn -> {
                if(btn.equals(ButtonType.YES)){
                    boolean cleared = DBManager.clearTable(DBUtil.Tables.HISTORIES);
                    if(cleared){
                        histories.clear();
                    }
                }
            });
        }
    }
}
