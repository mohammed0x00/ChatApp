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

    public void SendMessageToUser(HandlerThread thread, Message msg)
    {
        try {
            Integer receiver_id = DatabaseController.sendMessage(msg);
            msg.sender_id = thread.data.id;
            OnlineUsers.notifyUserMessage(receiver_id, msg);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

}