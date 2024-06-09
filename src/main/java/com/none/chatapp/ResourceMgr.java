package com.none.chatapp;

import com.none.chatapp_commands.*;
import javafx.application.Platform;
import javafx.scene.Node;

import java.net.Socket;
import java.util.*;

public class ResourceMgr {
    private Map<String, Node> queue = new HashMap<>();
    private static final String USER_IMAGE_MAGIC = "USER_IMG";
    private static final String USER_ITEM_MAGIC = "USER_ITEM";
    HandlerThread handlerThread;

    public ResourceMgr(HandlerThread thread)
    {
        handlerThread = thread;
    }

    public void requestFile(Message msg, MessageBubble bubble)
    {
        queue.put(String.valueOf(msg.sender_id) + msg.content, bubble);
        try {
            new RequestFileCommand(msg.content, msg.sender_id).SendCommand(handlerThread.socket);
        }
        catch(Exception e)
        {
            switch (msg.type)
            {
                case Message.Type.image -> bubble.setImage(handlerThread.controller, null);
                case Message.Type.audio -> bubble.setAudio(handlerThread.controller, null);
                case Message.Type.attachment -> bubble.saveAttachment(null);
            }
        }
    }

    public void requestFile(User usr, UserItem usr_item)
    {
        queue.put(USER_IMAGE_MAGIC + String.valueOf(usr.id), usr_item);
        try {
            new RequestProfileImageCommand(usr.id).SendCommand(handlerThread.socket);
        }
        catch(Exception ignored)
        {
            /* Nothing */
        }
    }

    public void addUserItem(User usr, UserItem usr_item)
    {
        queue.put(USER_ITEM_MAGIC + String.valueOf(usr.id), usr_item);
    }

    public void responseHandler(ResponseFileRequestCommand response)
    {
        String key = String.valueOf(response.owner_id) + response.filename;
        Node caller = queue.get(key);
        if(caller instanceof MessageBubble bubble)
        {
            if (response.status)
                switch (bubble.msg.type)
                {
                    case Message.Type.image -> bubble.setImage(handlerThread.controller, response.data);
                    case Message.Type.audio -> {bubble.setAudio(handlerThread.controller, response.data);response.data = null;}
                    case Message.Type.attachment -> bubble.saveAttachment(response.data);
                }
            else
                switch (bubble.msg.type)
                {
                    case Message.Type.image -> bubble.setImage(handlerThread.controller, null);
                    case Message.Type.audio -> bubble.setAudio(handlerThread.controller, null);
                    case Message.Type.attachment -> bubble.saveAttachment(null);
                }
        }

        try{
            queue.remove(key);
        }catch (Exception e){}

    }

    public void responseHandler(ResponeProfileImageCommand response)
    {
        String key = USER_IMAGE_MAGIC + String.valueOf(response.owner_id);
        Node caller = queue.get(key);
        if(caller instanceof UserItem usr && response.data != null)
        {
            usr.setImage(response.data);
        }

        try{
            queue.remove(key);
        }catch (Exception e){}

    }

    public void setUserItemStatus(UsersController controller, User usr, boolean status)
    {
        String key = USER_ITEM_MAGIC + String.valueOf(usr.id);
        Node item = queue.get(key);
        if(item instanceof UserItem user)
        {
            user.setStatus(status);
            if(status) Platform.runLater(() -> controller.usersViewBox.getChildren().add(user));
            else Platform.runLater(() -> controller.offlineUsersViewBox.getChildren().add(user));
        }
    }
}
