module com.none.chatapp {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;

    opens com.none.chatapp to javafx.fxml;
    exports com.none.chatapp;
}