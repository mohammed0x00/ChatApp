package com.none.chatapp_server;

import com.none.chatapp_commands.User;

import java.sql.*;

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

    public static User getUserDetails (int id) {
        String query = "Call GetUserDetails(?);";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User tmp = new User();
                if(rs.wasNull())
                {
                    return null;
                }
                tmp.id = rs.getInt("user_id");
                tmp.name = rs.getString("username");
                tmp.age = rs.getInt("age");
                tmp.status_msg = rs.getString("status_message");
                System.out.println(tmp.name);
                // tmp.image = *** Not Implemented Yet ***
                return tmp; // Return user ID if a match is found
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

