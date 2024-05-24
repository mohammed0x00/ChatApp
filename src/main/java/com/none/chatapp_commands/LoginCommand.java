package com.none.chatapp_commands;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public class LoginCommand extends ServerCommand implements Serializable {
    public String UserName;
    public String UserPassword;

    public LoginCommand(String user_name, String password)
    {
        this.UserName = user_name;
        this.UserPassword = password;
        this.CMD_Number = COMMAND_LOGIN;
    }

}
