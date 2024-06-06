package com.none.chatapp;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


import com.none.chatapp_commands.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;


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
    public ImageView CurrentUserImg;

    public static int selected_user_id;
    public static int selected_conv_id;

    private BooleanProperty isChatSelected = new SimpleBooleanProperty(false);
    // For dragging the window
    private double xOffset = 0;
    private double yOffset = 0;
    private ImageView imageView;
    private Rectangle cropRect;
    private ImageView croppedImageView;


    // List of some emoji image filenames to be added to the picker


    // Add this method to initialize the circular clipping for the image
    public void initializeCircularImage(ImageView imageView, double imageSize) {
        // Set the dimensions of the image view to be square
        imageView.setFitWidth(imageSize);
        imageView.setFitHeight(imageSize);
        Circle clip = new Circle(imageSize / 2, imageSize / 2, imageSize / 2);
        imageView.setClip(clip);
    }


    @FXML
    void initialize() {
        // Apply circular clipping to selectedUserImage

        initializeCircularImage(selectedUserImage, 70);
        initializeCircularImage(CurrentUserImg, 70);

        HandlerThread.userItemMouseEvent = this::handleUserItemMouseClick;


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

                            // Modify the click event to insert emoji image instead of text
                            hbox.setOnMouseClicked(e -> insertEmojiImage(emojiImage));
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

    // Method to insert emoji image into the message view
    private void insertEmojiImage(Image emojiImage) {
        ImageView emojiImageView = new ImageView(emojiImage);
        emojiImageView.setFitHeight(20);
        emojiImageView.setFitWidth(20);
        HBox hbox = new HBox(emojiImageView);
        messageViewBox.getChildren().add(hbox);
    }

    private void initializeAttachButton() {
        AttachBtn.setOnMouseClicked(event -> handleAttachButtonEvent());
    }

    private void handleAttachButtonEvent() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(null);

        try{
            if (selectedFile != null) {
                Path filePath = Paths.get(selectedFile.getAbsolutePath());
                byte[] file = Files.readAllBytes(filePath);
                Message msg = new Message();
                msg.conv_id = selected_conv_id;
                msg.content = Utils.getFileExtension(filePath);
                msg.type = Message.Type.image;
                try {
                    new SendMessageCommand(msg, file).SendCommand(HandlerThread.socket);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }catch (Exception e)
        {
            Utils.showAlert(Alert.AlertType.ERROR, "Error", "Error occurred while sending file" + e.toString());
        }


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
            try {
                Image image = new Image(new FileInputStream(selectedFile));
                showCropWindow(image);
            } catch (FileNotFoundException e) {
                Utils.showAlert(Alert.AlertType.ERROR, "Error", "Can't load image" + e.toString());
            }
        }
    }

    private void handleDeleteProfileImage() {
        try{
            new ChangeUserImageCommand(true).SendCommand(HandlerThread.socket);
        }
        catch (Exception e)
        {
            Utils.showAlert(Alert.AlertType.ERROR, "Error", "Can't delete image");
        }
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

    private void showCropWindow(Image image) {
        Stage cropStage = new Stage(StageStyle.UNDECORATED);
        cropStage.initModality(Modality.APPLICATION_MODAL);

        BorderPane root = new BorderPane();
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        Pane imagePane = new Pane(imageView);
        root.setCenter(imagePane);

        Circle cropCircle = new Circle(100);
        cropCircle.setStroke(Color.BLUE);
        cropCircle.setStrokeWidth(2);
        cropCircle.setFill(Color.TRANSPARENT);
        cropCircle.setCenterX(image.getWidth() / 2);
        cropCircle.setCenterY(image.getHeight() / 2);
        imagePane.getChildren().add(cropCircle);

        // Add event handlers to drag the circle
        cropCircle.setOnMousePressed(event -> {
            cropCircle.setUserData(new javafx.geometry.Point2D(event.getSceneX(), event.getSceneY()));
        });

        cropCircle.setOnMouseDragged(event -> {
            javafx.geometry.Point2D mousePress = (javafx.geometry.Point2D) cropCircle.getUserData();
            double deltaX = event.getSceneX() - mousePress.getX();
            double deltaY = event.getSceneY() - mousePress.getY();
            cropCircle.setCenterX(cropCircle.getCenterX() + deltaX);
            cropCircle.setCenterY(cropCircle.getCenterY() + deltaY);
            cropCircle.setUserData(new javafx.geometry.Point2D(event.getSceneX(), event.getSceneY()));
        });

        Button cropButton = new Button("Crop");
        cropButton.setOnAction(e -> {
            WritableImage croppedImage = cropCircularImage(imageView, cropCircle);
            if (croppedImage != null) {
                CurrentUserImg.setImage(croppedImage);
                saveCroppedImage(croppedImage); // Save the cropped image
                cropStage.close();
            }
        });
        root.setBottom(cropButton);
        BorderPane.setAlignment(cropButton, Pos.CENTER);
        BorderPane.setMargin(cropButton, new javafx.geometry.Insets(10));

        Scene scene = new Scene(root, image.getWidth(), image.getHeight());
        cropStage.setScene(scene);
        cropStage.show();
    }
    private WritableImage cropCircularImage(ImageView imageView, Circle cropCircle) {
        int width = (int) (cropCircle.getRadius() * 2);
        int height = (int) (cropCircle.getRadius() * 2);
        WritableImage croppedImage = new WritableImage(width, height);
        PixelWriter pixelWriter = croppedImage.getPixelWriter();
        PixelReader pixelReader = imageView.getImage().getPixelReader();

        int startX = (int) (cropCircle.getCenterX() - cropCircle.getRadius());
        int startY = (int) (cropCircle.getCenterY() - cropCircle.getRadius());

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double distance = Math.sqrt(Math.pow(x - cropCircle.getRadius(), 2) + Math.pow(y - cropCircle.getRadius(), 2));
                if (distance <= cropCircle.getRadius()) {
                    int imageX = startX + x;
                    int imageY = startY + y;
                    if (imageX >= 0 && imageX < imageView.getImage().getWidth() && imageY >= 0 && imageY < imageView.getImage().getHeight()) {
                        Color color = pixelReader.getColor(imageX, imageY);
                        pixelWriter.setColor(x, y, color);
                    }
                } else {
                    pixelWriter.setColor(x, y, Color.TRANSPARENT);
                }
            }
        }
        return croppedImage;
    }




    private void saveCroppedImage(WritableImage image) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Files", "*.png"));
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                BufferedImage bufferedImage = new BufferedImage((int) image.getWidth(), (int) image.getHeight(), BufferedImage.TYPE_INT_ARGB);
                for (int y = 0; y < image.getHeight(); y++) {
                    for (int x = 0; x < image.getWidth(); x++) {
                        int argb = image.getPixelReader().getArgb(x, y);
                        bufferedImage.setRGB(x, y, argb);
                    }
                }
                ImageIO.write(bufferedImage, "png", file);

            } catch (IOException ex) {
                ex.printStackTrace();
                Utils.showAlert(Alert.AlertType.ERROR, "Error", "Error occurred while saving the image: " + ex.toString());
            }
        }
    }









}
