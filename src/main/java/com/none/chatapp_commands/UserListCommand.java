package com.none.chatapp_commands;

import java.io.Serializable;
import java.util.ArrayList;

public class UserListCommand extends ServerCommand implements Serializable {
    public ArrayList<User> list = new ArrayList<>();
}
