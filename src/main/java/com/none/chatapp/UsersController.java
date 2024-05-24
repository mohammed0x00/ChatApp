package com.none.chatapp;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import animatefx.animation.FadeIn;
import animatefx.animation.FadeOut;
import animatefx.animation.ZoomIn;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;


public class UsersController {

    @FXML
    public VBox usersViewBox;
    
    @FXML
    private VBox messageViewBox;

    private int current_user_id;
    private int selected_user_id;

    @FXML
    void initialize() {

    }

    @FXML
    void handleMouseEvent(MouseEvent event) {

    }

    void handleUserItemMouseClick(MouseEvent event) {

    }




}
