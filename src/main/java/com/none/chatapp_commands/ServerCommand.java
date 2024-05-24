package com.none.chatapp_commands;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public abstract class ServerCommand implements Serializable {
    public static final int COMMAND_LOGIN = 0x2154;

    public int CMD_Number;

    public static ServerCommand WaitForCommand(Socket socket) throws IOException, ClassNotFoundException {
        ServerCommand cmd = (ServerCommand) (new ObjectInputStream(socket.getInputStream())).readObject();
        return cmd;
    }



}
