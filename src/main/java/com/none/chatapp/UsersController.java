package com.none.chatapp;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javax.lang.model.type.NullType;

public class UsersController {

    @FXML
    public VBox usersViewBox;
    
    @FXML
    private VBox messageViewBox;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    void initialize() {

    }

    @FXML
    void handleMouseEvent(MouseEvent event) {
       usersViewBox.getChildren().add(new UserItem("Mohammed Ali Mansour", true, new Image("https://w7.pngwing.com/pngs/178/595/png-transparent-user-profile-computer-icons-login-user-avatars.png")));
       messageViewBox.getChildren().add(new MessageBubble("hello, I'm mohammed ali", "9:32", "seen"));
        messageViewBox.getChildren().add(new MessageBubble("hello, I'm mohammed aliiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii", "9:32", "seen"));

    }
}
