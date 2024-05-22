package com.none.chatapp;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class UserItem extends HBox {
    private static final double PADDING = 5;
    private static final double IMAGE_SIZE = 50;
    private static final double STATUS_CIRCLE_RADIUS = 5;

    public UserItem(String name, boolean isOnline, Image image) {
        // Create ImageView for the user's image
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(IMAGE_SIZE);
        imageView.setFitHeight(IMAGE_SIZE);
        imageView.setPreserveRatio(true);
        imageView.setClip(new Circle(IMAGE_SIZE / 2, IMAGE_SIZE / 2, IMAGE_SIZE / 2)); // Clip to make it circular

        // Create Label for the user's name
        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // Create a Circle for the user's status
        Circle statusCircle = new Circle(STATUS_CIRCLE_RADIUS);
        statusCircle.setFill(isOnline ? Color.GREEN : Color.GRAY);

        // HBox to hold name and status circle
        HBox nameStatusBox = new HBox(nameLabel, statusCircle);
        nameStatusBox.setAlignment(Pos.CENTER_LEFT);
        nameStatusBox.setSpacing(2);

        // Add ImageView and nameStatusBox to the main HBox
        this.getChildren().addAll(imageView, nameStatusBox);
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(PADDING);
        this.setPadding(new Insets(PADDING));

        // Bind the status circle's position to the name label
        statusCircle.translateXProperty().bind(Bindings.createDoubleBinding(
                () -> nameLabel.getWidth() + STATUS_CIRCLE_RADIUS + 5, // Adjust 5 based on spacing
                nameLabel.widthProperty()
        ));
    }
}