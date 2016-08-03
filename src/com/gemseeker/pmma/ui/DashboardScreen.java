package com.gemseeker.pmma.ui;

import com.gemseeker.pmma.ControlledScreen;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author RAFIS-FRED
 */
public class DashboardScreen extends ControlledScreen {

    private VBox contentView;
    private TableView summaryTable;
    private AnchorPane summaryChartPane;
    
    private SummaryNode onGoing = SummaryNode.ON_GOING;
    private SummaryNode postponed = SummaryNode.POSTPONED;
    private SummaryNode terminated = SummaryNode.TERMINATED;
    private SummaryNode finished = SummaryNode.FINISHED;
    
    public DashboardScreen(){
        HBox summaryGrid = new HBox();
        summaryGrid.setPrefHeight(84);
        // TODO: set proper value of summary nodes
        onGoing.setValue(0);
        postponed.setValue(0);
        terminated.setValue(0);
        finished.setValue(0);
        summaryGrid.getChildren().addAll(onGoing, postponed, terminated, finished);
        
        HBox hbox = new HBox();
        hbox.setMinHeight(48);
        hbox.setSpacing(8);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setPadding(new Insets(0, 0, 0, 16));
        hbox.setStyle("-fx-background-color: white;");
        
        Hyperlink link = new Hyperlink("View All Projects");
        link.setOnAction(evt->{
            ((MainActivityScreen)getScreenController()).toggleProjects.setSelected(true);
        });
        hbox.getChildren().addAll(new Label("Recent Updates"), link);
        
        summaryTable = new TableView();
        summaryTable.getStyleClass().add("summary-table");
        summaryTable.setPrefHeight(230);
        
        HBox hbox2 = new HBox();
        hbox2.setMinHeight(48);
        hbox2.setSpacing(8);
        hbox2.setAlignment(Pos.CENTER_LEFT);
        hbox2.setPadding(new Insets(0, 0, 0, 16));
        hbox2.setStyle("-fx-background-color: white;");
        hbox2.getChildren().addAll(new Label("Overall Status"));
        
        summaryChartPane = new AnchorPane();
        summaryChartPane.setPrefHeight(158);
        
        contentView = new VBox();
        contentView.getChildren().addAll(summaryGrid, hbox, summaryTable, hbox2, summaryChartPane);
    }
    
    @Override
    public Parent getContentView() {
        return contentView;
    }

    private static class SummaryNode extends VBox {
        
        static SummaryNode ON_GOING = 
                new SummaryNode(Color.web("#1de9b6"), Color.web("#eceff1"), "On Going");
        static SummaryNode POSTPONED = 
                new SummaryNode(Color.web("#7c4dff"), Color.web("#f8fafb"), "Postponed");
        static SummaryNode TERMINATED = 
                new SummaryNode(Color.web("#ec407a"), Color.web("#eceff1"), "Terminated");
        static SummaryNode FINISHED =
                new SummaryNode(Color.web("#4dd0e1"), Color.web("#f8fafb"), "Finished");
        
        Rectangle bar;
        Label countLabel;
        Label statLabel;
        
        public SummaryNode(Color color, Color bg, String text) {
            bar = new Rectangle();
            bar.setHeight(8);
            bar.widthProperty().bind(widthProperty());
            bar.setFill(color);
            countLabel = new Label();
            countLabel.getStyleClass().add("count-label");
            statLabel = new Label(text);
            statLabel.getStyleClass().add("stat-label");
            statLabel.setTranslateY(-8);
            setBackground(new Background(new BackgroundFill(bg, new CornerRadii(0), new Insets(0))));
            setAlignment(Pos.TOP_CENTER);
            setPadding(new Insets(0, 8, 8, 8));
            getChildren().addAll(bar, countLabel, statLabel);
            HBox.setHgrow(SummaryNode.this, Priority.ALWAYS);
        }
        
        void setValue(int value){
            countLabel.setText(String.valueOf(value));
        }
    }
}
