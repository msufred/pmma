package com.gemseeker.pmma.controllers;

import com.gemseeker.pmma.ControlledScreen;
import com.gemseeker.pmma.animations.EasingMode;
import com.gemseeker.pmma.animations.ExponentialInterpolator;
import com.gemseeker.pmma.data.Contact;
import com.gemseeker.pmma.fxml.ScreenLoader;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 *
 * @author Gem Seeker
 */
public class ContactsScreen extends ControlledScreen {

    public static final String NAME = "ContactsScreen";
    
    private static final Duration ANIM_DURATION = new Duration(400);
    private static final ExponentialInterpolator IN_INTERPOLATOR = new ExponentialInterpolator(EasingMode.EASE_OUT);
    private static final ExponentialInterpolator OUT_INTERPOLATOR = new ExponentialInterpolator(EasingMode.EASE_IN);
    
    @FXML StackPane container;
    @FXML TextField searchField;
    @FXML Button addBtn;
    @FXML Button deleteBtn;
    @FXML ListView<Contact> contactsList;
    
    private ObservableList<Contact> contacts;
    private FilteredList<Contact> filteredList;
    private final ContactForm contactForm;
    
    private boolean contactFormOpen = false;
    
    public ContactsScreen(){
        super(NAME);
        setContentView(ScreenLoader.loadScreen(ContactsScreen.this, "contacts.fxml"));
        initComponents();
        contactForm = new ContactForm();
    }
    
    private void initComponents(){
        contactsList.setCellFactory((ListView<Contact> listCell) -> new ContactsListCell());
        searchField.textProperty().addListener((ObservableValue<? extends String> ov, String old, String newValue) -> {
            filteredList.setPredicate(contact -> {
                return contact.getName().toLowerCase().contains(newValue.toLowerCase());
            });
        });
    }
    
    private void hideContactForm(){
        Node content = contactForm.contentView;
        TranslateTransition translate = new TranslateTransition(ANIM_DURATION, content);
        translate.setToY(container.getHeight() +  4); // 4 is offset
        translate.setInterpolator(OUT_INTERPOLATOR);
        translate.setOnFinished(evt -> {
            container.getChildren().remove(contactForm.contentView);
            contactFormOpen = false;
        });
        translate.play();
    }
    
    private void showContactForm(){
        Node content = contactForm.contentView;
        content.setTranslateY(container.getHeight() + 4); // 4 is offset
        container.getChildren().add(content);
        
        TranslateTransition translate = new TranslateTransition(ANIM_DURATION, content);
        translate.setToY(0.0); // 4 is offset
        translate.setInterpolator(IN_INTERPOLATOR);
        translate.setOnFinished(evt -> contactFormOpen = true);
        translate.play();
    }
    
    @FXML
    public void onAddAction(ActionEvent event){
        if(!contactFormOpen){
            showContactForm();
        }
    }
    
    @FXML
    public void onDeleteAction(ActionEvent event){
        
    }
    
    @Override
    public void onStart() {
        
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onResume() {
        if(contacts == null){
            contacts = ((MainActivityScreen) screenController).getContacts();
            filteredList = new FilteredList<>(contacts, contact -> true);
            contactsList.setItems(filteredList);
        }
    }

    @Override
    public void onFinish() {
    }
    
    private class ContactsListCell extends ListCell<Contact> {
        
        @FXML Label nameLabel;
        @FXML Label addressLabel;
        final Parent contentView;
        
        public ContactsListCell(){
            contentView = ScreenLoader.loadScreen(ContactsListCell.this, "contact_listcell.fxml");
        }

        @Override
        protected void updateItem(Contact item, boolean empty) {
            super.updateItem(item, empty);
            if(empty){
                setGraphic(null);
                setText(null);
            }else{
                nameLabel.setText(item.getName());
                addressLabel.setText(item.getCompany());
                setGraphic(contentView);
            }
        }

        @Override
        public void updateSelected(boolean selected) {
            super.updateSelected(selected);
            if(selected){
                contentView.getStyleClass().add("list-cell-selected");
            }else{
                contentView.getStyleClass().remove("list-cell-selected");
            }
        }
    }
    
    private class ContactForm {
        
        @FXML VBox formContainer;
        @FXML ScrollPane scrollPane;
        @FXML TextField nameField;
        @FXML TextField companyField;
        @FXML TextField addressField;
        @FXML TextField numberField;
        @FXML TextField emailField;
        @FXML Button saveBtn;
        @FXML Button cancelBtn;
        @FXML Button addNumBtn;
        @FXML Button addEmailBtn;
        @FXML ListView numbersList;
        @FXML ListView emailsList;
        
        final Parent contentView;
        private ObservableList<String> numbers;
        private ObservableList<String> emails;
        
        public ContactForm(){
            contentView = ScreenLoader.loadScreen(ContactForm.this, "contact_form.fxml");
            init();
        }

        private void init(){
            numberField.addEventFilter(KeyEvent.KEY_TYPED, evt -> {
                String allowedCharacters = "+0123456789";
                if(!allowedCharacters.contains(evt.getCharacter())){
                    evt.consume();
                }
            });
            numberField.textProperty().addListener((ObservableValue<? extends String> ov, String old, String newValue) -> {
                addNumBtn.setDisable(newValue.isEmpty());
            });
            emailField.textProperty().addListener((ObservableValue<? extends String> ov, String old, String newValue) -> {
                String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
                addEmailBtn.setDisable(!newValue.matches(regex));
            });
            cancelBtn.setOnAction(evt -> {
                hideContactForm();
            });
        }
        
        @FXML
        public void addNumberAction(ActionEvent event){
            if(numbers != null){
                numbers.add(numberField.getText());
                numberField.clear();
            }
        }
    }
    
}
