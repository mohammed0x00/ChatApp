package com.none.chatapp_commands;

import java.io.Serializable;
import java.sql.Timestamp;

public class Message implements Serializable {
    public enum Type {
        text,
        audio,
        video,
        attachment
    };

    public int id;
    public int conv_id;
    public int sender_id;
    public String content;
    public boolean seen;
    public Type type;
    public Timestamp sent_at;

    public void setType(String s)
    {
        type = Type.valueOf(s);
    }

    public String getType()
    {
        return type.toString();
    }

}
