package com.none.chatapp_commands;

import java.io.Serializable;

public class RequestProfileImageCommand extends ServerCommand implements Serializable {
    public Integer user_id = null;

    public RequestProfileImageCommand()
    {
        user_id = null;
    }

    public RequestProfileImageCommand(int usr_id)
    {
        user_id = usr_id;
    }
}
