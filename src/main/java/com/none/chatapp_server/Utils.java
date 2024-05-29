package com.none.chatapp_server;

import com.none.chatapp_commands.*;

import java.io.IOException;
import java.sql.SQLException;

public class Utils {

    public static User handleLogin(LoginCommand loginCommand) {

        try {
            Integer userId = DatabaseController.validateUser(loginCommand.UserName, loginCommand.UserPassword);
            if (userId != null) {
                System.out.println("Login successful. User ID: " + userId);
                User details = DatabaseController.getUserDetails(userId);
                return details;
            } else {
                System.out.println("Login failed.");
                return null;

            }
        } catch (Exception e) {
            e.printStackTrace();
            // An error occurred during login
            System.out.println("Login failed due to an error.");
            return null;
        }

    }

    public static void SendMessageToUser(HandlerThread thread, Message msg)
    {
        try {
            msg.sender_id = thread.data.id;
            Integer receiver_id = DatabaseController.sendMessage(msg);
            new MessageConfirmationCommand(msg).SendCommand(thread.socket);
            OnlineUsers.notifyUserMessage(receiver_id, msg);
        }
        catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }


    }

}