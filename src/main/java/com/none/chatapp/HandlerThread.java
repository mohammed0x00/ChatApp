package com.none.chatapp;

import com.none.chatapp_commands.*;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

public class HandlerThread extends Thread{
    public static Socket socket;
    static UsersController controller;
    static EventHandler<MouseEvent> userItemMouseEvent;

    private static final Thread me = new Thread(new Runnable() {
        @Override
        public void run()
        {
            while(true)
            {
                try {
                    ServerCommand cmd = ServerCommand.WaitForCommand(socket);
                    if(cmd instanceof UserStatusCommand stat_cmd)
                    {
                        if(stat_cmd.status == UserStatusCommand.Stat.ONLINE)
                        {
                            UserItem user = new UserItem(userItemMouseEvent, stat_cmd.user.id, stat_cmd.user.name, stat_cmd.user.isOnline, null);
                            Platform.runLater(() -> controller.usersViewBox.getChildren().add(user));
                        }
                        else for(Node i : controller.usersViewBox.getChildren())
                        {
                            UserItem item = (UserItem) i;
                            if(item.usr_id == stat_cmd.user.id)
                            {
                                Platform.runLater(() -> controller.usersViewBox.getChildren().remove(item));
                            }
                        }
                    }
                    else if(cmd instanceof UserListCommand list_cmd)
                    {
                        for(User item : list_cmd.list)
                        {
                            UserItem user = new UserItem(userItemMouseEvent, item.id, item.name, true, null);
                            Platform.runLater(() -> controller.usersViewBox.getChildren().add(user));
                        }
                    }
                    else if(cmd instanceof MessageListCommand listCommand)
                    {
                        Platform.runLater(() -> controller.messageViewBox.getChildren().clear());
                        for(Message item : listCommand.list)
                        {
                            MessageBubble tmp = new MessageBubble(item);
                            Platform.runLater(() -> controller.messageViewBox.getChildren().add(tmp));
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
