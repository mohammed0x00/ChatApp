package com.none.chatapp;

import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class DatabaseUtil {

    private static final String URL = "jdbc:mysql://localhost:3306/chat_app";
    private static final String USER = "Admin";
    private static final String PASSWORD = "Admin";

    public static Connection conn;
    public static int user_id;

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
            return null; // No match found

        } catch (Exception e) {
            return null;
        }
    }

    public static ResultSet getUsersList() {
        String query = "Call ListAllUsers();";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            return stmt.executeQuery();

        } catch (Exception e) {
            return null;
        }
    }

    public static ArrayList<UserItem> getOnlineUsersList(EventHandler<MouseEvent> userItemMouseEvent) {
        String query = "Call GetOnlineUsers();";
        ArrayList<UserItem> tmp = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while(rs.next())
            {
                int user = rs.getInt("user_id");
                if(user != user_id)
                {
                    String name = rs.getString("username");
                    tmp.add(new UserItem(userItemMouseEvent, user, name,true, new Image("https://w7.pngwing.com/pngs/178/595/png-transparent-user-profile-computer-icons-login-user-avatars.png")));
                }
            }
            return tmp;

        } catch (Exception e) {
            return null;
        }
    }

    public static ArrayList<MessageBubble> loadConversation(int user1_id, int user2_id)
    {
        int conv_id;
        String query = "Call StartConversation(?, ?); Call GetConversationID(?, ?);";
        ArrayList<MessageBubble> tmp = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, user1_id);
            stmt.setInt(2, user2_id);
            stmt.setInt(3, user1_id);
            stmt.setInt(4, user2_id);
            ResultSet rs = stmt.executeQuery();
            if(rs.next())
            {
                conv_id = rs.getInt("p_conversation_id");
            }
            else
            {
                return null;
            }

        } catch (Exception e) {
            return null;
        }

        String query2 = "Call GetMessages(?);";
        try (PreparedStatement stmt = conn.prepareStatement(query2)) {
            stmt.setInt(1, conv_id);
            ResultSet rs = stmt.executeQuery();
            while(rs.next())
            {
                int id = rs.getInt("message_id");
                String content = rs.getString("content");
                String sent_at = new SimpleDateFormat("mm-dd hh:mm").format(rs.getTime("read_at"));
                boolean is_seen = rs.getBoolean("is_seen");
                tmp.add(new MessageBubble(id, content, sent_at, is_seen? "seen" : ""));
            }

        } catch (Exception e) {
            return null;
        }

        return tmp;

    }

    public static void updateStatus() throws SQLException {
        String query = "Call UpdateUserLastActive(?);";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, user_id);
        stmt.execute();
    }

}
