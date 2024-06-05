package com.none.chatapp;

import com.none.chatapp_commands.Message;
import com.none.chatapp_commands.RequestFileCommand;
import com.none.chatapp_commands.ResponseFileRequestCommand;
import javafx.scene.Node;

import java.util.*;

public class ResourceMgr {
    private static Map<String, Node> queue = new HashMap<>();

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
}
