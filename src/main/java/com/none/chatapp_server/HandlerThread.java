package com.none.chatapp_server;

import java.io.*;
import java.net.Socket;

import com.none.chatapp_commands.*;

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
                        OnlineUsers.SendListToSocket(this);
                    }
                    else
                    {
                        new LoginResponseCommand(LoginResponseCommand.RESPONSE_INVALID).SendCommand(socket);
                    }

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