package com.none.chatapp_commands;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public abstract class ServerCommand implements Serializable {

    public static ServerCommand WaitForCommand(Socket socket, int timeout) throws IOException, ClassNotFoundException {
        if(timeout > 0)
        {
            socket.setSoTimeout(timeout * 1000);
        }
        else
        {
            socket.setSoTimeout(0);
        }
        ServerCommand cmd = (ServerCommand) (new ObjectInputStream(socket.getInputStream())).readObject();
        socket.setSoTimeout(0);
        return cmd;
    }

    public static ServerCommand WaitForCommand(Socket socket) throws IOException, ClassNotFoundException {
        socket.setSoTimeout(0);
        ServerCommand cmd = (ServerCommand) (new ObjectInputStream(socket.getInputStream())).readObject();
        return cmd;
    }

    public void SendCommand(Socket socket) throws IOException {
        new ObjectOutputStream(socket.getOutputStream()).writeObject(this);
    }

    public void SendCommand(LockableSocket socket) throws IOException {
        while (socket.isLocked() || socket.isLockedByOtherThread());
        socket.lockSocket();
        new ObjectOutputStream(socket.getOutputStream()).writeObject(this);
        socket.unlockSocket();
    }

}
