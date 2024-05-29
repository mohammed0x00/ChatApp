package com.none.chatapp_server;

import com.none.chatapp_commands.*;

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

}