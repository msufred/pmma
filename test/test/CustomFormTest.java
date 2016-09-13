package test;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author Gem Seeker
 */
public class CustomFormTest extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        VBox root = new VBox();
        root.setPadding(new Insets(16));
        root.setSpacing(8);
        
        FieldGroup fg = new FieldGroup(root);
        fg.setDefault(true);
        root.getChildren().add(fg);
        
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }
    
    private class FieldGroup extends HBox {
        
        final VBox parent;
        private ObservableList<Node> children;
        private TextField field;
        private Button add, remove;
        
        public FieldGroup(VBox parent){
            this.parent = parent;
            children = this.parent.getChildren();
            init();
        }
        
        private void init(){
            setSpacing(4);
            field = new TextField();
            field.setPromptText("Email");
            HBox.setHgrow(field, Priority.ALWAYS);
            add = new Button("+");
            remove = new Button("x");
            getChildren().addAll(field, add, remove);
            
            add.setOnAction(evt -> {
                if(parent != null){
                    FieldGroup fg = new FieldGroup(parent);
                    children.add(fg);
                    add.setDisable(true);
                }
            });
            
            remove.setOnAction(evt -> {
                children.remove(this);
                ((FieldGroup)children.get(children.size() - 1)).add.setDisable(false);
            });
        }
        
        public void setDefault(boolean isDefault){
            if(isDefault){
                remove.setDisable(true);
            }
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
