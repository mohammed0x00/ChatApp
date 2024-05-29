package com.none.chatapp;

import com.none.chatapp_commands.ServerCommand;
import com.none.chatapp_commands.User;
import com.none.chatapp_commands.UserListCommand;
import com.none.chatapp_commands.UserStatusCommand;
import javafx.application.Platform;
import javafx.scene.Node;

import java.io.IOException;
import java.net.Socket;

public class HandlerThread extends Thread{
    static Socket socket;
    static UsersController controller;

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
                            UserItem user = new UserItem(null, stat_cmd.user.id, stat_cmd.user.name, true, null);
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
                            UserItem user = new UserItem(null, item.id, item.name, true, null);
                            Platform.runLater(() -> controller.usersViewBox.getChildren().add(user));
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
