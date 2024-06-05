package com.none.chatapp_commands;

import java.io.Serializable;

public class ChangeUserImageCommand extends UploadFileCommand implements Serializable {
    public static final boolean IMAGE_CHANGE = false;
    public static final boolean IMAGE_DELETE = true;

    public boolean remove_image = false;

    public ChangeUserImageCommand(byte[] d, String ext) {
        super(d, ext);
    }

    public ChangeUserImageCommand(boolean rm_image) {
        super(new byte[1], null);
        remove_image = rm_image;
    }

}
