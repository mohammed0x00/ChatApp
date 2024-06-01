package com.none.chatapp_commands;

import com.none.chatapp_server.HandlerThread;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public abstract class ServerCommand implements Serializable {

    public static ServerCommand WaitForCommand(Socket socket) throws IOException, ClassNotFoundException {
        ServerCommand cmd = (ServerCommand) (new ObjectInputStream(socket.getInputStream())).readObject();
        return cmd;
    }

    public void SendCommand(Socket socket) throws IOException {
        new ObjectOutputStream(socket.getOutputStream()).writeObject(this);
    }

}