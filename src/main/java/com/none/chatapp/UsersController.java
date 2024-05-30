package com.none.chatapp;

import java.io.IOException;
import com.none.chatapp_commands.Message;
import com.none.chatapp_commands.MessagesListRequestCommand;
import com.none.chatapp_commands.SendMessageCommand;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;


public class UsersController {

    @FXML
    public VBox usersViewBox;

    @FXML
    public VBox offlineUsersViewBox;
    
    @FXML
    public VBox messageViewBox;

    @FXML
    public Label selectedUserName;

    @FXML
    private ImageView selectedUserImage;

    @FXML
    public TextField messageTextField;

    public static int selected_user_id;
    public static int selected_conv_id;

    // Add this method to initialize the circular clipping for the image
    private void initializeCircularImage() {
        // Set the dimensions of the image view to be square
        double imageSize = 70; // Set your desired size here
        selectedUserImage.setFitWidth(imageSize);
        selectedUserImage.setFitHeight(imageSize);

        // Create a circle clipping mask
        Circle clip = new Circle();
        clip.setCenterX(imageSize / 2);
        clip.setCenterY(imageSize / 2);
        clip.setRadius(imageSize / 2);

        // Apply the clipping mask to the image view
        selectedUserImage.setClip(clip);
    }


    @FXML
    void initialize() {
        initializeCircularImage();HandlerThread.userItemMouseEvent = this::handleUserItemMouseClick;
    }

    @FXML
    public void handleSendButtonEvent(MouseEvent event) {
        Message msg = new Message();
        msg.conv_id = selected_conv_id;
        msg.content = messageTextField.getText();
        msg.type = Message.Type.text;
        try {
            new SendMessageCommand(msg).SendCommand(HandlerThread.socket);
            messageTextField.clear();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void handleUserItemMouseClick(MouseEvent event) {
        messageViewBox.getChildren().clear();

        Object caller = event.getSource();
        if(!(caller instanceof UserItem)) caller = ((Node)caller).getParent();
        if(caller instanceof UserItem selectedUser)
        {
            selected_user_id = selectedUser.usr_id;
            try {
                System.out.println(event.getSource().toString());
                new MessagesListRequestCommand(selected_user_id).SendCommand(HandlerThread.socket);
                selectedUserName.setText(selectedUser.getName());
                if (selectedUser.getName().equals("Sarah Donald")) {
                    selectedUserImage.setImage(HandlerThread.imageUrl2);
                } else {
                    selectedUserImage.setImage(HandlerThread.imageUrl1);
                }
            }
            catch (IOException e)
            {
                System.out.println("IOEXception: "+ e.getMessage());
            }
        }
    }




}
