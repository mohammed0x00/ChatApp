package com.none.chatapp;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class MessageController {

    @FXML
    private Label contentLabel;

    @FXML
    private void initialize() {
        // This method will be called after the FXML file is loaded
        contentLabel.setText("Hello, World!");
    }

}
