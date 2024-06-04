package com.none.chatapp_commands;

import java.io.Serializable;

public class ResponseFileRequestCommand extends ServerCommand implements Serializable {
    public byte[] data;
    public boolean status;
    public ResponseFileRequestCommand(boolean stat, byte[] d)
    {
        data = new byte[d.length];
        System.arraycopy(d, 0, data, 0, d.length);
        status = stat;
    }

}
