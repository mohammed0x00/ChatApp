package com.none.chatapp;

import com.none.chatapp_commands.Message;
import com.none.chatapp_commands.RequestFileCommand;
import com.none.chatapp_commands.ResponseFileRequestCommand;
import com.none.chatapp_commands.ServerCommand;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

class MessageBubble extends HBox {
    private static final double PADDING = 10;
    private static final double ARC_SIZE = 20;

    public int msg_id;

    public MessageBubble(Message msg, byte[] data) {
        msg_id = msg.id;


        // Create a Text for the message
        Text messageText = new Text(msg.content);
        messageText.setTextAlignment(TextAlignment.LEFT);
        messageText.setFill(Color.WHITE);

        // Create a StackPane for the text to apply padding
        StackPane textContainer = new StackPane(messageText);
        textContainer.setPadding(new Insets(PADDING));

        // Format the timestamp to show only month, day, hour, and minutes
        /*String formattedDate = "N/A"; // Default value if timestamp is null
        if (msg.sent_at != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                formattedDate = sdf.format(msg.sent_at);
            } catch (Exception e) {
                e.printStackTrace(); // Handle potential formatting exceptions
            }
        }*/

        // Create Labels for time and status
        Label timeLabel = new Label("");
        timeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");

        Label statusLabel = new Label(msg.seen ? "seen" : "");
        statusLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");

        VBox labelsBox = new VBox(timeLabel, statusLabel);
        //labelsBox.setAlignment(Pos.BOTTOM_RIGHT);
        //labelsBox.setSpacing(2);

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

        if(msg.type == Message.Type.image)
        {
            messageText.setVisible(false);
            ImageView imageView = new ImageView();
            imageView.setImage(new Image(new ByteArrayInputStream(data)));
            this.getChildren().add(imageView);

        }
    }
}
