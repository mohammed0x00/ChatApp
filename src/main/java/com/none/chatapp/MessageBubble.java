package com.none.chatapp;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class MessageBubble extends Region {

    private Label messageLabel;
    private Button deleteButton;

    public MessageBubble(String message) {
        messageLabel = new Label(message);
        messageLabel.setMaxWidth(200); // Set maximum width for text wrapping
        messageLabel.setWrapText(true); // Allow text wrapping

        deleteButton = new Button("Delete");
        deleteButton.setOnAction(event -> {
            // Handle delete button action
            getParent().getChildrenUnmodifiable().remove(this);
        });

        // Styling for message bubble
        Rectangle bubbleBackground = new Rectangle(10, 10);
        bubbleBackground.setFill(Color.LIGHTBLUE);
        bubbleBackground.setStroke(Color.BLUE);

        HBox messageBox = new HBox(10);
        messageBox.setAlignment(Pos.CENTER_LEFT);
        messageBox.getChildren().addAll(messageLabel, deleteButton);

        getChildren().addAll(bubbleBackground, messageBox);
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();

        double width = getWidth();
        double height = getHeight();

        double buttonWidth = deleteButton.prefWidth(-1);
        double buttonHeight = deleteButton.prefHeight(-1);

        // Position the message box inside the bubble
        messageLabel.resizeRelocate(10, 5, width - 2 * buttonWidth - 20, height - 10);
        deleteButton.resizeRelocate(width - buttonWidth - 5, (height - buttonHeight) / 2, buttonWidth, buttonHeight);

        // Set the size of the bubble background
        ((Rectangle) getChildren().get(0)).setWidth(width);
        ((Rectangle) getChildren().get(0)).setHeight(height);
    }

    @Override
    protected double computePrefWidth(double height) {
        return messageLabel.prefWidth(-1) + deleteButton.prefWidth(-1) + 20; // Add padding
    }

    @Override
    protected double computePrefHeight(double width) {
        return Math.max(messageLabel.prefHeight(width), deleteButton.prefHeight(width)) + 10; // Add padding
    }
}
