package com.none.chatapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.none.chatapp_commands.Message;
import com.none.chatapp_commands.MessagesListRequestCommand;
import com.none.chatapp_commands.SendMessageCommand;
import com.none.chatapp_commands.User;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


public class UsersController {

    @FXML
    private AnchorPane anchRoot;
    @FXML
    private SplitPane SplitP;
    @FXML
    public VBox usersViewBox;

    @FXML
    public VBox offlineUsersViewBox;

    @FXML
    public VBox messageViewBox;

    @FXML
    private TabPane StatusTab;

    @FXML
    public Label selectedUserName;

    @FXML
    private ImageView selectedUserImage;

    @FXML
    private ImageView sendImgbtn;

    @FXML
    public TextField messageTextField;

    @FXML
    private Node emojiButton;

    @FXML
    private ImageView AttachBtn;

    @FXML
    private HBox UserWindow;
    @FXML
    private Label CurrentUserName;
    @FXML
    private ImageView CurrentUserImg;

    public static int selected_user_id;
    public static int selected_conv_id;

    private BooleanProperty isChatSelected = new SimpleBooleanProperty(false);
    // For dragging the window
    private double xOffset = 0;
    private double yOffset = 0;

    // List of some emoji image filenames to be added to the picker


    // Add this method to initialize the circular clipping for the image
    private void initializeCircularImage(ImageView imageView, double imageSize) {
        // Set the dimensions of the image view to be square
        imageView.setFitWidth(imageSize);
        imageView.setFitHeight(imageSize);

        // Create a circle clipping mask
        Circle clip = new Circle();
        clip.setCenterX(imageSize / 2);
        clip.setCenterY(imageSize / 2);
        clip.setRadius(imageSize / 2);

        // Apply the clipping mask to the image view
        imageView.setClip(clip);
    }


    @FXML
    void initialize() {
        // Apply circular clipping to selectedUserImage
        initializeCircularImage(selectedUserImage, 70);

        HandlerThread.userItemMouseEvent = this::handleUserItemMouseClick;
        // Set current user info
        initializeCurrentUser();


        // Make the UserWindow draggable
        makeWindowDraggable();

        // Bind the visibility of the message text field and send button to the isChatSelected property
        UserWindow.visibleProperty().bind(isChatSelected);
        messageTextField.visibleProperty().bind(isChatSelected);
        sendImgbtn.visibleProperty().bind(isChatSelected.and(Bindings.isNotEmpty(messageTextField.textProperty())));
        emojiButton.visibleProperty().bind(isChatSelected);
        AttachBtn.visibleProperty().bind(isChatSelected);

        // Initialize emoji picker
        initializeEmojiPicker();
        // Initialize attachment button
        initializeAttachButton();
        initializeDropdownMenu();

    }

    private void initializeCurrentUser() {
        // Example current user data, replace with actual data source

        String currentUserName = LoginController.Current_User;

        CurrentUserName.setText(currentUserName);

        System.out.println(currentUserName);
        if(currentUserName.equals("Sarah Donald") || currentUserName.equals("sarah@gmail.com")) {
            CurrentUserImg.setImage(HandlerThread.imageUrl2);
        }
        else
            CurrentUserImg.setImage(HandlerThread.imageUrl1);
        //CurrentUserImg.setImage(currentUserImage);

        // Apply circular clipping to CurrentUserImg
        initializeCircularImage(CurrentUserImg, 70);
    }


    private void makeWindowDraggable() {
        // Event handlers for dragging the window
        addDragHandlers(anchRoot);
        addDragHandlers(SplitP);
        addDragHandlers(messageViewBox);
        addDragHandlers(usersViewBox);
        addDragHandlers(StatusTab);
    }

    private void addDragHandlers(Node node) {
        node.setOnMousePressed(event -> {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            xOffset = stage.getX() - event.getScreenX();
            yOffset = stage.getY() - event.getScreenY();
        });

        node.setOnMouseDragged(event -> {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setX(event.getScreenX() + xOffset);
            stage.setY(event.getScreenY() + yOffset);
        });
    }


    private void initializeEmojiPicker() {
        ContextMenu  emojiPicker = new ContextMenu();
        emojiPicker.setStyle("-fx-background-color: #242526;");

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setPrefSize(300, 200);
        tabPane.setStyle("-fx-background-color: #242526;");

        File emojiDir = new File("C:\\Users\\speedlink\\IdeaProjects\\ChatApp\\src\\main\\resources\\com\\none\\chatapp\\icons\\emojis\\");
        File[] categoryDirs = emojiDir.listFiles(File::isDirectory);

        if (categoryDirs != null) {
            for (File categoryDir : categoryDirs) {
                String categoryName = categoryDir.getName();
                FlowPane flowPane = new FlowPane();
                flowPane.setStyle("-fx-background-color: #242526;");
                flowPane.setHgap(5);
                flowPane.setVgap(5);
                flowPane.setPrefWrapLength(250);

                File[] emojiFiles = categoryDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));
                if (emojiFiles != null) {
                    for (File emojiFile : emojiFiles) {
                        try {
                            Image emojiImage = new Image(new FileInputStream(emojiFile));
                            ImageView emojiImageView = new ImageView(emojiImage);
                            emojiImageView.setFitHeight(20);
                            emojiImageView.setFitWidth(20);
                            emojiImageView.setStyle("-fx-opacity: 0.8;");

                            HBox hbox = new HBox(emojiImageView);
                            hbox.setStyle("-fx-background-color: #242526;");  // Set background color
                            hbox.setOnMouseEntered(e -> hbox.setStyle("-fx-background-color: #3A3B3C;"));  // Set hover color
                            hbox.setOnMouseExited(e -> hbox.setStyle("-fx-background-color: #242526;"));  // Reset to original color

                            hbox.setOnMouseClicked(e -> messageTextField.appendText(getEmojiText(emojiFile.getName())));
                            flowPane.getChildren().add(hbox);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }

                ScrollPane scrollPane = new ScrollPane(flowPane);
                scrollPane.setFitToWidth(true);
                scrollPane.setFitToHeight(true);
                scrollPane.setStyle("-fx-background-color: #242526;");

                Tab tab = new Tab(categoryName, scrollPane);
                tab.setStyle("-fx-background-color: white; -fx-text-fill: white;");

                tab.setOnSelectionChanged(event -> {
                    if (tab.isSelected()) {
                        tab.setStyle("-fx-background-color: #F48967; -fx-text-fill: white;");
                    } else {
                        tab.setStyle("-fx-background-color: white; -fx-text-fill: white;");
                    }
                });

                tabPane.getTabs().add(tab);
            }
        }

        CustomMenuItem tabPaneItem = new CustomMenuItem(tabPane, false);
        emojiPicker.getItems().add(tabPaneItem);

        emojiButton.setOnMouseClicked(event -> emojiPicker.show(emojiButton, event.getScreenX(), event.getScreenY()));
    }

    private String getEmojiText(String fileName) {
        // This method can be customized to convert the filename to a desired emoji text representation
        return ":" + fileName.substring(0, fileName.lastIndexOf('.')) + ":";
    }

    private void initializeAttachButton() {
        AttachBtn.setOnMouseClicked(event -> handleAttachButtonEvent());
    }

    private void handleAttachButtonEvent() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Attachments");
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            String fileExtension = getFileExtension(file.getName());
            // send it
        }
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex != -1 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1).toLowerCase();
        }
        return "";
    }


    @FXML
    public void handleSendButtonEvent(MouseEvent event) {
        Message msg = new Message();
        msg.conv_id = selected_conv_id;
        msg.content = messageTextField.getText();
        msg.type = Message.Type.text;
        try {
            new SendMessageCommand(msg).SendCommand(HandlerThread.socket);
            messageTextField.clear();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    void handleUserItemMouseClick(MouseEvent event) {
        messageViewBox.getChildren().clear();

        Object caller = event.getSource();
        if(!(caller instanceof UserItem)) caller = ((Node)caller).getParent();
        if(caller instanceof UserItem selectedUser)
        {
            selected_user_id = selectedUser.usr_id;
            try {
                System.out.println(event.getSource().toString());
                new MessagesListRequestCommand(selected_user_id).SendCommand(HandlerThread.socket);
                selectedUserName.setText(selectedUser.getName());
                if (selectedUser.getName().equals("Sarah Donald")) {
                    selectedUserImage.setImage(HandlerThread.imageUrl2);
                } else {
                    selectedUserImage.setImage(HandlerThread.imageUrl1);
                }
                messageViewBox.setAlignment(Pos.TOP_LEFT);
                // Update the isChatSelected property to show the message text field and send button
                isChatSelected.set(true);
            }
            catch (IOException e)
            {
                System.out.println("IOEXception: "+ e.getMessage());
            }
        }
    }

    private void initializeDropdownMenu() {
        ContextMenu userMenu = new ContextMenu();
        userMenu.setStyle("-fx-background-color: #242526;");

        // User Information
        HBox userInfoBox = new HBox();
        userInfoBox.setAlignment(Pos.CENTER_LEFT);
        userInfoBox.setSpacing(10);
        userInfoBox.setStyle("-fx-padding: 10px; -fx-background-color: #3A3B3C;");

        ImageView userProfileImage = new ImageView(CurrentUserImg.getImage());
        initializeCircularImage(userProfileImage, 40);

        Label userInfo = new Label(LoginController.Current_User + " (ID: " + getCurrentUserId() + ")");
        userInfo.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");

        userInfoBox.getChildren().addAll(userProfileImage, userInfo);
        CustomMenuItem userInfoItem = new CustomMenuItem(userInfoBox, false);

        // Menu Items
        MenuItem editStatusItem = createMenuItem("Edit Status", this::handleEditStatus);
        MenuItem editUsernameItem = createMenuItem("Edit Username", this::handleEditUsername);
        MenuItem changePasswordItem = createMenuItem("Change Password", this::handleChangePassword);
        MenuItem uploadProfileImageItem = createMenuItem("Upload Profile Image", this::handleUploadProfileImage);
        MenuItem deleteProfileImageItem = createMenuItem("Delete Profile Image", this::handleDeleteProfileImage);
        MenuItem deleteAccountItem = createMenuItem("Delete Account", this::handleDeleteAccount);
        MenuItem logoutItem = createMenuItem("Logout", this::handleLogout);

        userMenu.getItems().addAll(
                userInfoItem,
                new SeparatorMenuItem(),
                editStatusItem,
                editUsernameItem,
                changePasswordItem,
                uploadProfileImageItem,
                deleteProfileImageItem,
                deleteAccountItem,
                new SeparatorMenuItem(),
                logoutItem
        );
        CurrentUserImg.setOnMouseClicked(event -> userMenu.show(CurrentUserImg, event.getScreenX(), event.getScreenY()));
    }


    private MenuItem createMenuItem(String text, Runnable action) {
        HBox menuItemBox = new HBox();
        menuItemBox.setAlignment(Pos.CENTER_LEFT);
        menuItemBox.setSpacing(10);
        menuItemBox.setStyle("-fx-background-color: #242526; -fx-background-radius: 5px;"); // Initial style

        // Add mouse event handlers to the HBox to apply hover effect
        //menuItemBox.setOnMouseEntered(e -> menuItemBox.setStyle("-fx-background-color: #F48967; -fx-background-radius: 5px;"));
        //menuItemBox.setOnMouseExited(e -> menuItemBox.setStyle("-fx-background-color: #242526; -fx-background-radius: 5px;"));

        Label label = new Label(text);
        label.setStyle("-fx-text-fill: white;");

        menuItemBox.getChildren().add(label);

        CustomMenuItem menuItem = new CustomMenuItem(menuItemBox, true);
        menuItem.setOnAction(event -> action.run());

        return menuItem;
    }

    private String getCurrentUserId() {
        // Return the current user's ID
        return "123"; // Replace with actual user ID retrieval logic
    }

    private void handleEditStatus() {
        Stage stage = new Stage();
        VBox vbox = new VBox();
        vbox.setStyle("-fx-background-color: #242526; -fx-padding: 20px;");
        vbox.setSpacing(10);

        Label label = new Label("Status");
        label.setStyle("-fx-text-fill: white;");
        TextField textField = new TextField();
        Button saveButton = new Button("Save");
        saveButton.setStyle("-fx-background-color: #F48967; -fx-text-fill: white;");
        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #3A3B3C; -fx-text-fill: white;");

        saveButton.setOnAction(e -> {
            String newStatus = textField.getText();
            // Save the new status logic here
            stage.close();
        });

        cancelButton.setOnAction(e -> stage.close());

        HBox buttons = new HBox(saveButton, cancelButton);
        buttons.setSpacing(10);

        vbox.getChildren().addAll(label, textField, buttons);
        Scene scene = new Scene(vbox, 300, 150);
        stage.setScene(scene);
        stage.show();
    }



    private void handleEditUsername() {
        Stage stage = new Stage();
        VBox vbox = new VBox();
        vbox.setStyle("-fx-background-color: #242526; -fx-padding: 20px;");
        vbox.setSpacing(10);

        Label label = new Label("Username");
        label.setStyle("-fx-text-fill: white;");
        TextField textField = new TextField();
        Button saveButton = new Button("Save");
        saveButton.setStyle("-fx-background-color: #F48967; -fx-text-fill: white;");
        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #3A3B3C; -fx-text-fill: white;");

        saveButton.setOnAction(e -> {
            String newUsername = textField.getText();
            // Save the new username logic here
            stage.close();
        });

        cancelButton.setOnAction(e -> stage.close());

        HBox buttons = new HBox(saveButton, cancelButton);
        buttons.setSpacing(10);

        vbox.getChildren().addAll(label, textField, buttons);
        Scene scene = new Scene(vbox, 300, 150);
        stage.setScene(scene);
        stage.show();
    }


    private void handleChangePassword() {
        Stage stage = new Stage();
        VBox vbox = new VBox();
        vbox.setStyle("-fx-background-color: #242526; -fx-padding: 20px;");
        vbox.setSpacing(10);

        Label currentPasswordLabel = new Label("Current Password");
        currentPasswordLabel.setStyle("-fx-text-fill: white;");
        PasswordField currentPasswordField = new PasswordField();

        Label newPasswordLabel = new Label("New Password");
        newPasswordLabel.setStyle("-fx-text-fill: white;");
        PasswordField newPasswordField = new PasswordField();

        Button saveButton = new Button("Save");
        saveButton.setStyle("-fx-background-color: #F48967; -fx-text-fill: white;");
        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #3A3B3C; -fx-text-fill: white;");

        saveButton.setOnAction(e -> {
            String currentPassword = currentPasswordField.getText();
            String newPassword = newPasswordField.getText();
            // Save the new password logic here
            stage.close();
        });

        cancelButton.setOnAction(e -> stage.close());

        HBox buttons = new HBox(saveButton, cancelButton);
        buttons.setSpacing(10);

        vbox.getChildren().addAll(currentPasswordLabel, currentPasswordField, newPasswordLabel, newPasswordField, buttons);
        Scene scene = new Scene(vbox, 300, 200);
        stage.setScene(scene);
        stage.show();
    }

    private void handleUploadProfileImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            // Logic to upload and set the profile image
        }
    }

    private void handleDeleteProfileImage() {
        CurrentUserImg.setImage(HandlerThread.imageUrl1);
    }

    private void handleDeleteAccount() {
        Stage stage = new Stage();
        VBox vbox = new VBox();
        vbox.setStyle("-fx-background-color: #242526; -fx-padding: 20px;");
        vbox.setSpacing(10);

        Label titleLabel = new Label("Delete Account");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        Label descriptionLabel = new Label("After confirmation, your account will be deleted in 7 days. " +
                "Please note, you can cancel your deletion request at anytime within 7 days from your profile.");
        descriptionLabel.setStyle("-fx-text-fill: white;");
        descriptionLabel.setWrapText(true);

        Label passwordLabel = new Label("Password");
        passwordLabel.setStyle("-fx-text-fill: white;");
        PasswordField passwordField = new PasswordField();

        Button deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-background-color: darkred; -fx-text-fill: white;");
        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #3A3B3C; -fx-text-fill: white;");

        deleteButton.setOnAction(e -> {
            String password = passwordField.getText();
            // Logic to request account deletion with the provided password
            stage.close();
        });

        cancelButton.setOnAction(e -> stage.close());

        HBox buttons = new HBox(deleteButton, cancelButton);
        buttons.setSpacing(10);

        vbox.getChildren().addAll(titleLabel, descriptionLabel, passwordLabel, passwordField, buttons);
        Scene scene = new Scene(vbox, 400, 250);
        stage.setScene(scene);
        stage.show();
    }

    private void handleLogout() {
        Stage stage = (Stage) CurrentUserImg.getScene().getWindow();
        stage.close();
    }



}
