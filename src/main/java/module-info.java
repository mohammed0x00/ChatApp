module com.none.chatapp {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.desktop;
    requires AnimateFX;
    requires java.compiler;
    requires java.sql;
    requires jdk.jshell;

    opens com.none.chatapp to javafx.fxml;
    opens com.none.chatapp_server to javafx.fxml;
    exports com.none.chatapp;
    exports com.none.chatapp_server;
}