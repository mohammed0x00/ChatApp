package com.none.chatapp_commands;

import java.io.Serializable;

public class ResponseUserDetailsCommand extends ServerCommand implements Serializable {
    public User me;

    public ResponseUserDetailsCommand(User usr)
    {
        me = usr;
    }
}
