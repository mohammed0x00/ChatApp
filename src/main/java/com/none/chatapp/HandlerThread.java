package com.none.chatapp;

import com.none.chatapp_commands.*;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

public class HandlerThread extends Thread{
    public static Socket socket;
    static UsersController controller;
    static EventHandler<MouseEvent> userItemMouseEvent;
    public static Image imageUrl2 = new Image("https://w7.pngwing.com/pngs/129/292/png-transparent-female-avatar-girl-face-woman-user-flat-classy-users-icon.png");
    public static Image imageUrl1 = new Image("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRKBH5DbCnCmwQCpcjv__106JSjG3U2oVNZRw&s");
    static Image imageUrl;

    private static final Thread me = new Thread(new Runnable() {
        @Override
        public void run()
        {

            try {
                new RequestUsersListCommand().SendCommand(socket);
                new RequestProfileImageCommand().SendCommand(socket);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            while(true)
            {
                try {
                    ServerCommand cmd = ServerCommand.WaitForCommand(socket);
                    if(cmd instanceof UserStatusCommand stat_cmd)
                    {
                        if(stat_cmd.status == UserStatusCommand.Stat.ONLINE)
                        {
                            for(Node item : controller.offlineUsersViewBox.getChildren())
                                if(item instanceof UserItem u)
                                    if(u.usr_id == stat_cmd.user.id)
                                    {
                                        u.setStatus(true);
                                        Platform.runLater(() -> controller.offlineUsersViewBox.getChildren().remove(u));
                                        Platform.runLater(() -> controller.usersViewBox.getChildren().add(u));
                                    }
                        }
                        else
                            for(Node item : controller.usersViewBox.getChildren())
                            {
                                if(item instanceof UserItem u)
                                    if(u.usr_id == stat_cmd.user.id)
                                    {
                                        u.setStatus(false);
                                        Platform.runLater(() -> controller.usersViewBox.getChildren().remove(u));
                                        Platform.runLater(() -> controller.offlineUsersViewBox.getChildren().add(u));
                                    }
                            }
                    }
                    else if(cmd instanceof UserListCommand list_cmd)
                    {
                        for(User item : list_cmd.list)
                        {
                            UserItem user = new UserItem(userItemMouseEvent, item.id, item.name, true);
                            ResourceMgr.requestFile(item, user);
                            Platform.runLater(() -> controller.usersViewBox.getChildren().add(user));
                        }
                    }
                    else if(cmd instanceof MessageListCommand listCommand)
                    {
                        controller.selected_conv_id = listCommand.Conversation_id;
                        Platform.runLater(() -> controller.messageViewBox.getChildren().clear());
                        for(Message item : listCommand.list)
                        {
                            MessageBubble tmp = new MessageBubble(item);
                            ResourceMgr.requestFile(item, tmp);
                            Platform.runLater(() -> controller.messageViewBox.getChildren().add(tmp));
                        }
                    }
                    else if(cmd instanceof ClientNotifyMessageCommand ncmd)
                    {
                        if(controller.selected_user_id == ncmd.msg.sender_id)
                        {
                            MessageBubble bub = new MessageBubble(ncmd.msg);
                            ResourceMgr.requestFile(ncmd.msg, bub);
                            Platform.runLater(() -> controller.messageViewBox.getChildren().add(bub));
                        }
                    }
                    else if(cmd instanceof MessageConfirmationCommand confcmd)
                    {
                        if(controller.selected_conv_id == confcmd.msg.conv_id)
                        {
                            MessageBubble bub = new MessageBubble(confcmd.msg);
                            ResourceMgr.requestFile(confcmd.msg, bub);
                            Platform.runLater(() -> controller.messageViewBox.getChildren().add(bub));
                        }
                    }
                    else if(cmd instanceof ResponseUsersListCommand responseCmd)
                    {
                        for(User item : responseCmd.list)
                        {
                            UserItem user = new UserItem(userItemMouseEvent, item.id, item.name, item.isOnline);
                            ResourceMgr.requestFile(item, user);

                            if(item.isOnline)
                            {
                                Platform.runLater(() -> controller.usersViewBox.getChildren().add(user));
                            }
                            else
                            {
                                Platform.runLater(() -> controller.offlineUsersViewBox.getChildren().add(user));
                            }

                        }
                    }
                    else if(cmd instanceof ResponseImageChangeCommand response)
                    {
                        if(response.result)
                        {
                            Platform.runLater(() ->Utils.showAlert(Alert.AlertType.INFORMATION, "Image Changed", "Image Changed/Removed Successfully."));
                            try{new RequestProfileImageCommand().SendCommand(socket);}catch(Exception ignored){}
                        }
                        else Platform.runLater(() ->Utils.showAlert(Alert.AlertType.ERROR, "Error", "Can't Change/Remove Image."));
                    }
                    else if(cmd instanceof ResponeProfileImageCommand img_cmd && img_cmd.status)
                    {
                        if(img_cmd.status && img_cmd.data != null)
                        {
                            if(img_cmd.owner_id == null)
                            {
                                Platform.runLater(() ->controller.initializeCircularImage(controller.CurrentUserImg, 70));
                                Platform.runLater(() ->controller.CurrentUserImg.setImage(new Image(new ByteArrayInputStream(img_cmd.data))));
                            }
                            else
                            {
                                ResourceMgr.responseHandler(img_cmd);
                            }

                        }
                    }
                    else if(cmd instanceof ResponseFileRequestCommand response_cmd)
                    {
                        ResourceMgr.responseHandler(response_cmd);
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
