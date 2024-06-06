package com.none.chatapp;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.control.Label;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.io.ByteArrayInputStream;

public class UserItem extends HBox {
    private static final double PADDING = 5;
    private static final double IMAGE_SIZE = 50;
    private static final double STATUS_CIRCLE_RADIUS = 7; // Increase radius for visibility
    private static final long DEBOUNCE_INTERVAL = 300; // 300 milliseconds debounce interval
    private long lastClickTime = 0;

    public int usr_id;
    Circle statusCircle;
    Label nameLabel;
    private boolean isSelected = false;
    private static UserItem selectedUserItem = null;
    ImageView imageView;

    public UserItem(EventHandler<MouseEvent> click_event, int id, String name, boolean isOnline) {
        usr_id = id;
        // Create ImageView for the user's image
        imageView = new ImageView();
        imageView.setFitWidth(IMAGE_SIZE);
        imageView.setFitHeight(IMAGE_SIZE);
        imageView.setPreserveRatio(true);
        imageView.setClip(new Circle(IMAGE_SIZE / 2, IMAGE_SIZE / 2, IMAGE_SIZE / 2)); // Clip to make it circular

        // Create a Circle for the user's status
        statusCircle = new Circle(STATUS_CIRCLE_RADIUS);
        setStatus(isOnline);

        // StackPane to overlay the status circle on the image
        StackPane imageStackPane = new StackPane(imageView, statusCircle);
        StackPane.setAlignment(statusCircle, Pos.BOTTOM_RIGHT);
        statusCircle.setTranslateX(-STATUS_CIRCLE_RADIUS / 2);
        statusCircle.setTranslateY(STATUS_CIRCLE_RADIUS / 2);

        // Create Label for the user's name
        nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        nameLabel.setTextFill(Color.WHITE);

        // Add imageStackPane and nameLabel to the main HBox
        this.getChildren().addAll(imageStackPane, nameLabel);
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(PADDING);
        this.setPadding(new Insets(PADDING));

        // Set the initial background color
        this.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));

        // Set event handlers for hover and selection effects using hex colors
        this.setOnMouseEntered(event -> {
            if (!isSelected) {
                this.setBackground(new Background(new BackgroundFill(Color.web("#3A3B3C"), CornerRadii.EMPTY, Insets.EMPTY))); // DarkGray in hex
            }
        });

        this.setOnMouseExited(event -> {
            if (!isSelected) {
                this.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
            }
        });

        EventHandler<MouseEvent> combinedEvent = event -> {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastClickTime < DEBOUNCE_INTERVAL) {
                return; // Ignore this click as it is within the debounce interval
            }
            lastClickTime = currentTime;

            // Execute the passed click_event handler
            click_event.handle(event);

            // Handle the selection logic
            if (selectedUserItem != null) {
                selectedUserItem.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
                selectedUserItem.isSelected = false;
            }
            selectedUserItem = this;
            this.setBackground(new Background(new BackgroundFill(Color.web("#5E423F"), CornerRadii.EMPTY, Insets.EMPTY))); // SteelBlue in hex
            this.isSelected = true;
        };

        imageStackPane.setOnMouseClicked(combinedEvent);
        imageView.setOnMouseClicked(combinedEvent);
        nameLabel.setOnMouseClicked(combinedEvent);
        this.setOnMouseClicked(combinedEvent);
    }

    public void setStatus(boolean isOnline) {
        statusCircle.setFill(isOnline ? Color.GREEN : Color.GRAY);
    }

    public String getName() {
        return nameLabel.getText();
    }

    public void setImage(byte[] data)
    {
        if(data != null)
        {
            Platform.runLater(() ->imageView.setImage(new Image(new ByteArrayInputStream(data))));
        }

    }
}
