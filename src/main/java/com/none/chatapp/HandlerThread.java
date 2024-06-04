package com.none.chatapp;

import com.none.chatapp_commands.*;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

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
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            while(true)
            {
                try {
                    ServerCommand cmd = ServerCommand.WaitForCommand(socket, 0);
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
                            if(item.name.equals("Sarah Donald"))
                                imageUrl = imageUrl2;
                            else
                                imageUrl = imageUrl1;
                            UserItem user = new UserItem(userItemMouseEvent, item.id, item.name, true, imageUrl);
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
                            Platform.runLater(() -> controller.messageViewBox.getChildren().add(tmp));
                        }
                    }
                    else if(cmd instanceof ClientNotifyMessageCommand ncmd)
                    {
                        if(controller.selected_user_id == ncmd.msg.sender_id)
                        {
                            Platform.runLater(() -> controller.messageViewBox.getChildren().add(new MessageBubble(ncmd.msg)));
                        }
                    }
                    else if(cmd instanceof MessageConfirmationCommand confcmd)
                    {
                        if(controller.selected_conv_id == confcmd.msg.conv_id)
                        {
                            Platform.runLater(() -> controller.messageViewBox.getChildren().add(new MessageBubble(confcmd.msg)));
                        }
                    }
                    else if(cmd instanceof ResponseUsersListCommand responseCmd)
                    {
                        for(User item : responseCmd.list)
                        {
                            if(item.name.equals("Sarah Donald"))
                                imageUrl = imageUrl2;
                            else
                                imageUrl = imageUrl1;
                            UserItem user = new UserItem(userItemMouseEvent, item.id, item.name, item.isOnline, imageUrl);

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
