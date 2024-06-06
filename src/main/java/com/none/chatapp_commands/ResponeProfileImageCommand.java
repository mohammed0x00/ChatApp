package com.none.chatapp_commands;

import java.io.Serializable;

public class ResponeProfileImageCommand extends ResponseFileRequestCommand implements Serializable {
    public ResponeProfileImageCommand(boolean stat, byte[] d) {
        super(null, null, stat, d);
    }

    public ResponeProfileImageCommand(int owner, boolean stat, byte[] d) {
        super(null, owner, stat, d);
    }
}
