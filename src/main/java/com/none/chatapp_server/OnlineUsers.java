package com.none.chatapp_server;

import com.none.chatapp_commands.*;

import java.io.IOException;
import java.util.ArrayList;

public class OnlineUsers{

    public static ArrayList<HandlerThread> onlineUsers = new ArrayList<>();
    public static ArrayList<Message> broadcastMessages = new ArrayList<>();

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

    public void broardcastMessage(String msg)
    {
        Message new_msg = new Message();
        new_msg.content = msg;
        new_msg.id = -1;
        new_msg.type = Message.Type.text;
        new_msg.conv_id = -1;
        broadcastMessages.add(new_msg);
        for (HandlerThread usr : onlineUsers)
        {
            try {
                new ClientNotifyMessageCommand(new_msg).SendCommand(usr.socket);
            }catch (Exception ignored){}
        }
    }
    public void notifyBroadcastedMessages(HandlerThread thread)
    {
        for (Message msg : broadcastMessages) try
        {
            new ClientNotifyMessageCommand(msg).SendCommand(thread.socket);
        }
        catch (Exception ignored){}
    }

}
