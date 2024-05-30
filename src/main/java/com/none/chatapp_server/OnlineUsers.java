package com.none.chatapp_server;

import com.none.chatapp_commands.*;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class OnlineUsers{

    public static ArrayList<HandlerThread> onlineUsers = new ArrayList<>();

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

    public static void notifyUserMessage(int receiver, Message msg) throws IOException {
        try
        {
            for (HandlerThread user : onlineUsers)
                if(user.data.id == receiver)
                {
                    new ClientNotifyMessageCommand(msg).SendCommand(user.socket);
                }
        }
        catch (Exception e)
        {
            throw e;
        }

    }

    public static void changeUsersListStatus(ArrayList<User> lst)
    {
        for(User item : lst)
        {
            item.isOnline = false;
            for(HandlerThread onlineUser : onlineUsers)
            {
                if(item.id == onlineUser.data.id)
                {
                    item.isOnline = true;
                    break;
                }
            }
        }
    }

}
