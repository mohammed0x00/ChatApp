package com.none.chatapp_commands;

import java.io.Serializable;

public class ResponseImageChangeCommand extends ServerCommand implements Serializable {
    public Boolean result;

    public ResponseImageChangeCommand(boolean res)
    {
        result = res;
    }
}
