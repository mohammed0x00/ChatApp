package com.none.chatapp_commands;

import java.io.Serializable;

public class SendMessageCommand extends ServerCommand implements Serializable {
    public Message msg = new Message();
    public byte[] file_data;
    public String extension;
    public SendMessageCommand(Message m)
    {
        msg.sender_id = m.sender_id;
        msg.content = m.content;
        msg.seen = m.seen;
        msg.type = m.type;
        msg.sent_at = m.sent_at;
        msg.conv_id = m.conv_id;
        msg.id = m.conv_id;
    }

    public SendMessageCommand(Message m, byte[] f, String ext)
    {
        msg.sender_id = m.sender_id;
        msg.content = m.content;
        msg.seen = m.seen;
        msg.type = m.type;
        msg.sent_at = m.sent_at;
        msg.conv_id = m.conv_id;
        msg.id = m.conv_id;
        file_data = new byte[f.length];
        System.arraycopy(f, 0, file_data, 0, f.length);
        extension = ext;
    }
}
