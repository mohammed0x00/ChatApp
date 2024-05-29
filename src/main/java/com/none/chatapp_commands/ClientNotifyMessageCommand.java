package com.none.chatapp_commands;

import java.io.Serializable;

public class ClientNotifyMessageCommand extends ServerCommand implements Serializable {
    public Message msg = new Message();

    public ClientNotifyMessageCommand(Message m)
    {
        msg.sender_id = m.sender_id;
        msg.content = m.content;
        msg.seen = m.seen;
        msg.type = m.type;
        msg.sent_at = m.sent_at;
        msg.conv_id = m.conv_id;
        msg.id = m.conv_id;
    }
}
