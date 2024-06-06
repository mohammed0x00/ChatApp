package com.none.chatapp_server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

import com.none.chatapp_commands.*;
import javafx.scene.image.Image;
import javafx.util.Pair;

public class HandlerThread extends Thread {
    public Socket socket;
    User data;

    public HandlerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            while(true)
            {
                ServerCommand cmd = ServerCommand.WaitForCommand(socket);
                if(cmd instanceof LoginCommand loginCMD) {
                    User tmp = Utils.handleLogin(loginCMD);
                    // Send login response back to client
                    if (tmp != null)
                    {
                        data = tmp;
                        new LoginResponseCommand(LoginResponseCommand.RESPONSE_SUCCESSFUL).SendCommand(socket);
                        OnlineUsers.add(this);
                    }
                    else
                    {
                        new LoginResponseCommand(LoginResponseCommand.RESPONSE_INVALID).SendCommand(socket);
                    }
                }
                else if(cmd instanceof MessagesListRequestCommand reqCmd)
                {

                    Pair <Integer, ArrayList<Message>> list = DatabaseController.loadConversation(data.id, reqCmd.user_id);
                    new MessageListCommand(list.getKey(), list.getValue()).SendCommand(socket);
                }
                else if (cmd instanceof SendMessageCommand sndCmd)
                {
                    Utils.SendMessageToUser(this, sndCmd);
                }
                else if(cmd instanceof RequestUsersListCommand)
                {
                    ArrayList<User> list = DatabaseController.getUsersList(this);
                    assert list != null;
                    OnlineUsers.changeUsersListStatus(list);
                    new ResponseUsersListCommand(list).SendCommand(socket);
                }
                else if (cmd instanceof SignUpCommand sgn_cmd) {
                    Utils.handleSignUp(this, sgn_cmd);
                }
                else if(cmd instanceof RequestFileCommand file_cmd)
                {
                    byte [] tmp;
                    if (file_cmd.owner_id == null) {
                        tmp = FTPUploader.getFile(data.id, file_cmd.filename);
                    } else {
                        tmp = FTPUploader.getFile(file_cmd.owner_id, file_cmd.filename);
                    }

                    if(tmp != null)
                    {
                        new ResponseFileRequestCommand(file_cmd.filename, file_cmd.owner_id,true, tmp).SendCommand(socket);
                    }
                    else
                    {
                        new ResponseFileRequestCommand(file_cmd.filename, file_cmd.owner_id,false, new byte[1]).SendCommand(socket);
                    }
                }
                else if(cmd instanceof ChangeUserImageCommand img_cmd)
                {
                    try{
                        if(img_cmd.remove_image)
                        {
                            DatabaseController.changeUserImage(this.data.id, "", true);
                        }
                        else
                        {
                            String filename = FTPUploader.saveFile(this.data.id, img_cmd.data, img_cmd.extension);
                            if(filename == null) throw new Exception();
                            DatabaseController.changeUserImage(this.data.id, filename, false);
                            data.image = filename;
                        }
                        new ResponseImageChangeCommand(true).SendCommand(socket);
                    }
                    catch (Exception e)
                    {
                        new ResponseImageChangeCommand(false).SendCommand(socket);
                    }
                }
                else if (cmd instanceof RequestProfileImageCommand img_cmd)
                {
                    if(img_cmd.user_id == null)
                    {
                        byte[] img = FTPUploader.getFile(data.id, data.image);
                        new ResponeProfileImageCommand(true, img).SendCommand(socket);
                    }
                    else
                    {
                        byte[] img = null;
                        try{
                            String image_file = DatabaseController.getUserDetails(img_cmd.user_id).image;
                            img = FTPUploader.getFile(img_cmd.user_id, image_file);
                        }
                        catch (Exception ignored){}
                        new ResponeProfileImageCommand(img_cmd.user_id, img != null, img).SendCommand(socket);
                    }

                }
            }
        }
        catch (IOException e) {
            System.out.println("Warning: " + e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(this.data != null)
                OnlineUsers.remove(this);

        }
    }
}