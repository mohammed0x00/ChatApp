package com.none.chatapp_commands;

import java.io.Serializable;

public class ResponeProfileImageCommand extends ResponseFileRequestCommand implements Serializable {
    public ResponeProfileImageCommand(boolean stat, byte[] d) {
        super(null, 0, stat, d);
    }
}
