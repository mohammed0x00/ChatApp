package com.none.chatapp;

import com.none.chatapp_commands.Message;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;

class MessageBubble extends HBox {
    private static final double PADDING = 10;
    private static final double ARC_SIZE = 20;

    private Text messageText;
    private ImageView imageView;
    public int msg_id;
    private Message msg;

    public MessageBubble(Message msg) {
        this.msg = msg;
        msg_id = msg.id;

        // Create a Text for the message
        messageText = new Text(msg.content);
        messageText.setTextAlignment(TextAlignment.LEFT);
        messageText.setFill(Color.WHITE);

        // Create a StackPane for the text to apply padding
        StackPane textContainer = new StackPane(messageText);
        textContainer.setPadding(new Insets(PADDING));

        // Create Labels for time and status
        Label timeLabel = new Label("");
        timeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");

        Label statusLabel = new Label(msg.seen ? "seen" : "");
        statusLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");

        VBox labelsBox = new VBox(timeLabel, statusLabel);

        // Determine the alignment and colors based on sender
        if (msg.sender_id == UsersController.selected_user_id) {
            labelsBox.setAlignment(Pos.BOTTOM_LEFT);
            labelsBox.setSpacing(2);
            this.setAlignment(Pos.CENTER_RIGHT);
            textContainer.setBackground(new Background(new BackgroundFill(
                    Color.web("#303030"), new CornerRadii(ARC_SIZE), Insets.EMPTY)));
            this.getChildren().addAll(labelsBox, textContainer); // Sent messages: labels on the left
        } else {
            labelsBox.setAlignment(Pos.BOTTOM_RIGHT);
            labelsBox.setSpacing(2);
            this.setAlignment(Pos.CENTER_LEFT);
            textContainer.setBackground(new Background(new BackgroundFill(
                    Color.web("#B8684D"), new CornerRadii(ARC_SIZE), Insets.EMPTY)));
            this.getChildren().addAll(textContainer, labelsBox); // Received messages: labels on the right
        }

        // Adjust the width of the text container based on the content
        messageText.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
            double textWidth = newBounds.getWidth() + PADDING * 2;
            double maxWidth = this.getScene() != null ? this.getScene().getWidth() * 0.6 : 400; // Max width is 60% of scene width or 400 if scene is not available
            textContainer.setMaxWidth(Math.min(textWidth, maxWidth));
            textContainer.setMinWidth(Region.USE_PREF_SIZE); // Use the preferred size
        });

        // Add padding around the HBox
        this.setSpacing(10); // Space between bubble and labels
        this.setPadding(new Insets(10)); // Add padding around the HBox

        // Make sure HBox resizes based on VBox width
        this.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(this, Priority.ALWAYS);
    }

    public void setImage(byte[] data) {
        if (data == null) {
            Platform.runLater(() -> {
                messageText.setVisible(true);
                messageText.setText("Error: Can't Load " + messageText.getText());
            });
        } else {
            Platform.runLater(() -> {
                messageText.setVisible(false);
                imageView = new ImageView();
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                adjustBubbleSizeForImage(imageView);

                imageView.setImage(new Image(new ByteArrayInputStream(data)));
                adjustBubbleSizeForImage(imageView);

                // Wrap ImageView in a StackPane with padding and rounded corners
                StackPane imageContainer = new StackPane(imageView);
                imageContainer.setPadding(new Insets(PADDING));
                imageContainer.setBackground(new Background(new BackgroundFill(
                        msg.sender_id == UsersController.selected_user_id ? Color.web("#303030") : Color.web("#B8684D"),
                        new CornerRadii(ARC_SIZE), Insets.EMPTY)));


                this.getChildren().clear();
                if (msg.sender_id == UsersController.selected_user_id) {
                    this.getChildren().addAll(imageContainer, new VBox());
                } else {
                    this.getChildren().addAll(new VBox(), imageContainer);
                }
                // Add click event to ImageView
                imageView.setOnMouseClicked(event -> openImageInNewWindow(imageView.getImage()));
            });
        }
    }

    private void adjustBubbleSizeForImage(ImageView imageView) {
        imageView.imageProperty().addListener((obs, oldImage, newImage) -> {
            if (newImage != null) {
                double imageWidth = newImage.getWidth();
                double imageHeight = newImage.getHeight();

                double maxWidth = 448;
                double maxHeight = 290;

                if (imageWidth > maxWidth || imageHeight > maxHeight) {
                    double widthScale = maxWidth / imageWidth;
                    double heightScale = maxHeight / imageHeight;
                    double scale = Math.min(widthScale, heightScale);

                    imageView.setFitWidth(imageWidth * scale);
                    imageView.setFitHeight(imageHeight * scale);
                } else {
                    imageView.setFitWidth(imageWidth);
                    imageView.setFitHeight(imageHeight);
                }
            }
        });
    }
    private void openImageInNewWindow(Image image) {
        Stage stage = new Stage();
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        BorderPane root = new BorderPane(imageView);
        root.setStyle("-fx-background-color: #2e2e2e;"); // Set dark background color

        // Apply styles directly to the ImageView
        imageView.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75), 10, 0.5, 0, 0);");


        Scene scene = new Scene(root, image.getWidth(), image.getHeight());
        stage.setScene(scene);
        stage.setTitle("Image Viewer");
        stage.show();
    }
}
