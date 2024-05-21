package com.none.chatapp;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class UsersController {

    @FXML
    public ScrollPane messageViewPane;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    void initialize() {

    }

    @FXML
    void handleMouseEvent(MouseEvent event) {
        messageViewPane.setContent(new MessageBubble("hello world", "3:33", "unseen"));
    }
}
