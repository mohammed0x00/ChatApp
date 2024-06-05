package com.none.chatapp_commands;

import java.io.Serializable;

public class UploadFileCommand extends ServerCommand implements Serializable {
    public String extension;
    public byte[] data;

    public UploadFileCommand(byte[] d, String ext)
    {
        extension = ext;
        data = d;
    }
}
