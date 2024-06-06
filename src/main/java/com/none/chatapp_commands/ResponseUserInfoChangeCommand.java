package com.none.chatapp_commands;

import java.io.Serializable;

public class ResponseUserInfoChangeCommand extends ServerCommand implements Serializable {
    public boolean status;
    public String err_msg;

    public enum ResponseType {
        CHANGE_USER_NAME,
        CHANGE_STATUS_MSG,
        CHANGE_PASSWORD
    }

    public ResponseType responseType;

    public ResponseUserInfoChangeCommand()
    {

    }

    public ResponseUserInfoChangeCommand(ResponseType type, boolean stat)
    {
        status = stat;
        responseType = type;
    }

    public ResponseUserInfoChangeCommand(ResponseType type, boolean stat, String err)
    {
        status = stat;
        err_msg = err;
        responseType = type;
    }

}
