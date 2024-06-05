package com.none.chatapp_commands;

import java.io.Serializable;

public class ResponseFileRequestCommand extends ServerCommand implements Serializable {
    public byte[] data;
    public boolean status;
    public String filename;
    public Integer owner_id = null;

    public ResponseFileRequestCommand(String f_name, int owner, boolean stat, byte[] d)
    {
        if(d != null)
        {
            data = new byte[d.length];
            System.arraycopy(d, 0, data, 0, d.length);
        }
        status = stat;
        filename = f_name;
        owner_id = owner;
    }

}
