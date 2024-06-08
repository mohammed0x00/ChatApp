module com.none.chatapp {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.desktop;
    requires AnimateFX;
    requires java.compiler;
    requires java.sql;
    requires jdk.jshell;
    requires org.apache.commons.net;
    requires javafx.media;


    opens com.none.chatapp to javafx.fxml;
    opens com.none.chatapp_server to javafx.fxml;
    exports com.none.chatapp;
    exports com.none.chatapp_server;
    exports com.none.chatapp_commands;
    opens com.none.chatapp_commands to javafx.fxml;
}