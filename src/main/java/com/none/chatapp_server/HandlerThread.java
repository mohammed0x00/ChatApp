package com.none.chatapp_server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import com.none.chatapp_commands.*;

public class HandlerThread extends Thread {
    private Socket socket;

    public HandlerThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        int current_user_id;
        try {
            while(true)
            {
                ServerCommand cmd = ServerCommand.WaitForCommand(socket);
                if(cmd.CMD_Number == ServerCommand.COMMAND_LOGIN) {
                    LoginCommand c = (LoginCommand) cmd;
                    boolean isLoginSuccessful = Utils.handleLogin(c);
                    // Send login response back to client
                    new ObjectOutputStream(socket.getOutputStream()).writeObject(new LoginResponseCommand(isLoginSuccessful));
                    //System.out.println(loginResponse);
                }

            }
        } catch (java.io.EOFException e) {
            System.out.println("Client disconnected");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}