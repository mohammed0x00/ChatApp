package com.none.chatapp_commands;

import java.io.Serializable;

public class ResponseActionCommand extends ServerCommand implements Serializable {
    public Boolean result;

    public ResponseActionCommand()
    {
        result = true;
    }

    public ResponseActionCommand(boolean res)
    {
        result = res;
    }
}
