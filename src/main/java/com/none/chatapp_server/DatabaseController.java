package com.none.chatapp_server;

import com.none.chatapp_commands.Message;
import com.none.chatapp_commands.User;
import javafx.util.Pair;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class DatabaseController {

    private static final String URL = "jdbc:mysql://localhost:3306/chatbus_app";
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
                tmp.isOnline = true;
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

    public static Pair<Integer, ArrayList<Message>> loadConversation(int user1_id, int user2_id) throws SQLException {
        Integer conv_id;
        ArrayList<Message> tmp = new ArrayList<>();
        try (CallableStatement stmt = conn.prepareCall("Call StartConversation(?, ?)")) {
            stmt.setInt(1, user1_id);
            stmt.setInt(2, user2_id);
            stmt.execute();

        } catch (Exception e) {
            throw e;
            //return null;
        }

        try (CallableStatement stmt = conn.prepareCall("Call GetConversationID(?, ?, ?);")) {
            stmt.setInt(1, user1_id);
            stmt.setInt(2, user2_id);
            stmt.registerOutParameter(3, Types.INTEGER);
            stmt.execute();
            conv_id = stmt.getInt(3);

        } catch (Exception e) {
            throw e;
            //return null;
        }


        try (PreparedStatement stmt = conn.prepareStatement("Call GetMessages(?);")) {
            stmt.setInt(1, conv_id);
            ResultSet rs = stmt.executeQuery();
            while(rs.next())
            {
                Message new_msg = new Message();
                new_msg.id = rs.getInt("message_id");
                new_msg.content = rs.getString("content");
                new_msg.sent_at = rs.getTimestamp("sent_at");
                new_msg.seen = rs.getBoolean("is_seen");
                new_msg.sender_id = rs.getInt("sender_id");

                // Debug statement to verify sent_at value
                System.out.println("Message ID: " + new_msg.id + ", Sent At: " + new_msg.sent_at);
                tmp.add(new_msg);
            }

        } catch (Exception e) {
            throw e;
            //return null;
        }

        return new Pair<> (conv_id, tmp);

    }



    public static Integer sendMessage (Message msg) throws SQLException {

        try (PreparedStatement stmt = conn.prepareStatement("Call SendMessage(?, ?, ?, ?);")) {
            stmt.setInt(1, msg.conv_id);
            stmt.setInt(2, msg.sender_id);
            stmt.setString(3, msg.content);
            stmt.setString(4, msg.getType());

            ResultSet rs = stmt.executeQuery();
            if(rs.next())
            {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            throw e;
        }
        return null;
    }

    public static ArrayList<User> getUsersList (HandlerThread exceptMe) {
        ArrayList<User> list = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement("Call ListAllUsers();")) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                User tmp = new User();
                if(rs.wasNull())
                {
                    return list;
                }
                tmp.id = rs.getInt("user_id");
                tmp.name = rs.getString("username");
                tmp.age = rs.getInt("age");
                tmp.status_msg = rs.getString("status_message");
                tmp.isOnline = true;
                if(tmp.id != exceptMe.data.id)
                {
                    list.add(tmp);
                }

                // tmp.image = *** Not Implemented Yet ***
                ; // Return user ID if a match is found
            }
            return list;

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
