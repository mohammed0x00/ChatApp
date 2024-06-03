package com.none.chatapp_commands;

import java.io.Serializable;

public class SignUpCommand extends ServerCommand implements Serializable {
    public String username;
    public String password;
    public String e_mail;
    public int age;
    public boolean gender;

    public SignUpCommand(String uname, String pwd, String mail, int p_age, boolean sex)
    {
        username = uname;
        password = pwd;
        e_mail = mail;
        age = p_age;
        gender = sex;
    }

}
