package com.none.chatapp;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

class MessageBubble extends HBox {
    private static final double PADDING = 10;
    private static final double ARC_SIZE = 20;

    public MessageBubble(String message, String time, String status) {
        // Create a Text for the message
        Text messageText = new Text(message);
        messageText.setTextAlignment(TextAlignment.LEFT);

        // Create a Rectangle shape for the bubble with rounded corners
        Rectangle bubble = new Rectangle();
        bubble.setArcWidth(ARC_SIZE); // Radius of rounded corners
        bubble.setArcHeight(ARC_SIZE); // Radius of rounded corners
        bubble.setFill(Color.LIGHTBLUE);

        // StackPane to overlay text on the rectangle
        StackPane bubblePane = new StackPane();
        bubblePane.getChildren().addAll(bubble, messageText);
        bubblePane.setPadding(new Insets(PADDING));

        // Bind the text's wrapping width to the width of the HBox minus padding and labels
        messageText.wrappingWidthProperty().bind(Bindings.createDoubleBinding(() ->
                Math.min(500, this.getWidth() - 80), this.widthProperty())); // Adjust 80 based on label width

        // Bind the rectangle's width and height to the text's bounds with padding
        bubble.widthProperty().bind(Bindings.createDoubleBinding(() ->
                messageText.getLayoutBounds().getWidth() + 2 * PADDING, messageText.layoutBoundsProperty()));
        bubble.heightProperty().bind(Bindings.createDoubleBinding(() ->
                messageText.getLayoutBounds().getHeight() + 2 * PADDING, messageText.layoutBoundsProperty()));

        // Create Labels for time and status
        Label timeLabel = new Label(time);
        timeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");

        Label statusLabel = new Label(status);
        statusLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");

        VBox labelsBox = new VBox(timeLabel, statusLabel);
        labelsBox.setAlignment(Pos.BOTTOM_RIGHT);
        labelsBox.setSpacing(2);

        // Add bubblePane and labelsBox to the HBox
        this.getChildren().addAll(bubblePane, labelsBox);
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(10); // Space between bubble and labels
        this.setPadding(new Insets(10)); // Add padding around the HBox

        // Make sure HBox resizes based on VBox width
        this.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(this, javafx.scene.layout.Priority.ALWAYS);
    }
}
