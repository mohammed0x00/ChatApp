package com.none.chatapp_commands;

import java.io.Serializable;

public class LoginResponseCommand extends ServerCommand implements Serializable {
    public boolean isSuccess;

    public LoginResponseCommand(boolean isSuccess) {
        this.isSuccess = isSuccess;
        this.CMD_Number = ServerCommand.COMMAND_LOGIN;
    }
}
