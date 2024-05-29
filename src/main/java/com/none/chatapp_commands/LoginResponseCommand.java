package com.none.chatapp_commands;

import java.io.Serializable;

public class LoginResponseCommand extends ServerCommand implements Serializable {
    public static final boolean RESPONSE_SUCCESSFUL = true;
    public static final boolean RESPONSE_INVALID = false;

    public boolean isSuccess;

    public LoginResponseCommand(boolean status) {
        this.isSuccess = status;
    }
}
