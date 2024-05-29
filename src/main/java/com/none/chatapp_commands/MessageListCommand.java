package com.none.chatapp_commands;

import java.io.Serializable;
import java.util.ArrayList;

public class MessageListCommand extends ServerCommand implements Serializable {
    public ArrayList<Message> list = new ArrayList<>();
    public int Conversation_id;
    public MessageListCommand(int conv_id, ArrayList<Message> lst)
    {
        Conversation_id = conv_id;
        list.addAll(lst);
    }
}
