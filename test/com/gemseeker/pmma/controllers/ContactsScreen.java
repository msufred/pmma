package com.gemseeker.pmma.controllers;

import com.gemseeker.pmma.ControlledScreen;
import com.gemseeker.pmma.animations.interpolators.EasingMode;
import com.gemseeker.pmma.animations.interpolators.QuarticInterpolator;
import com.gemseeker.pmma.data.Contact;
import com.gemseeker.pmma.data.DBManager;
import com.gemseeker.pmma.data.Email;
import com.gemseeker.pmma.data.History;
import com.gemseeker.pmma.data.PhoneNumber;
import com.gemseeker.pmma.fxml.ScreenLoader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

/**
 *
 * @author Gem Seeker
 */
public class ContactsScreen extends ControlledScreen {

    public static final String NAME = "ContactsScreen";
    
    private static final int ADD_MODE = 0;
    private static final int EDIT_MODE = 1;
    
    private static final Duration ANIM_DURATION_LONG = new Duration(400);
    private static final Duration ANIM_DURATION_SHORT = new Duration(200);
    private static final Interpolator IN_INTERPOLATOR = new QuarticInterpolator(EasingMode.EASE_OUT);
    private static final Interpolator OUT_INTERPOLATOR = new QuarticInterpolator(EasingMode.EASE_IN);
    
    @FXML StackPane container;
    @FXML ScrollPane scrollPane;
    @FXML TextField searchField;
    @FXML Button addBtn;
    @FXML Button editBtn;
    @FXML Button deleteBtn;
    @FXML ListView<Contact> contactsList;
    
    private ChatForm chatForm;
    private ContactForm contactForm;
    private Pane overlay;
    private ObservableList<Contact> contacts;
    private FilteredList<Contact> filteredList;
    private int mode;

    public ContactsScreen(){
        super(NAME);
        setContentView(ScreenLoader.loadScreen(ContactsScreen.this, "contacts.fxml"));
        initComponents();
    }
    
    private void initComponents(){
        contactsList.setCellFactory((ListView<Contact> listCell) -> new ContactsListCell());
        contactsList.getSelectionModel().selectedItemProperty()
                .addListener((ObservableValue<? extends Contact> ov, Contact old, Contact newValue) -> {
            if(newValue != null){
                if(chatForm == null){
                    chatForm = new ChatForm();
                }
                if(container.getChildren().isEmpty()){
                    showChat();
                }
                chatForm.setContact(newValue);
            }else{
                if(chatForm != null && container.getChildren().contains(chatForm.getContentView())){
                    hideChat();
                }
            }
            editBtn.setDisable(newValue == null);
            deleteBtn.setDisable(newValue == null);
        });
        searchField.textProperty().addListener((ObservableValue<? extends String> ov, String old, String newValue) -> {
            filteredList.setPredicate(contact -> {
                return contact.getNameProperty().get().toLowerCase().contains(newValue.toLowerCase());
            });
        });
        
        contactForm = new ContactForm();
        overlay = new Pane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5)");
        overlay.setOnMouseClicked(evt -> {
            hideContactForm();
        });
    }
    
    private void showContactForm(int mode){
        contactForm.setMode(mode);
        this.mode = mode;
        
        Node content = contactForm.contentView;
        content.setTranslateY(((StackPane)contentView).getHeight() + 8);
        overlay.setOpacity(0);
        ((StackPane)contentView).getChildren().add(overlay);
        ((StackPane)contentView).getChildren().add(content);
        
        FadeTransition fadeIn = new FadeTransition(ANIM_DURATION_SHORT, overlay);
        fadeIn.setToValue(1);
        fadeIn.setInterpolator(IN_INTERPOLATOR);
        fadeIn.setOnFinished(evt -> {
            TranslateTransition translate = new TranslateTransition(ANIM_DURATION_LONG, content);
            translate.setToY(0);
            translate.setInterpolator(IN_INTERPOLATOR);
            translate.setOnFinished(e -> {
                ((MainActivityScreen)screenController).setPendingOperationState(true);
                if(this.mode == EDIT_MODE){
                    contactForm.editContact();
                }
            });
            translate.play();
        });
        fadeIn.play();
    }
    
    private void hideContactForm(){
        TranslateTransition translate = new TranslateTransition(ANIM_DURATION_LONG, contactForm.contentView);
        translate.setToY(((StackPane)contentView).getHeight() + 8);
        translate.setInterpolator(OUT_INTERPOLATOR);
        translate.setOnFinished(evt -> {
            FadeTransition fadeOut = new FadeTransition(ANIM_DURATION_SHORT, overlay);
            fadeOut.setToValue(0);
            fadeOut.setInterpolator(OUT_INTERPOLATOR);
            fadeOut.setOnFinished(e -> {
                ((StackPane)contentView).getChildren().removeAll(contactForm.contentView, overlay);
                ((MainActivityScreen)screenController).setPendingOperationState(false);
            });
            fadeOut.play();
        });
        translate.play();
    }
    
    private void showChat(){
        Node content = chatForm.getContentView();
        content.setTranslateY(container.getHeight() + 8);
        container.getChildren().add(content);
        TranslateTransition translate = new TranslateTransition(ANIM_DURATION_LONG, content);
        translate.setToY(0);
        translate.setInterpolator(IN_INTERPOLATOR);
        translate.play();
    }
    
    private void hideChat(){
        TranslateTransition translate = new TranslateTransition(ANIM_DURATION_LONG, chatForm.getContentView());
        translate.setToY(container.getHeight() + 8);
        translate.setInterpolator(OUT_INTERPOLATOR);
        translate.setOnFinished(evt -> {
            container.getChildren().remove(chatForm.getContentView());
        });
        translate.play();
    }
    
    public ObservableList<Contact> getContacts(){
        return contacts;
    }

    @FXML
    public void onAddAction(ActionEvent event){
        showContactForm(ADD_MODE);
    }
    
    @FXML
    public void onEditAction(ActionEvent event){
        
    }
    
    @FXML
    public void onDeleteAction(ActionEvent event){
        Contact contact = contactsList.getSelectionModel().getSelectedItem();
        if(contact != null){
            Alert alert = new Alert(AlertType.INFORMATION, 
                    "You are about to delete the contact \"" 
                        + contact.getNameProperty().get()
                        + "\". Do you want to continue?",
                    ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> result = alert.showAndWait();
            result.ifPresent(btn -> {
                if(btn.equals(ButtonType.YES)){
                    boolean deleted = DBManager.deleteContact(contact);
                    if(deleted){
                        contacts.remove(contact);
                        
                        // add to history
                        History history = new History();
                        history.setDate(LocalDateTime.now());
                        history.setNotes("Deleted contact \"" + contact.getNameProperty().get() + "\"");
                        DBManager.addHistory(history);
                        ((MainActivityScreen)screenController).getHistories().add(history);
                    }
                }
            });
        }
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
    
    /**************************************************************************
     *                                                                        *
     * ContactsListCell class -- A custom ListCell that is displayed on       *
     * Contacts ListView.                                                     *
     *                                                                        *
     **************************************************************************/
    
    private class ContactsListCell extends ListCell<Contact> {

        @FXML ImageView imageView;
        @FXML Label nameLabel;
        @FXML Label addressLabel;
        final Parent contentView;
        
        public ContactsListCell(){
            contentView = ScreenLoader.loadScreen(ContactsListCell.this, "contact_listcell.fxml");
            Circle circle = new Circle(24, 24, 24);
            imageView.setClip(circle);
        }

        @Override
        protected void updateItem(Contact item, boolean empty) {
            super.updateItem(item, empty);
            if(empty){
                setGraphic(null);
                setText(null);
            }else{
                nameLabel.setText(item.getNameProperty().get());
                Optional.of(item.getImagePathProperty()).ifPresent(path -> {
                    if(path.get() != null){
                        File file = new File(path.get());
                        if(file.exists()){
                            try{
                                Image image = new Image(new FileInputStream(file));
                                imageView.setImage(image);
                            }catch(FileNotFoundException e){
                                System.err.println(e);
                            }
                        }
                    }
                });
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
    
    /**************************************************************************
     *                                                                        *
     *                          ContactForm class                             *
     *                                                                        *
     **************************************************************************/
    
    private class ContactForm {
        @FXML StackPane formStackPane;
        @FXML VBox formContainer;
        @FXML ScrollPane scrollPane;
        @FXML TextField nameField;
        @FXML TextField companyField;
        @FXML TextField addressField;
        @FXML TextField numberField;
        @FXML ComboBox<String> numberType;
        @FXML TextField emailField;
        @FXML ComboBox<String> emailType;
        @FXML Button saveBtn;
        @FXML Button cancelBtn;
        @FXML Button addNumBtn;
        @FXML Button addEmailBtn;
        @FXML VBox phoneBox;
        @FXML VBox emailBox;
        private ProgressIndicator indicator;
        private Pane overlay;
        
        final Parent contentView;
        ObservableList<PhoneNumber> numbers;
        ObservableList<Email> emails;
        Task saveTask;
        Contact mContact = null;
        
        ContactForm(){
            contentView = ScreenLoader.loadScreen(ContactForm.this, "contact_form.fxml");
            init();
        }

        private void init(){
            numberField.addEventFilter(KeyEvent.KEY_TYPED, evt -> {
                String allowedCharacters = "+0123456789 ";
                if(!allowedCharacters.contains(evt.getCharacter())){
                    evt.consume();
                }
            });
            numberField.textProperty().addListener((ObservableValue<? extends String> ov, String old, String newValue) -> {
                addNumBtn.setDisable(newValue.isEmpty());
            });
            numberType.setItems(FXCollections.observableArrayList("Home", "Mobile", "Work"));
            numberType.getSelectionModel().select(0);
            emailField.textProperty().addListener((ObservableValue<? extends String> ov, String old, String newValue) -> {
                String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
                addEmailBtn.setDisable(!newValue.matches(regex));
            });
            emailType.setItems(FXCollections.observableArrayList("Personal", "Office"));
            emailType.getSelectionModel().select(0);
            cancelBtn.setOnAction(evt -> {
                hideContactForm();
            });
            
            numbers = FXCollections.observableArrayList();
            emails = FXCollections.observableArrayList();
            
            indicator = new ProgressIndicator(ProgressIndicator.INDETERMINATE_PROGRESS);
            indicator.setMaxSize(64, 64);
            
            overlay = new Pane();
            overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3);");
            
            // create Task for saving Contact, saving is done on a background
            // thread
            saveTask = new Task(){
                final Contact contact = new Contact();
                @Override
                protected Object call() throws Exception {
                    int idNum = 0;
                    while(true){
                        String tempId = "contact_" + idNum;
                        if(!contacts.stream().anyMatch(c -> c.getContactIdProperty().get().equals(tempId))){
                            contact.setContactId(tempId);
                            // set user id for each number and email
                            numbers.stream().forEach((PhoneNumber num) -> num.setUserId(tempId));
                            emails.stream().forEach((Email email) -> email.setUserId(tempId));
                            break;
                        }
                        idNum++;
                    }
                    contact.setName(nameField.getText());
                    contact.setCompany(companyField.getText());
                    contact.setAddress(addressField.getText());

                    boolean contactSaved = DBManager.addContact(contact);
                    if(!contactSaved){
                        updateMessage("failed to save contact");
                        System.out.println("failed to save contact.");
                    }else{
                        boolean numbersAdded = DBManager.addPhoneNumbers(numbers);
                        boolean emailsAdded = DBManager.addEmails(emails);
                        if(numbersAdded && emailsAdded){
                            contact.setPhones(numbers);
                            contact.setEmails(emails);
                        }
                    }
                    return null;
                }

                @Override
                protected void running() {
                    super.running();
                    System.out.println("Running!");
                    formStackPane.getChildren().addAll(overlay, indicator);
                }

                @Override
                protected void succeeded() {
                    super.succeeded(); 
                    System.out.println("Succeeded!");
                    formStackPane.getChildren().removeAll(overlay, indicator);
                    contacts.add(contact);
                    hideContactForm();
                }

                @Override
                protected void failed() {
                    super.failed();
                    System.out.println("Failed!");
                    formStackPane.getChildren().removeAll(overlay, indicator);
                    hideContactForm();
                }
            };
        }
        
        private void clearFields(){
            nameField.clear();
            addressField.clear();
            companyField.clear();
            numberType.getSelectionModel().select(0);
            emailType.getSelectionModel().select(0);
        }
        
        public void setMode(int mode){
            if(mode == ADD_MODE){
                clearFields();
            }
        }
        
        public void setContact(Contact contact){
            mContact = contact;
        }
        
        /**
         * Fill the fields with the values of the Contact to edit if there's
         * any.
         */
        public void editContact(){
            if(mContact != null){
                nameField.setText(mContact.getNameProperty().get());
                addressField.setText(mContact.getAddressProperty().get());
                companyField.setText(mContact.getCompanyProperty().get());
                // TODO: load contact's numbers and emails
            }
        }
        
        @FXML
        public void addNumberAction(ActionEvent event){
            PhoneNumber number = new PhoneNumber();
            number.setPhoneNumber(numberField.getText());
            number.setType(numberType.getSelectionModel().getSelectedItem());
            
            PhoneNode node = new PhoneNode(this);
            node.bind(number);

            phoneBox.getChildren().add(node.contentView);
            numbers.add(number);
            numberField.clear();
        }
        
        @FXML
        public void addEmailAction(ActionEvent event){
            Email email = new Email();
            email.setEmailAddress(emailField.getText());
            email.setType(emailType.getSelectionModel().getSelectedItem());
            
            EmailNode node = new EmailNode(this);
            node.bind(email);
            
            emailBox.getChildren().add(node.contentView);
            emails.add(email);
            emailField.clear();
        }
        
        @FXML
        public void onSaveAction(ActionEvent event){
            if(nameField.getText().isEmpty() || companyField.getText().isEmpty() || addressField.getText().isEmpty()){
                Alert alert = new Alert(AlertType.INFORMATION, "Some fields are empty!");
                alert.setTitle("Failed to Save Contact");
                alert.showAndWait();
                return;
            }
            Thread thread = new Thread(saveTask);
            thread.setDaemon(true);
            thread.start();
        }
    }
    
    private class PhoneNode {
        final Parent contentView;
        final ContactForm form;
        @FXML Label typeLabel;
        @FXML Label valueLabel;
        @FXML Button deleteBtn;
        PhoneNumber phoneNumber;
        
        PhoneNode(ContactForm form){
            contentView = ScreenLoader.loadScreen(PhoneNode.this, "number_node.fxml");
            this.form = form;
            deleteBtn.setOnAction(evt -> {
                if(phoneNumber != null){
                    form.phoneBox.getChildren().remove(contentView);
                    form.numbers.remove(phoneNumber);
                }
            });
        }
        
        void bind(PhoneNumber number){
            typeLabel.textProperty().bind(number.getTypeProperty());
            valueLabel.textProperty().bind(number.getPhoneNumberProperty());
            phoneNumber = number;
        }
    }
    
    private class EmailNode {
        final Parent contentView;
        final ContactForm form;
        @FXML Label typeLabel;
        @FXML Label valueLabel;
        @FXML Button deleteBtn;
        Email email;
        
        EmailNode(ContactForm form){
            contentView = ScreenLoader.loadScreen(EmailNode.this, "email_node.fxml");
            this.form = form;
            deleteBtn.setOnAction(evt -> {
                if(email != null){
                    form.emailBox.getChildren().remove(contentView);
                    form.emails.remove(email);
                }
            });
        }
        
        void bind(Email email){
            typeLabel.textProperty().bind(email.getTypeProperty());
            valueLabel.textProperty().bind(email.getEmailAddressProperty());
            this.email = email;
        }
    }
}
