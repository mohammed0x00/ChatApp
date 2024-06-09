package com.none.chatapp;

import com.none.chatapp_commands.*;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

public class HandlerThread{
    public Socket socket;
    UsersController controller;
    EventHandler<MouseEvent> userItemMouseEvent;
    public ResourceMgr rscMgr;
    private User my_details;
    public Stage login_stage;
    public boolean close_request = false;
    LoginController login_controller;

    public HandlerThread(LoginController login_cont, Stage login, Socket socket, UsersController controller)
    {
        this.socket = socket;
        this.controller = controller;
        rscMgr = new ResourceMgr(this);
        userItemMouseEvent = controller::handleUserItemMouseClick;
        login_stage = login;
        login_controller = login_cont;
    }

    private Thread me = new Thread(new Runnable() {
        @Override
        public void run()
        {
            try {
                initEnvironment();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            while(true)
            {
                if(close_request) break;
                try {
                    ServerCommand cmd = ServerCommand.WaitForCommand(socket);
                    switch (cmd) {
                        case UserStatusCommand stat_cmd ->{
                            boolean status = stat_cmd.status == UserStatusCommand.Stat.ONLINE;
                            rscMgr.setUserItemStatus(controller, stat_cmd.user, status);
                        }
                        case UserListCommand list_cmd -> {
                            Platform.runLater(() -> controller.usersViewBox.getChildren().clear());
                            for (User item : list_cmd.list) {
                                UserItem user = new UserItem(userItemMouseEvent, item.id, item.name, true);
                                rscMgr.requestFile(item, user);
                                Platform.runLater(() -> controller.usersViewBox.getChildren().add(user));
                            }
                        }
                        case MessageListCommand listCommand -> {
                            controller.selected_conv_id = listCommand.Conversation_id;
                            Platform.runLater(() -> controller.messageViewBox.getChildren().clear());
                            for (Message item : listCommand.list) {
                                MessageBubble tmp = new MessageBubble(controller, rscMgr, item);
                                if (item.type == Message.Type.audio || item.type ==  Message.Type.image)
                                    rscMgr.requestFile(item, tmp);
                                controller.addToMessageList(tmp);
                            }
                        }
                        case ClientNotifyMessageCommand ncmd -> {
                            if (controller.selected_user_id == ncmd.msg.sender_id) {
                                MessageBubble bub = new MessageBubble(controller, rscMgr, ncmd.msg);
                                if (ncmd.msg.type == Message.Type.audio || ncmd.msg.type ==  Message.Type.image)
                                    rscMgr.requestFile(ncmd.msg, bub);
                                controller.addToMessageList(bub);

                            }
                        }
                        case MessageConfirmationCommand confcmd -> {
                            if (controller.selected_conv_id == confcmd.msg.conv_id) {
                                MessageBubble bub = new MessageBubble(controller, rscMgr, confcmd.msg);
                                if (confcmd.msg.type == Message.Type.audio || confcmd.msg.type ==  Message.Type.image)
                                    rscMgr.requestFile(confcmd.msg, bub);
                                controller.addToMessageList(bub);
                            }
                        }
                        case ResponseUsersListCommand responseCmd -> {
                            Platform.runLater(() -> controller.usersViewBox.getChildren().clear());
                            Platform.runLater(() -> controller.offlineUsersViewBox.getChildren().clear());
                            for (User item : responseCmd.list) {
                                UserItem user = new UserItem(userItemMouseEvent, item.id, item.name, item.isOnline);
                                if(user.usr_id != -1) rscMgr.requestFile(item, user);
                                rscMgr.addUserItem(item, user);

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
                                    rscMgr.responseHandler(img_cmd);
                                }

                            }
                        }
                        case ResponseFileRequestCommand response_cmd -> rscMgr.responseHandler(response_cmd);
                        case ResponseUserDetailsCommand response -> {
                            my_details = response.me;
                            Platform.runLater(() ->{
                                controller.userIDLabel.setText(my_details.name + " (ID: " + String.valueOf(my_details.id) + ")");
                                controller.userFrontUiLabel.setText(my_details.name);
                            });
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
                        case ResponseDeleteAccountCommand response ->
                        {
                            if(response.status) Platform.runLater(() -> controller.handleLogout());
                            else Platform.runLater(() -> Utils.showAlert(Alert.AlertType.ERROR, "Error", "Unable to Delete Your Account"));
                        }
                        case null, default -> {
                        }
                    }


                } catch (IOException | ClassNotFoundException e) {
                    System.out.println(e.toString() + " " + e.getMessage());
                    if(close_request) break;
                    try {
                        if(login_controller.login())
                        {
                            socket = login_controller.socket;
                            System.out.println("Reconnected");
                            initEnvironment();
                        }
                    } catch (Exception ignored) {
                    }

                }
            }
        }

    });

    void initEnvironment() throws IOException {
        new RequestUsersListCommand().SendCommand(socket);
        new RequestProfileImageCommand().SendCommand(socket);
        new RequestUserDetailsCommand().SendCommand(socket);
    }


    public void startThread()
    {
        me.start();
    }

}
