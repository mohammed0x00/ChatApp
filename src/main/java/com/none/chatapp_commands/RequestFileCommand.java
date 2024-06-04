package com.none.chatapp_commands;

import java.io.Serializable;

public class RequestFileCommand extends ServerCommand implements Serializable {
    public String filename;
    public Integer owner_id = null;

    public RequestFileCommand(String f_name)
    {
        filename = f_name;
    }

    public RequestFileCommand(String f_name, Integer owner)
    {
        filename = f_name;
        owner_id = owner;
    }
}
