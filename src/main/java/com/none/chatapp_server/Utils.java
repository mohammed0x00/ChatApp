package com.none.chatapp_server;

import com.none.chatapp_commands.*;
import javafx.fxml.FXML;

import java.util.HashMap;
import java.util.Map;

public class Utils {

    public static boolean handleLogin(LoginCommand loginCommand) throws Exception {
        DatabaseController.connect();
        try {
            Integer userId = DatabaseController.validateUser(loginCommand.UserName, loginCommand.UserPassword);
            if (userId != null) {

                System.out.println("Login successful. User ID: " + userId);
                return true;
            } else {
                System.out.println("Login failed.");
                return false;

            }
        } catch (Exception e) {
            e.printStackTrace();
            // An error occurred during login
            System.out.println("Login failed due to an error.");
            return false;
        }

    }

}