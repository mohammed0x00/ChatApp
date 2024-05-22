package com.none.chatapp;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
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
    private ResourceBundle resources;

    @FXML
    private URL location;

    Timer timer = new Timer();

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
                    for(UserItem item : Objects.requireNonNull(DatabaseUtil.getOnlineUsersList()))
                    {
                        Platform.runLater(() -> usersViewBox.getChildren().add(item));
                    }
                }
                catch (Exception ignored)
                {
                    System.out.println(ignored.getMessage());
                }
            }
        };

        // Schedule the task to run every 5 seconds with an initial delay of 0 seconds
        long delay = 0;
        long period = 60000 * 2;
        timer.scheduleAtFixedRate(repetitiveTask, delay, period);

        try{
            for(UserItem item : Objects.requireNonNull(DatabaseUtil.getOnlineUsersList()))
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
       usersViewBox.getChildren().add(new UserItem("Mohammed Ali Mansour", true, new Image("https://w7.pngwing.com/pngs/178/595/png-transparent-user-profile-computer-icons-login-user-avatars.png")));
       messageViewBox.getChildren().add(new MessageBubble("hello, I'm mohammed ali", "9:32", "seen"));
       messageViewBox.getChildren().add(new MessageBubble("hello, I'm mohammed aliiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii", "9:32", "seen"));

    }

}
