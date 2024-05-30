package com.none.chatapp_commands;

import java.io.Serializable;
import java.util.ArrayList;

public class ResponseUsersListCommand extends ServerCommand implements Serializable {
    public ArrayList<User> list = new ArrayList<>();

    public ResponseUsersListCommand(ArrayList<User> lst)
    {
        list.addAll(lst);
    }
}
