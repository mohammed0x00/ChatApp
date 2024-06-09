package com.none.chatapp_commands;

import java.io.Serializable;

public class DeleteAccountCommand extends ServerCommand implements Serializable {
    public String password;
    public DeleteAccountCommand(String password)
    {
        this.password = password;
    }
}
