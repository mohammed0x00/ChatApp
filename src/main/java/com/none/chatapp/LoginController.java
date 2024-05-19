package com.none.chatapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import  javafx.scene.layout.AnchorPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.input.MouseEvent;
import animatefx.animation.*;

public class LoginController {

    @FXML
    private AnchorPane anchRoot;

    @FXML
    private Circle btnClose;

    @FXML
    private ImageView imgLogo;

    @FXML
    private Label lblLogo;

    @FXML
    private Label lblDsc;

    @FXML
    private ImageView btnBack;

    @FXML
    private TextField txfUser;

    @FXML
    private PasswordField txfPass;

    @FXML
    private Button btnLog;

    @FXML
    private Button btnSign;

    @FXML
    private Label lblSign;

    @FXML
    private Label lblLog;

    @FXML
    private Pane pnSign;

    @FXML
    private Pane pnLogin;


    @FXML
    private void handleButtonAction(ActionEvent event)
    {
        if(event.getSource().equals(btnSign))
        {
            new ZoomIn(pnSign).play();
            pnSign.toFront();
        }

    }

    @FXML
    void handleMouseEvent(MouseEvent event) {
        if(event.getSource() == btnClose)
        {
            new animatefx.animation.FadeOut(anchRoot).play();
            System.exit(0);
        }
        if(event.getSource() == btnBack)
        {
            new ZoomIn(pnLogin).play();
            pnLogin.toFront();
        }

    }
    public void initialize()
    {
        new animatefx.animation.FadeIn(anchRoot).play();
    }

}
