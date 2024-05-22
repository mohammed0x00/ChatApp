package com.none.chatapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import animatefx.animation.*;

public class LoginController {

    @FXML
    private AnchorPane anchRoot;

    @FXML
    private Circle btnClose;

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
    private Pane pnSign;

    @FXML
    private Pane pnLogin;

    // Fields to store initial mouse click coordinates
    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    private void handleButtonAction(ActionEvent event) {
        if (event.getSource().equals(btnSign)) {
            new ZoomIn(pnSign).play();
            pnSign.toFront();
        }
        if (event.getSource().equals(btnLog)) {
            String username = txfUser.getText();
            String password = txfPass.getText();

            try
            {
                DatabaseUtil.connect();
                Integer userId = DatabaseUtil.validateUser(username, password);
                if (userId != null) {
                    // Login successful, proceed to the next scene or dashboard

                    DatabaseUtil.user_id = userId;
                    Scene scene = new Scene(new FXMLLoader(getClass().getResource("users-view.fxml")).load(), 800, 700);
                    Stage newStage = new Stage();
                    newStage.setTitle("Chat Bus");
                    newStage.setScene(scene);
                    newStage.show();

                    Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    currentStage.close();

                } else {
                    // Login failed, show error message
                    Utils.showAlert(Alert.AlertType.ERROR,"Login Failed", "Invalid username or password.");
                }
            }
            catch (Exception e)
            {
                Utils.showAlert(Alert.AlertType.ERROR,"Connection Failed" ,"Cannot Connect to Server");
            }

        }
    }

    @FXML
    void handleMouseEvent(MouseEvent event) {
        if (event.getSource() == btnClose) {
            new animatefx.animation.FadeOut(anchRoot).play();
            System.exit(0);
        }
        if (event.getSource() == btnBack) {
            new ZoomIn(pnLogin).play();
            pnLogin.toFront();
        }
    }

    @FXML
    private void handleMousePressed(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    @FXML
    private void handleMouseDragged(MouseEvent event) {
        Stage stage = (Stage) anchRoot.getScene().getWindow();
        stage.setX(event.getScreenX() - xOffset);
        stage.setY(event.getScreenY() - yOffset);
    }

    @FXML
    public void initialize() {
        new animatefx.animation.FadeIn(anchRoot).play();

        // Add mouse pressed and dragged event handlers to the root node
        anchRoot.setOnMousePressed(this::handleMousePressed);
        anchRoot.setOnMouseDragged(this::handleMouseDragged);
    }



}
