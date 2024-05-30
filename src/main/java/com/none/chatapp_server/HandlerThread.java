package com.none.chatapp_server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

import com.none.chatapp_commands.*;
import javafx.util.Pair;

public class HandlerThread extends Thread {
    public Socket socket;
    User data;

    public HandlerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            while(true)
            {
                ServerCommand cmd = ServerCommand.WaitForCommand(socket);
                if(cmd instanceof LoginCommand loginCMD) {
                    User tmp = Utils.handleLogin(loginCMD);
                    // Send login response back to client
                    if (tmp != null)
                    {
                        data = tmp;
                        new LoginResponseCommand(LoginResponseCommand.RESPONSE_SUCCESSFUL).SendCommand(socket);
                        OnlineUsers.add(this);
                    }
                    else
                    {
                        new LoginResponseCommand(LoginResponseCommand.RESPONSE_INVALID).SendCommand(socket);
                    }
                }
                else if(cmd instanceof MessagesListRequestCommand reqCmd)
                {

                    Pair <Integer, ArrayList<Message>> list = DatabaseController.loadConversation(data.id, reqCmd.user_id);
                    new MessageListCommand(list.getKey(), list.getValue()).SendCommand(socket);
                }
                else if (cmd instanceof SendMessageCommand sndCmd)
                {
                    Utils.SendMessageToUser(this, sndCmd.msg);
                }
                else if(cmd instanceof RequestUsersListCommand)
                {
                    ArrayList<User> list = DatabaseController.getUsersList(this);
                    assert list != null;
                    OnlineUsers.changeUsersListStatus(list);
                    new ResponseUsersListCommand(list).SendCommand(socket);
                }

            }
        }
        catch (IOException e) {
            System.out.println("Client disconnected: " + e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            OnlineUsers.remove(this);

        }
    }
}