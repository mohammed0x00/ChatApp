package com.none.chatapp;

import javafx.scene.control.Label;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class MessageBubble extends Pane {

    @FXML
    private Label contentLabel;

    public MessageBubble() {

        FXMLLoader fxmlLoader1 = new FXMLLoader(getClass().getResource("MessageBubble.fxml"));

        try {
            fxmlLoader1.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //this.setHeight(22);
        //this.setWidth((99));


    }


}
