module com.none.chatapp {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.desktop;
    requires AnimateFX;

    opens com.none.chatapp to javafx.fxml;
    exports com.none.chatapp;
}