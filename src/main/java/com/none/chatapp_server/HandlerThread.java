package com.none.chatapp_server;

import java.io.*;
import java.util.ArrayList;

import com.none.chatapp_commands.*;
import javafx.scene.image.Image;
import javafx.util.Pair;

public class HandlerThread extends Thread {
    public LockableSocket socket;
    User data;

    public HandlerThread(LockableSocket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            while(true)
            {
                ServerCommand cmd = ServerCommand.WaitForCommand(socket);
                switch (cmd) {
                    case LoginCommand loginCMD -> {
                        User tmp = Utils.handleLogin(loginCMD);
                        // Send login response back to client
                        if (tmp != null) {
                            data = tmp;
                            new LoginResponseCommand(LoginResponseCommand.RESPONSE_SUCCESSFUL).SendCommand(socket);
                            OnlineUsers.add(this);
                        } else {
                            new LoginResponseCommand(LoginResponseCommand.RESPONSE_INVALID).SendCommand(socket);
                        }
                    }
                    case MessagesListRequestCommand reqCmd -> {
                        if(reqCmd.user_id == -1) new MessageListCommand(-1, OnlineUsers.broadcastMessages).SendCommand(socket);
                        else
                        {
                            Pair<Integer, ArrayList<Message>> list = DatabaseController.loadConversation(data.id, reqCmd.user_id);
                            new MessageListCommand(list.getKey(), list.getValue()).SendCommand(socket);
                        }
                    }
                    case SendMessageCommand sndCmd -> Utils.SendMessageToUser(this, sndCmd);
                    case RequestUsersListCommand ignored -> {
                        ArrayList<User> list = DatabaseController.getUsersList(this);
                        assert list != null;
                        list.add(OnlineUsers.serverUser);
                        OnlineUsers.changeUsersListStatus(list);
                        new ResponseUsersListCommand(list).SendCommand(socket);
                    }
                    case SignUpCommand sgn_cmd -> Utils.handleSignUp(this, sgn_cmd);
                    case RequestFileCommand file_cmd -> {
                        byte[] tmp;
                        if (file_cmd.owner_id == null) {
                            tmp = FTPUploader.getFile(data.id, file_cmd.filename);
                        } else {
                            tmp = FTPUploader.getFile(file_cmd.owner_id, file_cmd.filename);
                        }

                        if (tmp != null) {
                            new ResponseFileRequestCommand(file_cmd.filename, file_cmd.owner_id, true, tmp).SendCommand(socket);
                        } else {
                            new ResponseFileRequestCommand(file_cmd.filename, file_cmd.owner_id, false, new byte[1]).SendCommand(socket);
                        }
                    }
                    case ChangeUserImageCommand img_cmd -> {
                        try {
                            if (img_cmd.remove_image) {
                                DatabaseController.changeUserImage(this.data.id, "", true);
                                data.image = null;
                            } else {
                                String filename = FTPUploader.saveFile(this.data.id, img_cmd.data, img_cmd.extension);
                                if (filename == null) throw new Exception();
                                DatabaseController.changeUserImage(this.data.id, filename, false);
                                data.image = filename;
                            }
                            new ResponseImageChangeCommand(true).SendCommand(socket);
                        } catch (Exception e) {
                            new ResponseImageChangeCommand(false).SendCommand(socket);
                        }
                    }
                    case RequestProfileImageCommand img_cmd -> {
                        if (img_cmd.user_id == null) {
                            byte[] img = FTPUploader.getFile(data.id, data.image);
                            new ResponeProfileImageCommand(true, img).SendCommand(socket);
                        } else {
                            byte[] img = null;
                            try {
                                String image_file = DatabaseController.getUserDetails(img_cmd.user_id).image;
                                img = FTPUploader.getFile(img_cmd.user_id, image_file);
                            } catch (Exception ignored) {
                            }
                            new ResponeProfileImageCommand(img_cmd.user_id, img != null, img).SendCommand(socket);
                        }
                    }
                    case RequestUserDetailsCommand ignored ->
                            new ResponseUserDetailsCommand(data).SendCommand(socket);
                    case ChangeUserInfoCommand ignored -> {
                        ResponseUserInfoChangeCommand response = new ResponseUserInfoChangeCommand();
                        switch (cmd) {
                            case ChangeUserInfoCommand.CHANGE_PASSWORD usr_cmd ->{
                                Pair<Boolean, String> ret = DatabaseController.changeUserPassword(data.id, usr_cmd.old_password, usr_cmd.new_password);
                                response.responseType = ResponseUserInfoChangeCommand.ResponseType.CHANGE_PASSWORD;
                                response.status = ret.getKey();
                                response.err_msg = ret.getValue();
                            }
                            case ChangeUserInfoCommand.CHANGE_STATUS_MSG usr_cmd -> {
                                response.status = DatabaseController.changeUserStatusMessage(data.id, usr_cmd.message);
                                response.responseType = ResponseUserInfoChangeCommand.ResponseType.CHANGE_STATUS_MSG;
                                if(response.status) data.status_msg = usr_cmd.message;
                            }
                            case ChangeUserInfoCommand.CHANGE_USER_NAME usr_cmd -> {
                                response.status = DatabaseController.changeUserName(data.id, usr_cmd.Username);
                                response.responseType = ResponseUserInfoChangeCommand.ResponseType.CHANGE_USER_NAME;
                                if(response.status) data.name = usr_cmd.Username;
                            }
                            default -> {}
                        }
                        response.SendCommand(socket);
                    }
                    case null, default -> {}
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

            }
            if(this.data != null)
                OnlineUsers.remove(this);

        }
    }
}