package com.none.chatapp;

import com.none.chatapp_commands.*;
import javafx.application.Platform;
import javafx.scene.Node;

import java.util.*;

public class ResourceMgr {
    private static Map<String, Node> queue = new HashMap<>();
    private static final String USER_IMAGE_MAGIC = "USER_IMG";
    private static final String USER_ITEM_MAGIC = "USER_ITEM";

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

    public static void addUserItem(User usr, UserItem usr_item)
    {
        queue.put(USER_ITEM_MAGIC + String.valueOf(usr.id), usr_item);
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

    public static void setUserItemStatus(UsersController controller, User usr, boolean status)
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
