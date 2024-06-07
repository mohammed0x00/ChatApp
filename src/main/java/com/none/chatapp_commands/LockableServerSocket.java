package com.none.chatapp_commands;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketImpl;

public class LockableServerSocket extends ServerSocket {
    public LockableServerSocket(int port) throws IOException {
        super(port);
    }

    @Override
    public LockableSocket accept() throws IOException {
        if (isClosed())
            throw new SocketException("Socket is closed");
        if (!isBound())
            throw new SocketException("Socket is not bound yet");
        LockableSocket s = new LockableSocket((SocketImpl) null);
        implAccept(s);
        return s;
    }
}
