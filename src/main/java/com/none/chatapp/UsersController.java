package com.none.chatapp;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.sound.sampled.spi.AudioFileWriter;


public class UsersController {

    @FXML
    public ScrollPane messagesScrollPane;

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

    public static Label userIDLabel;

    @FXML
    public ImageView CurrentUserImg;

    @FXML
    public ImageView RecordButton;

    public ImageView userProfileImage;

    public static int selected_user_id = Integer.MIN_VALUE;
    public static int selected_conv_id = Integer.MIN_VALUE;

    private BooleanProperty isChatSelected = new SimpleBooleanProperty(false);
    private IntegerProperty conv_id = new SimpleIntegerProperty(selected_user_id);

    private AudioFormat audioFormat;
    private TargetDataLine targetDataLine;
    private ByteArrayOutputStream out;
    private Timeline timeline;
    private BooleanProperty isRecording = new SimpleBooleanProperty(false);
    private StringProperty recordingTime = new SimpleStringProperty("00:00");

    // Create a BooleanBinding for the condition selected_conv_id != -1
    BooleanBinding isConversationSelected = conv_id.isNotEqualTo(-1);
    BooleanBinding isChatAndConversationSelected = isChatSelected.and(isConversationSelected);

    // For dragging the window
    private double xOffset = 0;
    private double yOffset = 0;


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
        messageTextField.visibleProperty().bind(isChatAndConversationSelected);
        sendImgbtn.visibleProperty().bind(isChatAndConversationSelected.and(Bindings.isNotEmpty(messageTextField.textProperty())));
        emojiButton.visibleProperty().bind(isChatAndConversationSelected);
        AttachBtn.visibleProperty().bind(isChatAndConversationSelected);
        RecordButton.visibleProperty().bind(isChatAndConversationSelected);
        messagesScrollPane.visibleProperty().bind(isChatAndConversationSelected);

        messageViewBox.heightProperty().addListener((observable, oldValue, newValue) -> {
            messagesScrollPane.layout();
            messagesScrollPane.setVvalue(1.0);
        });

        // Initialize emoji picker
        initializeEmojiPicker();
        // Initialize attachment button
        initializeAttachButton();
        initializeDropdownMenu();
        RecordButton.setOnMouseClicked(event -> showRecordingWindow());

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
        ContextMenu emojiPicker = new ContextMenu();
        emojiPicker.setStyle("-fx-background-color: #242526;");

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setPrefSize(300, 200);
        tabPane.setStyle("-fx-background-color: #242526;");

        URL emojiDirUrl = getClass().getResource("/com/none/chatapp/icons/emojis");
        if (emojiDirUrl != null) {
            File emojiDir = null;
            try {
                emojiDir = new File(emojiDirUrl.toURI());
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
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
        } else {
            System.err.println("Emoji directory not found.");
        }

        CustomMenuItem tabPaneItem = new CustomMenuItem(tabPane, false);
        emojiPicker.getItems().add(tabPaneItem);

        emojiButton.setOnMouseClicked(event -> emojiPicker.show(emojiButton, event.getScreenX(), event.getScreenY()));
    }

    // Method to insert emoji image into the message view
    private void insertEmojiImage(Image emojiImage) {
        Message msg = new Message();
        msg.conv_id = selected_conv_id;
        msg.content = "png";
        msg.type = Message.Type.image;
        try {
            new SendMessageCommand(msg, imageToByteArray(emojiImage)).SendCommand(HandlerThread.socket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initializeAttachButton() {
        AttachBtn.setOnMouseClicked(event -> handleAttachButtonEvent());
    }

    private void handleAttachButtonEvent() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        File selectedFile = fileChooser.showOpenDialog(null);

        try{
            if (selectedFile != null) {
                Path filePath = Paths.get(selectedFile.getAbsolutePath());
                byte[] file = Files.readAllBytes(filePath);
                Message msg = new Message();
                msg.conv_id = selected_conv_id;
                msg.content = Utils.getFileExtension(filePath);
                switch (msg.content)
                {
                    case "png":
                    case "jpg":
                    case "jpeg":
                        msg.type = Message.Type.image;
                        break;
                    case "mp3":
                        msg.type = Message.Type.audio;
                        break;
                    default:
                        msg.type = Message.Type.attachment;
                        break;
                }

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
                selectedUserImage.setImage(selectedUser.getImage());
                messageViewBox.setAlignment(Pos.TOP_LEFT);
                // Update the isChatSelected property to show the message text field and send button
                isChatSelected.set(true);
                conv_id.setValue(selected_user_id);
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

        userProfileImage = new ImageView(CurrentUserImg.getImage());
        initializeCircularImage(userProfileImage, 40);

        userIDLabel = new Label(LoginController.Current_User + " (ID: None)");
        userIDLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");

        userInfoBox.getChildren().addAll(userProfileImage, userIDLabel);
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
            try {
                new ChangeUserInfoCommand.CHANGE_STATUS_MSG(newStatus).SendCommand(HandlerThread.socket);
            }catch (Exception ignored){Utils.showAlert(Alert.AlertType.ERROR, "Error", "An error occurred");}
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
            try {
                new ChangeUserInfoCommand.CHANGE_USER_NAME(newUsername).SendCommand(HandlerThread.socket);
            }catch (Exception ignored){Utils.showAlert(Alert.AlertType.ERROR, "Error", "An error occurred");}
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
            try {
                new ChangeUserInfoCommand.CHANGE_PASSWORD(currentPassword, newPassword).SendCommand(HandlerThread.socket);
            }catch (Exception ignored){Utils.showAlert(Alert.AlertType.ERROR, "Error", "An error occurred");}
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
        cropButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                WritableImage croppedImage = UsersController.this.cropCircularImage(imageView, cropCircle);
                try {
                    if (croppedImage != null) {
                        UsersController.this.saveCroppedImage(croppedImage); // Save the cropped image
                        byte[] img = writableImageToByteArray(croppedImage);
                        new ChangeUserImageCommand(img, "png").SendCommand(HandlerThread.socket);
                        cropStage.close();
                    }
                }catch (Exception ignored)
                {
                    Utils.showAlert(Alert.AlertType.ERROR, "Error", "Can't crop image" + e.toString());
                }
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

    private static byte[] writableImageToByteArray(WritableImage writableImage) throws IOException {
        int width = (int) writableImage.getWidth();
        int height = (int) writableImage.getHeight();
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        PixelReader pixelReader = writableImage.getPixelReader();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = pixelReader.getArgb(x, y);
                bufferedImage.setRGB(x, y, argb);
            }
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);
        return baos.toByteArray();
    }

    public static byte[] imageToByteArray(Image image) throws IOException {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        PixelReader pixelReader = image.getPixelReader();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = pixelReader.getArgb(x, y);
                bufferedImage.setRGB(x, y, argb);
            }
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);
        return baos.toByteArray();
    }

    public void updateImageViewObjects(byte[] img)
    {
        Image image;
        if(img == null) image = new Image(String.valueOf(getClass().getResource("/com/none/chatapp/icons/Default_Profile.png")));
        else image = new Image(new ByteArrayInputStream(img));
        CurrentUserImg.setImage(image);
        initializeCircularImage(CurrentUserImg, 70);
        userProfileImage.setImage(CurrentUserImg.getImage());
    }

    public void addToMessageList(MessageBubble bub) {
        Platform.runLater(() -> {
            // Ensure no duplicates are added
            if (!messageViewBox.getChildren().contains(bub)) {
                messageViewBox.getChildren().add(bub);
            }
        });
    }

    private void showRecordingWindow() {
        Stage recordingStage = new Stage(StageStyle.UNDECORATED);
        recordingStage.initModality(Modality.APPLICATION_MODAL);

        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setSpacing(10);
        root.setStyle("-fx-background-color: #242526; -fx-padding: 20px;");

        Label timerLabel = new Label();
        timerLabel.textProperty().bind(recordingTime);
        timerLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");

        Button startButton = new Button("Start");
        Button stopButton = new Button("Stop");
        Button cancelButton = new Button("Cancel");
        Button sendButton = new Button("Send");

        HBox buttonBox = new HBox(startButton, stopButton, cancelButton, sendButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(10);

        root.getChildren().addAll(timerLabel, buttonBox);

        Scene scene = new Scene(root, 300, 150);
        recordingStage.setScene(scene);
        recordingStage.show();

        startButton.setOnAction(event -> startRecording());
        stopButton.setOnAction(event -> stopRecording());
        cancelButton.setOnAction(event -> {
            stopRecording();
            recordingStage.close();
        });
        sendButton.setOnAction(event -> {
            stopRecording();
            try {
                sendRecording();
            } catch (Exception e) {
                Utils.showAlert(Alert.AlertType.ERROR, "Error", "Unable to send Voice");
            }
            recordingStage.close();
        });

        isRecording.addListener((obs, wasRecording, isNowRecording) -> {
            startButton.setDisable(isNowRecording);
            stopButton.setDisable(!isNowRecording);
            sendButton.setDisable(!wasRecording);
        });

        stopButton.setDisable(true);
        sendButton.setDisable(true);
    }

    private void startRecording() {
        audioFormat = new AudioFormat(16000, 16, 2, true, true);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);

        try {
            targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
            targetDataLine.open(audioFormat);
            targetDataLine.start();

            out = new ByteArrayOutputStream();
            isRecording.set(true);
            recordingTime.set("00:00");

            new Thread(() -> {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while (isRecording.get()) {
                    bytesRead = targetDataLine.read(buffer, 0, buffer.length);
                    out.write(buffer, 0, bytesRead);
                }
            }).start();

            timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                String[] timeParts = recordingTime.get().split(":");
                int minutes = Integer.parseInt(timeParts[0]);
                int seconds = Integer.parseInt(timeParts[1]);

                if (seconds < 59) {
                    seconds++;
                } else {
                    minutes++;
                    seconds = 0;
                }

                recordingTime.set(String.format("%02d:%02d", minutes, seconds));

                if (minutes == 3) {
                    stopRecording();
                }
            }));
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();

        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        if (targetDataLine != null) {
            isRecording.set(false);
            targetDataLine.stop();
            targetDataLine.close();
            if (timeline != null) {
                timeline.stop();
            }
        }
    }

    private void sendRecording() throws Exception {
        byte[] audioData = out.toByteArray();
        Message msg = new Message();
        msg.conv_id = selected_conv_id;
        msg.content = "mp3";
        msg.type = Message.Type.audio;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(audioData);
             AudioInputStream audioInputStream = new AudioInputStream(bais, audioFormat, audioData.length)) {

            // Write the AudioInputStream to a new ByteArrayOutputStream in WAV format
            ByteArrayOutputStream wavOut = new ByteArrayOutputStream();
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, wavOut);

            new SendMessageCommand(msg, wavOut.toByteArray()).SendCommand(HandlerThread.socket);
            audioData = null;
        }catch (Exception ignored){audioData=null;throw new Exception();}
    }


}
