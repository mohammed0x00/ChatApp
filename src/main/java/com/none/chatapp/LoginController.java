package com.none.chatapp;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import animatefx.animation.*;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import com.none.chatapp_commands.*;

import java.io.IOException;
import java.net.Socket;


public class LoginController {
    public final String hostname = "localhost";
    public final int port = 12345;
    Socket socket;

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
    private Button btnSignUp;

    @FXML
    private TextField signupEmailTextField;

    @FXML
    private TextField signupUsernameTextField;

    @FXML
    private PasswordField signupPasswordTextField;

    @FXML
    private Pane pnSign;

    @FXML
    private Pane pnLogin;

    @FXML
    private RadioButton RdMale;

    @FXML
    private RadioButton RdFemale;

    @FXML
    private RadioButton RdOther;

    @FXML
    private DatePicker BrthDate;

    @FXML
    private VBox SignVBox;

    private String username;
    private String password;



    // Fields to store initial mouse click coordinates
    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    private void handleButtonAction(ActionEvent event) {
        if (event.getSource().equals(btnSign)) {
            SignVBox.setVisible(true);
            new ZoomIn(pnSign).play();
            pnSign.toFront();
        }
        if (event.getSource().equals(btnLog)) {
            username = txfUser.getText();
            password = txfPass.getText();
            try {
                if (login()) {
                    // Login successful, proceed to the next scene or dashboard
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("users-view.fxml"));
                    Scene scene = new Scene(loader.load(), 950, 700);
                    Stage newStage = new Stage();
                    newStage.setTitle("Chat Bus");
                    newStage.setScene(scene);
                    newStage.initStyle(StageStyle.TRANSPARENT);
                    newStage.setResizable(true);
                    newStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                        @Override
                        public void handle(WindowEvent event) {
                            System.exit(0);
                        }
                    });

                    newStage.show();
                    Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    HandlerThread user_thread = new HandlerThread(this, currentStage, socket, loader.getController());
                    ((UsersController)loader.getController()).current_thread = user_thread;
                    user_thread.startThread();

                    txfUser.setText("");
                    txfPass.setText("");
                    currentStage.hide();
                } else {
                    // Login failed, show error message
                    Utils.showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");
                    txfUser.setText("");
                    txfPass.setText("");
                    socket.close();
                }
            } catch (Exception e) {
                System.out.println(e.toString() + e.getMessage()+e.getLocalizedMessage());
                e.printStackTrace();


                Utils.showAlert(Alert.AlertType.ERROR, "Connection Failed", "Cannot Connect to Server"+ e.getMessage() + e.toString());
            }
        }
    }

    public boolean login() throws IOException, ClassNotFoundException {
        socket = new Socket(hostname, port);
        new LoginCommand(username, password).SendCommand(socket);
        // Wait for the response
        ServerCommand response = ServerCommand.WaitForCommand(socket, 7);
        if (response instanceof LoginResponseCommand loginResponse)
        {
            return loginResponse.isSuccess;
        }
        return false;
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
            SignVBox.setVisible(false);
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

        //SignVBox.setVisible(false);
        new animatefx.animation.FadeIn(anchRoot).play();

        // Add mouse pressed and dragged event handlers to the root node
        anchRoot.setOnMousePressed(this::handleMousePressed);
        anchRoot.setOnMouseDragged(this::handleMouseDragged);
        SignVBox.setVisible(false);

        // Initialize radio buttons without ToggleGroup
        RdMale.setOnAction(e -> handleRadioButtonSelection(RdMale));
        RdFemale.setOnAction(e -> handleRadioButtonSelection(RdFemale));
        RdOther.setOnAction(e -> handleRadioButtonSelection(RdOther));

        // Initialize radio buttons without ToggleGroup
        RdMale.setOnAction(e -> handleRadioButtonSelection(RdMale));
        RdFemale.setOnAction(e -> handleRadioButtonSelection(RdFemale));
        RdOther.setOnAction(e -> handleRadioButtonSelection(RdOther));

        // Add listener to DatePicker to calculate and display age
        BrthDate.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                int age = Utils.calculateAge(newValue);
            }
        });

    }

    private void handleRadioButtonSelection(RadioButton selectedRadioButton) {
        RdMale.setSelected(selectedRadioButton == RdMale);
        RdFemale.setSelected(selectedRadioButton == RdFemale);
        RdOther.setSelected(selectedRadioButton == RdOther);
    }

    @FXML
    public void handleSignUpButton(ActionEvent event)
    {
        try
        {
            socket = new Socket(hostname, port);
        }catch (Exception ignored){}

        int age = Utils.calculateAge(BrthDate.getValue());
        boolean gender = false;

        if(RdMale.isSelected()) gender = true;

        if(signupEmailTextField.getText().isEmpty())
        {
            Utils.showAlert(Alert.AlertType.ERROR, "Error", "Please, Enter E-Mail address.");
            return;
        }
        if(signupPasswordTextField.getText().isEmpty())
        {
            Utils.showAlert(Alert.AlertType.ERROR, "Error", "Please, Enter Password.");
            return;
        }
        if(signupUsernameTextField.getText().isEmpty())
        {
            Utils.showAlert(Alert.AlertType.ERROR, "Error", "Please, Enter User Name.");
            return;
        }

        try {
            new SignUpCommand(signupUsernameTextField.getText(), signupPasswordTextField.getText(),
                                signupEmailTextField.getText(), age, gender).SendCommand(socket);
        } catch (IOException e) {
            Utils.showAlert(Alert.AlertType.ERROR, "Error", "Connection Error.");
            return;
        }

        try {
            ServerCommand cmd = ServerCommand.WaitForCommand(socket, 5);

            if(cmd instanceof SignUpResponseCommand response)
            {
                if(response.err_code == SignUpResponseCommand.RESPONSE_SUCCESSFUL)
                {
                    Utils.showAlert(Alert.AlertType.INFORMATION, "Succeeded", "Your Account was Created. Please login");
                    new ZoomIn(pnLogin).play();
                    pnLogin.toFront();
                    SignVBox.setVisible(false);
                }
                else if(response.err_code == SignUpResponseCommand.RESPONSE_EXISTS)
                {
                    Utils.showAlert(Alert.AlertType.ERROR, "Error", "Username or E-mail already exists.");
                }
                else
                {
                    Utils.showAlert(Alert.AlertType.ERROR, "Error", "Unexpected error: -1");
                }
            }
            else
            {
                Utils.showAlert(Alert.AlertType.ERROR, "Error", "Unexpected error: Timeout exceeded");
            }
        }
        catch (Exception e)
        {
            Utils.showAlert(Alert.AlertType.ERROR, "Error", "Cannot connect to server");
        }



    }


}


