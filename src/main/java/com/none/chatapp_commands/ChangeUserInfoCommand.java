package com.none.chatapp_commands;

import java.io.Serializable;

public class ChangeUserInfoCommand extends ServerCommand implements Serializable {
    public ChangeUserInfoCommand() {

    }

    public static class CHANGE_USER_NAME extends ChangeUserInfoCommand implements Serializable{
        public String Username;
        public CHANGE_USER_NAME(String username)
        {
            super();
            Username = username;
        }
    }

    public static class CHANGE_STATUS_MSG extends ChangeUserInfoCommand implements Serializable{
        public String message;
        public CHANGE_STATUS_MSG(String msg)
        {
            super();
            message = msg;
        }
    }

    public static class CHANGE_PASSWORD extends ChangeUserInfoCommand implements Serializable{
        public String old_password;
        public String new_password;
        public CHANGE_PASSWORD(String old_passwd, String new_passwd)
        {
            super();
            old_password = old_passwd;
            new_password = new_passwd;
        }
    }

}
