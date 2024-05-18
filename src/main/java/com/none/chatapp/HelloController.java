package com.none.chatapp;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

public class HelloController {
    @FXML
    private FlowPane chatMenu;

    @FXML
    protected void onHelloButtonClick() {
        MessageBubble x = new MessageBubble("hello");
        chatMenu.getChildren().add(x);
    }
}