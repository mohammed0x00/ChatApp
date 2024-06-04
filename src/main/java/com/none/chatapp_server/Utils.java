package com.none.chatapp_server;

import com.none.chatapp_commands.*;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.net.SocketException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

public class Utils {
    private static final String FTP_SERVER = "localhost";
    private static final int FTP_PORT = 21;
    private static final String FTP_USERNAME = "chatbus";
    private static final String FTP_PASSWORD = "12345";

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

    public static void handleSignUp(HandlerThread thread, SignUpCommand signup_cmd) {

        try {
            if(signup_cmd.age > 99 || signup_cmd.age < 10)
            {
                new SignUpResponseCommand(SignUpResponseCommand.RESPONSE_AGE_INVALID).SendCommand(thread.socket);
                return;
            }

            if(signup_cmd.username.isEmpty() || signup_cmd.e_mail.isEmpty() || signup_cmd.password.isEmpty())
            {
                new SignUpResponseCommand(SignUpResponseCommand.RESPONSE_EMPTY_FIELDS).SendCommand(thread.socket);
                return;
            }

            Integer ret = DatabaseController.SignUpDBCom(signup_cmd.username, signup_cmd.e_mail, signup_cmd.password, signup_cmd.age, signup_cmd.gender);
            if (ret == DatabaseController.SIGNUP_EXISTS) {
                new SignUpResponseCommand(SignUpResponseCommand.RESPONSE_EXISTS).SendCommand(thread.socket);
                return;
            }
            else if(ret == DatabaseController.SIGNUP_UNEXPECTED_ERROR)
            {
                new SignUpResponseCommand(SignUpResponseCommand.RESPONSE_UNEXPECTED).SendCommand(thread.socket);
                return;
            }
            else
            {
                User details = DatabaseController.getUserDetails(ret);
                if(details == null)
                {
                    new SignUpResponseCommand(SignUpResponseCommand.RESPONSE_UNEXPECTED).SendCommand(thread.socket);
                }
                else
                {
                    new SignUpResponseCommand(SignUpResponseCommand.RESPONSE_SUCCESSFUL).SendCommand(thread.socket);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            // An error occurred during login
            System.out.println("SignUp failed: Unexpected error.");
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

    public static String saveFile(User user, byte[] data, String ext) {
        String file_name = getRandomFileName();

        FTPClient ftpClient = new FTPClient();
        try {
            // Connect and login to the server
            ftpClient.connect(FTP_SERVER, FTP_PORT);
            ftpClient.login(FTP_USERNAME, FTP_PASSWORD);

            // Enter passive mode
            ftpClient.enterLocalPassiveMode();

            ftpClient.makeDirectory(String.valueOf(user.id));


            return null;
        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getRandomFileName()
    {
        return String.valueOf(LocalDate.now().getYear()) +
                String.valueOf(LocalDate.now().getMonth()) +
                String.valueOf(LocalDate.now().getDayOfYear()) +
                String.valueOf(LocalDateTime.now().getHour()) +
                String.valueOf(LocalDateTime.now().getMinute()) +
                String.valueOf(LocalDateTime.now().getSecond()) +
                String.valueOf(LocalDateTime.now().getNano()) +
                String.valueOf(new Random().nextInt());
    }

}