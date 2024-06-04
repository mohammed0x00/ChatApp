package com.none.chatapp_server;

import com.none.chatapp_commands.Message;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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
    private boolean serverRunning = false;

    @Override
    public void start(Stage primaryStage) {
        logTextArea = new TextArea();
        logTextArea.setEditable(false);
        logTextArea.setStyle("-fx-control-inner-background: #333; -fx-text-fill: white;");

        toggleButton = new Button("Start Server");
        toggleButton.setStyle("-fx-base: #4CAF50; -fx-text-fill: white;");
        toggleButton.setOnAction(event -> toggleServer());

        HBox controlBox = new HBox(toggleButton);
        controlBox.setAlignment(Pos.CENTER);
        controlBox.setSpacing(10);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #222;");
        root.setPadding(new Insets(10));
        root.setCenter(logTextArea);
        root.setBottom(controlBox);

        Scene scene = new Scene(root, 500, 400);

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
                serverRunning = false;
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

    public static void main(String[] args) {
        launch(args);
    }
}
