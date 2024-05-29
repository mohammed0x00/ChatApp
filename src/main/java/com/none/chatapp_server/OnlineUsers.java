package com.none.chatapp_server;

import com.none.chatapp_commands.User;
import com.none.chatapp_commands.UserListCommand;
import com.none.chatapp_commands.UserStatusCommand;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class OnlineUsers{

    private static ArrayList<HandlerThread> onlineUsers = new ArrayList<>();

    public static void SendListToSocket(HandlerThread thread) throws IOException {
        UserListCommand cmd = new UserListCommand();
        for(HandlerThread i : onlineUsers)
        {
            if(i != thread)
            {
                cmd.list.add(i.data);
            }
        }
        try
        {
            cmd.SendCommand(thread.socket);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    public static void add(HandlerThread thread)
    {
        UserStatusCommand cmd = new UserStatusCommand(thread.data, UserStatusCommand.Stat.ONLINE);
        for (HandlerThread i : onlineUsers)
        {
            try {
                cmd.SendCommand(i.socket);
            } catch (IOException e) {
                // Ok, No Problem
            }
        }
        onlineUsers.add(thread);
    }

    public static void remove(HandlerThread thread)
    {
        onlineUsers.remove(thread);
        UserStatusCommand cmd = new UserStatusCommand(thread.data, UserStatusCommand.Stat.OFFLINE);
        for (HandlerThread i : onlineUsers)
        {
            try {
                cmd.SendCommand(i.socket);
            } catch (IOException e) {
                // Ok, No Problem
            }
        }
    }

}
