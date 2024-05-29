package com.none.chatapp_commands;

import java.io.Serializable;

public class MessagesListRequestCommand extends ServerCommand implements Serializable {
    public int user_id;
    public MessagesListRequestCommand(int id)
    {
        user_id = id;
    }
}
