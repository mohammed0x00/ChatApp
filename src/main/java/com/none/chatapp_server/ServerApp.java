package com.none.chatapp_server;

import com.none.chatapp_commands.Message;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

public class ServerApp extends Application {
    private final static int SERVER_PORT = 12345;

    private ServerSocket serverSocket;
    private Thread serverThread;
    private TextArea logTextArea;
    private Button toggleButton;
    private TextArea broadcastTextArea;
    private Button broadcastButton;
    private boolean serverRunning = false;
    private Stage primaryStage;
    private HBox controlBox;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage; // Add this line
        // Logs area
        logTextArea = new TextArea();
        logTextArea.setEditable(false);
        logTextArea.setStyle("-fx-control-inner-background: #2b2b2b; -fx-text-fill: #f5f5f5;");
        logTextArea.setPrefHeight(400); // Set preferred height for log window
        VBox.setVgrow(logTextArea, Priority.ALWAYS);

        // Broadcast message area
        broadcastTextArea = new TextArea();
        broadcastTextArea.setPromptText("Enter message to broadcast...");
        broadcastTextArea.setStyle("-fx-control-inner-background: #2b2b2b; -fx-text-fill: #f5f5f5; -fx-prompt-text-fill: #808080;");
        broadcastTextArea.setWrapText(true);
        broadcastTextArea.setMaxHeight(100); // Set maximum height for broadcast text area
        broadcastTextArea.setVisible(false);
        VBox.setVgrow(broadcastTextArea, Priority.ALWAYS);

        // Broadcast button
        broadcastButton = new Button("Broadcast");
        broadcastButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
        broadcastButton.setOnAction(event -> broadcastMessage());
        broadcastButton.setVisible(false); // Initially hidden

        // Server control button
        toggleButton = new Button("Start Server");
        toggleButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        toggleButton.setOnAction(event -> toggleServer());

        // Control Box for server control and broadcast buttons
        controlBox = new HBox(5, toggleButton);
        // Set manual alignment for buttons
        controlBox.setAlignment(Pos.CENTER);
        controlBox.setPadding(new Insets(10));
        HBox.setHgrow(toggleButton, Priority.ALWAYS); // Make buttons grow with window size
        HBox.setHgrow(broadcastButton, Priority.ALWAYS);

        // Layout
        VBox root = new VBox(10);
        root.setStyle("-fx-background-color: #1e1e1e;");
        root.setPadding(new Insets(10));
        root.getChildren().addAll(logTextArea, broadcastTextArea, controlBox);

        Scene scene = new Scene(root, 550, 450);

        primaryStage.setScene(scene);
        primaryStage.setTitle("ChatApp Server");
        primaryStage.setOnCloseRequest(event -> stopServer());
        primaryStage.show();
    }

    private void toggleServer() {
        if (!serverRunning) {
            startServer();
        } else {
            stopServer();
        }
    }

    private void startServer() {
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
            log("Server is listening on port " + SERVER_PORT);
            toggleButton.setText("Stop Server");
            toggleButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
            serverRunning = true;
            serverThread = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        Socket socket = serverSocket.accept();
                        log("New client connected: " + socket.getInetAddress().getHostAddress());
                        new HandlerThread(socket).start();
                    } catch (IOException e) {
                        log("Error accepting client connection: " + e.getMessage());
                    }
                }
            });
            DatabaseController.connect();
            serverThread.start();
            Platform.runLater(() -> {
                controlBox.getChildren().add(broadcastButton);
                broadcastTextArea.setVisible(true);
                broadcastButton.setVisible(true);
                primaryStage.setHeight(600);
            });
            BooleanBinding broadcastButtonDisabled = Bindings.createBooleanBinding(() ->
                            broadcastTextArea.getText().trim().isEmpty(),
                    broadcastTextArea.textProperty()
            );
            broadcastButton.disableProperty().bind(broadcastButtonDisabled);

        } catch (IOException e) {
            log("Error starting server: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void stopServer() {
        if (serverSocket != null) {
            try {
                DatabaseController.closeConnection();
                serverSocket.close();
                serverThread.interrupt();
                log("Server stopped");
                toggleButton.setText("Start Server");
                toggleButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                serverRunning = false;
                Platform.runLater(() -> {
                    controlBox.getChildren().remove(broadcastButton);
                    broadcastTextArea.setVisible(false);
                    primaryStage.setHeight(500);
                });
            } catch (IOException e) {
                log("Error stopping server: " + e.getMessage());
            }
        } else {
            log("Server socket is null. Server might not have been started.");
        }
    }

    private void log(String message) {
        Platform.runLater(() -> logTextArea.appendText(message + "\n"));
    }
    private void broadcastMessage() {
        String message = broadcastTextArea.getText().trim();
        if (!message.isEmpty()) {
            log("Broadcasting message: " + message);
            //
            broadcastTextArea.clear();
        } else {
            log("Broadcast message is empty");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
