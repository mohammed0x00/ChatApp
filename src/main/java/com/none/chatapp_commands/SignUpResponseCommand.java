package com.none.chatapp_commands;

import java.io.Serializable;

public class SignUpResponseCommand extends ServerCommand implements Serializable {
    public static final int RESPONSE_SUCCESSFUL = 0x0110;
    public static final int RESPONSE_EXISTS = 0x0220;
    public static final int RESPONSE_UNEXPECTED = 0x0330;
    public static final int RESPONSE_AGE_INVALID = 0x0440;
    public static final int RESPONSE_EMPTY_FIELDS = 0x0550;

    public int err_code;

    public SignUpResponseCommand(int code)
    {
        err_code = code;
    }

}
