package com.none.chatapp;

import com.none.chatapp_commands.*;
import javafx.scene.Node;

import java.util.*;

public class ResourceMgr {
    private static Map<String, Node> queue = new HashMap<>();
    private static String USER_IMAGE_MAGIC = "USER_IMG";

    public static void requestFile(Message msg, MessageBubble bubble)
    {
        if (msg.type == Message.Type.image)
        {
            queue.put(String.valueOf(msg.sender_id) + msg.content, bubble);
            try {
                new RequestFileCommand(msg.content, msg.sender_id).SendCommand(HandlerThread.socket);
            }
            catch(Exception e)
            {
                bubble.setImage(null);
            }

        }
    }

    public static void requestFile(User usr, UserItem usr_item)
    {
        queue.put(USER_IMAGE_MAGIC + String.valueOf(usr.id), usr_item);
        try {
            new RequestProfileImageCommand(usr.id).SendCommand(HandlerThread.socket);
        }
        catch(Exception ignored)
        {
            /* Nothing */
        }
    }



    public static void responseHandler(ResponseFileRequestCommand response)
    {
        String key = String.valueOf(response.owner_id) + response.filename;
        Node caller = queue.get(key);
        if(caller instanceof MessageBubble bubble)
        {
            if (response.status) bubble.setImage(response.data);
            else bubble.setImage(null);
        }

        try{
            queue.remove(key);
        }catch (Exception e){}

    }

    public static void responseHandler(ResponeProfileImageCommand response)
    {
        String key = USER_IMAGE_MAGIC + String.valueOf(response.owner_id);
        Node caller = queue.get(key);
        if(caller instanceof UserItem usr)
        {
            usr.setImage(response.data);
        }

        try{
            queue.remove(key);
        }catch (Exception e){}

    }
}
