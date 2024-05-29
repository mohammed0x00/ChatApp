package com.none.chatapp_commands;

import java.io.Serializable;
import java.util.ArrayList;

public class MessageListCommand extends ServerCommand implements Serializable {
    public ArrayList<Message> list = new ArrayList<>();
    public MessageListCommand(ArrayList<Message> l)
    {
        list.addAll(l);
    }
}
