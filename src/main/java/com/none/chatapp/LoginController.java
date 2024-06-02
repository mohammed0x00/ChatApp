package com.none.chatapp;

import javafx.application.Application;
import javafx.collections.FXCollections;
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
import javafx.stage.Stage;
import animatefx.animation.*;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import com.none.chatapp_commands.*;
import java.net.Socket;
import java.time.LocalDate;
import java.time.Period;
import java.util.stream.IntStream;
import java.util.stream.Collectors;  // Import Collectors
import java.util.List;  // Import List
import javafx.util.StringConverter;

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

    public static String Current_User;


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
            String username = txfUser.getText();
            Current_User = username;
            String password = txfPass.getText();
            try {
                socket = new Socket(hostname, port);
                new LoginCommand(username, password).SendCommand(socket);
                // Wait for the response
                ServerCommand response = ServerCommand.WaitForCommand(socket);

                if (response instanceof LoginResponseCommand loginResponse) {
                    if (loginResponse.isSuccess) {
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
                        HandlerThread.controller = loader.getController();
                        HandlerThread.socket = socket;
                        newStage.show();
                        HandlerThread.startThread();
                        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        currentStage.close();
                    } else {
                        // Login failed, show error message
                        Utils.showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");
                        txfUser.setText("");
                        txfPass.setText("");
                        socket.close();
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                Utils.showAlert(Alert.AlertType.ERROR, "Connection Failed", "Cannot Connect to Server"+ e.getMessage() + e.toString());
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
                int age = calculateAge(newValue);
            }
        });

    }

    private void handleRadioButtonSelection(RadioButton selectedRadioButton) {
        RdMale.setSelected(selectedRadioButton == RdMale);
        RdFemale.setSelected(selectedRadioButton == RdFemale);
        RdOther.setSelected(selectedRadioButton == RdOther);
    }

    private int calculateAge(LocalDate birthDate) {
        if (birthDate == null) {
            return 0;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}


