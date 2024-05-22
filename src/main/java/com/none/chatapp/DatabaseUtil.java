package com.none.chatapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DatabaseUtil {

    private static final String URL = "jdbc:mysql://localhost:3306/chat_app";
    private static final String USER = "Admin";
    private static final String PASSWORD = "Admin";

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public Integer validateUser(String identifier, String passwordHash) {
        String query = "SELECT LoginWithEmailOrUsername(?, ?) AS user_id";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, identifier);
            stmt.setString(2, passwordHash);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("user_id"); // Return user ID if a match is found
            } else {
                return null; // No match found
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
