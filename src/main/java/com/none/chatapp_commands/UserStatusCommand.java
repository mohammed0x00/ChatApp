package com.none.chatapp_commands;

import java.io.Serializable;

public class UserStatusCommand extends ServerCommand implements Serializable {
    public Stat status;
    public User user;
    public enum Stat
    {
        ONLINE,
        OFFLINE
    }

    public UserStatusCommand(User u, Stat s)
    {
        user = u;
        status = s;
    }
}
