package com.none.chatapp;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import animatefx.animation.ZoomIn;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class UsersController {

    @FXML
    public VBox usersViewBox;
    
    @FXML
    private VBox messageViewBox;

    @FXML
    void initialize() {
        Timer timer = new Timer();

        // Create a TimerTask that defines the task to be executed
        TimerTask repetitiveTask = new TimerTask() {

            @Override
            public void run() {
                try
                {
                    DatabaseUtil.updateStatus();
                    Platform.runLater(() -> usersViewBox.getChildren().clear());
                    for(UserItem item : Objects.requireNonNull(DatabaseUtil.getOnlineUsersList(UsersController.this::handleUserItemMouseClick)))
                    {
                        Platform.runLater(() -> usersViewBox.getChildren().add(item));
                    }
                }
                catch (Exception ignored)
                {
                }
            }
        };

        // Schedule the task to run every 5 seconds with an initial delay of 0 seconds
        long delay = 0;
        long period = 30000;
        timer.scheduleAtFixedRate(repetitiveTask, delay, period);

        try{
            for(UserItem item : Objects.requireNonNull(DatabaseUtil.getOnlineUsersList(this::handleUserItemMouseClick)))
            {
                usersViewBox.getChildren().add(item);
            }

        }catch(Exception e)
        {
            Utils.showAlert(Alert.AlertType.ERROR, "Error", "Unexpected Error:" + e.getMessage());
        }


    }

    @FXML
    void handleMouseEvent(MouseEvent event) {


    }

    void handleUserItemMouseClick(MouseEvent event) {
        messageViewBox.getChildren().add(new MessageBubble("ahaaaaaaaaaaaaaa", "2:44", "seen"));
    }


}
