package com.none.chatapp_commands;

import java.io.Serializable;

public class ResponseDeleteAccountCommand extends ServerCommand implements Serializable {
    public boolean status;
    public ResponseDeleteAccountCommand(boolean status)
    {
        this.status = status;
    }
}
