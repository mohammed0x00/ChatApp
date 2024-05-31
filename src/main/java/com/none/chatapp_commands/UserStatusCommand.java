package com.none.chatapp_commands;

import java.io.Serializable;

public class UserStatusCommand extends ServerCommand implements Serializable {
    public Stat status;
    public User user = new User();
    public enum Stat
    {
        ONLINE,
        OFFLINE
    }

    public UserStatusCommand(User u, Stat s)
    {
        user.id = u.id;
        user.name = u.name;
        user.image = u.image;
        user.status_msg = u.status_msg;
        user.age = u.age;
        user.isOnline = u.isOnline;
        status = s;
    }
}
