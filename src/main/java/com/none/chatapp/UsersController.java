package com.none.chatapp;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import animatefx.animation.FadeIn;
import animatefx.animation.FadeOut;
import animatefx.animation.ZoomIn;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import static com.none.chatapp.DatabaseUtil.loadConversation;

public class UsersController {

    @FXML
    public VBox usersViewBox;
    
    @FXML
    private VBox messageViewBox;

    private int current_user_id;
    private int selected_user_id;

    @FXML
    void initialize() {
        Timer timer = new Timer();

        // Create a TimerTask that defines the task to be executed
        TimerTask repetitiveTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    DatabaseUtil.updateStatus();
                    Platform.runLater(() -> {
                        // Fade out the usersViewBox
                        new FadeOut(usersViewBox).play();

                        // Clear the usersViewBox after a short delay
                        PauseTransition pause = new PauseTransition(Duration.seconds(0.5)); // Adjust duration as needed
                        pause.setOnFinished(e -> {
                            usersViewBox.getChildren().clear();

                            // Add the updated user list
                            for (UserItem item : Objects.requireNonNull(DatabaseUtil.getOnlineUsersList(UsersController.this::handleUserItemMouseClick))) {
                                usersViewBox.getChildren().add(item);
                            }

                            // Fade in the usersViewBox
                            new FadeIn(usersViewBox).play();
                        });
                        pause.play();
                    });
                } catch (Exception ignored) {
                    // Handle exceptions
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
        if (event.getSource() instanceof UserItem) { // to be on safe side, you may
            // remove this if-statement
            // if you are sure
            UserItem user = (UserItem) event.getSource();
            selected_user_id = user.usr_id;
        }
        loadMessages();
    }

    public void setCurrentUserID(int id)
    {
        current_user_id = id;
    }

    void loadMessages()
    {
        ArrayList<MessageBubble> msgList = loadConversation(current_user_id, selected_user_id);
        messageViewBox.getChildren().clear();
        for(MessageBubble msg : msgList)
        {
            messageViewBox.getChildren().add(msg);
        }

    }


}
