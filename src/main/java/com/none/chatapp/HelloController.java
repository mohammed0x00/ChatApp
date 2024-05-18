package com.none.chatapp;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;

public class HelloController {
    @FXML
    private FlowPane chatMenu;

    @FXML
    protected void onHelloButtonClick() {
        MessageBubble x = new MessageBubble();
        chatMenu.getChildren().add(x);
    }
}