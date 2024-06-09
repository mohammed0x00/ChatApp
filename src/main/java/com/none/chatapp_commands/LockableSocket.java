package com.none.chatapp_commands;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketImpl;
import java.util.concurrent.locks.ReentrantLock;

public class LockableSocket extends Socket {
    private final ReentrantLock socketLock;

    public LockableSocket() {
        super();
        socketLock = new ReentrantLock();
    }


    public LockableSocket(SocketImpl socket) throws SocketException {
        super(socket);
        socketLock = new ReentrantLock();
    }

    // Acquire the lock
    public void lockSocket() {
        socketLock.lock();
    }

    // Release the lock
    public void unlockSocket() {
        socketLock.unlock();
    }

    // Check if the lock is held by the current thread
    public boolean isLocked() {
        return socketLock.isHeldByCurrentThread();
    }

    // Check if the lock is held by any thread
    public boolean isLockedByOtherThread() {
        return socketLock.isLocked() && !isLocked();
    }

    // Close the socket and release the lock
    @Override
    public void close() throws IOException {
        try {
            super.close();
            unlockSocket();
        } catch (Exception ignored) {}
    }
}
