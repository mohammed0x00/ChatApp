package com.none.chatapp_server;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DatabaseController {

    private static final String URL = "jdbc:mysql://localhost:3306/chat_app";
    private static final String USER = "Admin";
    private static final String PASSWORD = "Admin";

    public static Connection conn;

    public static void connect() throws Exception {
        conn = DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static Integer validateUser(String identifier, String passwordHash) {
        String query = "SELECT LoginWithEmailOrUsername(?, ?) AS user_id";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, identifier);
            stmt.setString(2, passwordHash);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int user_id = rs.getInt("user_id");
                if(rs.wasNull())
                {
                    return null;
                }
                return user_id; // Return user ID if a match is found
            }
            System.out.println("HI");

            return null; // No match found

        } catch (Exception e) {
            return null;
        }
    }


    // Close the database connection
    public static void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

