package com.none.chatapp;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;


public class UsersController {


    @FXML
    private VBox messagesViewBox;

    @FXML
    protected void onSendButtonClick() {
        messagesViewBox.getChildren().add(new MessageBubble("hello", "34:44", "seen"));
        messagesViewBox.getChildren().add(new MessageBubble("icneruicmeriocmeriocmeriocmerio\ncmeioceoicmeriomceriocmeocmeriomceriomcoeirmcoemroicvermocvimeorc", "34:44", "seen"));
    }


}
