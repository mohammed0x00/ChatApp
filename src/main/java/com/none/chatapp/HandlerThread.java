package com.none.chatapp;

import com.none.chatapp_commands.*;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

public class HandlerThread extends Thread{
    public static Socket socket;
    static UsersController controller;
    static EventHandler<MouseEvent> userItemMouseEvent;
    private static User my_details;
    private static final Thread me = new Thread(new Runnable() {
        @Override
        public void run()
        {

            try {
                new RequestUsersListCommand().SendCommand(socket);
                new RequestProfileImageCommand().SendCommand(socket);
                new RequestUserDetailsCommand().SendCommand(socket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            while(true)
            {
                try {
                    ServerCommand cmd = ServerCommand.WaitForCommand(socket);
                    switch (cmd) {
                        case UserStatusCommand stat_cmd ->{
                            boolean status = stat_cmd.status == UserStatusCommand.Stat.ONLINE;
                            ResourceMgr.setUserItemStatus(controller, stat_cmd.user, status);
                        }
                        case UserListCommand list_cmd -> {
                            for (User item : list_cmd.list) {
                                UserItem user = new UserItem(userItemMouseEvent, item.id, item.name, true);
                                ResourceMgr.requestFile(item, user);
                                Platform.runLater(() -> controller.usersViewBox.getChildren().add(user));
                            }
                        }
                        case MessageListCommand listCommand -> {
                            controller.selected_conv_id = listCommand.Conversation_id;
                            Platform.runLater(() -> controller.messageViewBox.getChildren().clear());
                            for (Message item : listCommand.list) {
                                MessageBubble tmp = new MessageBubble(item);
                                if (item.type == Message.Type.audio || item.type ==  Message.Type.image)
                                    ResourceMgr.requestFile(item, tmp);
                                controller.addToMessageList(tmp);
                                //Platform.runLater(() -> controller.messageViewBox.getChildren().add(tmp));
                            }
                        }
                        case ClientNotifyMessageCommand ncmd -> {
                            if (controller.selected_user_id == ncmd.msg.sender_id) {
                                MessageBubble bub = new MessageBubble(ncmd.msg);
                                if (ncmd.msg.type == Message.Type.audio || ncmd.msg.type ==  Message.Type.image)
                                    ResourceMgr.requestFile(ncmd.msg, bub);
                                controller.addToMessageList(bub);

                            }
                        }
                        case MessageConfirmationCommand confcmd -> {
                            if (controller.selected_conv_id == confcmd.msg.conv_id) {
                                MessageBubble bub = new MessageBubble(confcmd.msg);
                                if (confcmd.msg.type == Message.Type.audio || confcmd.msg.type ==  Message.Type.image)
                                    ResourceMgr.requestFile(confcmd.msg, bub);
                                controller.addToMessageList(bub);
                            }
                        }
                        case ResponseUsersListCommand responseCmd -> {
                            for (User item : responseCmd.list) {
                                UserItem user = new UserItem(userItemMouseEvent, item.id, item.name, item.isOnline);
                                if(user.usr_id != -1) ResourceMgr.requestFile(item, user);
                                ResourceMgr.addUserItem(item, user);

                                if (item.isOnline) {
                                    Platform.runLater(() -> controller.usersViewBox.getChildren().add(user));
                                } else {
                                    if(user.usr_id != -1) Platform.runLater(() -> controller.offlineUsersViewBox.getChildren().add(user));
                                }

                            }
                        }
                        case ResponseImageChangeCommand response -> {
                            if (response.result) {
                                Platform.runLater(() -> Utils.showAlert(Alert.AlertType.INFORMATION, "Image Changed", "Image Changed/Removed Successfully."));
                                try {
                                    new RequestProfileImageCommand().SendCommand(socket);
                                } catch (Exception ignored) {
                                }
                            } else
                                Platform.runLater(() -> Utils.showAlert(Alert.AlertType.ERROR, "Error", "Can't Change/Remove Image."));
                        }
                        case ResponeProfileImageCommand img_cmd when img_cmd.status -> {
                            if (img_cmd.status) {
                                if (img_cmd.owner_id == null) {
                                    Platform.runLater(() -> controller.updateImageViewObjects(img_cmd.data));
                                } else {
                                    ResourceMgr.responseHandler(img_cmd);
                                }

                            }
                        }
                        case ResponseFileRequestCommand response_cmd -> ResourceMgr.responseHandler(response_cmd);
                        case ResponseUserDetailsCommand response -> {
                            my_details = response.me;
                            UsersController.userIDLabel.setText(my_details.name + " (ID: " + String.valueOf(my_details.id) + ")");
                        }
                        case ResponseUserInfoChangeCommand response -> {
                            switch (response.responseType)
                            {
                                case CHANGE_PASSWORD -> Platform.runLater(() -> Utils.showAlert(response.status ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR, response.status ? "Task Completed" : "Error", response.status ? "Password Changed Successfully." : response.err_msg));
                                case CHANGE_STATUS_MSG, CHANGE_USER_NAME -> {
                                    Platform.runLater(() -> Utils.showAlert(response.status ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR, response.status ? "Task Completed" : "Error", response.status ? "Successful operation." : "An error occurred"));
                                    if(response.status) new RequestUserDetailsCommand().SendCommand(socket);
                                }
                            }
                        }
                        case null, default -> {
                        }
                    }



                } catch (IOException | ClassNotFoundException e) {
                    System.out.println(e.toString() + " " + e.getMessage());
                }
            }
        }

    });

    public static void startThread()
    {
        me.start();
    }

}
