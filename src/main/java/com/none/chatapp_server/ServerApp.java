package com.none.chatapp_server;

import javafx.application.Application;
import javafx.stage.Stage;

import java.net.ServerSocket;
import java.net.Socket;

public class ServerApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        int port = 12345;  // Port number on which the server will listen

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            while(true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");

                // Create a new thread for each client connection
                new HandlerThread(socket).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
