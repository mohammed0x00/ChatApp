package com.none.chatapp;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.input.MouseEvent;


public class UserItem extends HBox {
    private static final double PADDING = 5;
    private static final double IMAGE_SIZE = 50;
    private static final double STATUS_CIRCLE_RADIUS = 7; // Increase radius for visibility
    public int usr_id;

    public UserItem(EventHandler<MouseEvent> click_event, int id, String name, boolean isOnline, Image image) {
        usr_id = id;
        // Create ImageView for the user's image
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(IMAGE_SIZE);
        imageView.setFitHeight(IMAGE_SIZE);
        imageView.setPreserveRatio(true);
        imageView.setClip(new Circle(IMAGE_SIZE / 2, IMAGE_SIZE / 2, IMAGE_SIZE / 2)); // Clip to make it circular

        // Create a Circle for the user's status
        Circle statusCircle = new Circle(STATUS_CIRCLE_RADIUS);
        statusCircle.setFill(isOnline ? Color.GREEN : Color.GRAY);

        // StackPane to overlay the status circle on the image
        StackPane imageStackPane = new StackPane(imageView, statusCircle);
        StackPane.setAlignment(statusCircle, Pos.BOTTOM_RIGHT);
        statusCircle.setTranslateX(-STATUS_CIRCLE_RADIUS / 2);
        statusCircle.setTranslateY(STATUS_CIRCLE_RADIUS / 2);

        // Create Label for the user's name
        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        nameLabel.setTextFill(Color.WHITE);

        // Add imageStackPane and nameLabel to the main HBox
        this.getChildren().addAll(imageStackPane, nameLabel);
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(PADDING);
        this.setPadding(new Insets(PADDING));

        imageStackPane.setOnMouseClicked(click_event);
        imageView.setOnMouseClicked(click_event);
        nameLabel.setOnMouseClicked(click_event);
        this.setOnMouseClicked(click_event);

    }

}
