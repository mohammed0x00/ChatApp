package com.none.chatapp;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

class MessageBubble extends HBox {
    private static final double PADDING = 10;
    private static final double ARC_SIZE = 20;

    public MessageBubble(String message, String time, String status) {
        // Create a Text for the message
        Text messageText = new Text(message);
        messageText.setTextAlignment(TextAlignment.LEFT);

        // Create a StackPane for the text to apply padding
        StackPane textContainer = new StackPane(messageText);
        textContainer.setPadding(new Insets(PADDING));
        textContainer.setBackground(new Background(new BackgroundFill(
                Color.LIGHTBLUE, new CornerRadii(ARC_SIZE), Insets.EMPTY)));

        // Create Labels for time and status
        Label timeLabel = new Label(time);
        timeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");

        Label statusLabel = new Label(status);
        statusLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");

        VBox labelsBox = new VBox(timeLabel, statusLabel);
        labelsBox.setAlignment(Pos.BOTTOM_RIGHT);
        labelsBox.setSpacing(2);

        // Add textContainer and labelsBox to the HBox
        this.getChildren().addAll(textContainer, labelsBox);
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(10); // Space between bubble and labels
        this.setPadding(new Insets(10)); // Add padding around the HBox

        // Make sure HBox resizes based on VBox width
        this.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(this, Priority.SOMETIMES);

        // Bind the text's wrapping width to the available width of the HBox minus some padding
        messageText.wrappingWidthProperty().bind(Bindings.createDoubleBinding(() ->
                Math.max(0, this.getWidth() - 80), this.widthProperty()));

        // Add a listener to adjust the width based on the content
        messageText.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
            double newWidth = newBounds.getWidth() + PADDING * 2;
            textContainer.setMaxWidth(newWidth);
            textContainer.setMinWidth(Region.USE_PREF_SIZE); // Use the preferred size
        });
    }
}
