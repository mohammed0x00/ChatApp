package com.none.chatapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import animatefx.animation.FadeIn;
import animatefx.animation.FadeOut;
import animatefx.animation.ZoomIn;
import com.none.chatapp_commands.Message;
import com.none.chatapp_commands.MessageListCommand;
import com.none.chatapp_commands.MessagesListRequestCommand;
import com.none.chatapp_commands.SendMessageCommand;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;


public class UsersController {

    @FXML
    public VBox usersViewBox;
    
    @FXML
    public VBox messageViewBox;

    @FXML
    public TextField messageTextField;

    private int current_user_id;
    public int selected_user_id;
    public int selected_conv_id;

    @FXML
    void initialize() {
        HandlerThread.userItemMouseEvent = this::handleUserItemMouseClick;
    }

    @FXML
    public void handleSendButtonEvent(MouseEvent event) {
        Message msg = new Message();
        msg.conv_id = selected_conv_id;
        msg.content = messageTextField.getText();
        msg.type = Message.Type.text;
        try {
            new SendMessageCommand(msg).SendCommand(HandlerThread.socket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void handleUserItemMouseClick(MouseEvent event) {
        messageViewBox.getChildren().clear();

        Object caller = event.getSource();
        if(!(caller instanceof UserItem)) caller = ((Node)caller).getParent();
        if(caller instanceof UserItem selectedUser)
        {
            selected_user_id = selectedUser.usr_id;
            try {
                System.out.println(event.getSource().toString());
                new MessagesListRequestCommand(selected_user_id).SendCommand(HandlerThread.socket);
            }
            catch (IOException e)
            {
                System.out.println("IOEXception: "+ e.getMessage());
            }
        }
    }




}
