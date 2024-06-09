package com.none.chatapp;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.*;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Period;

public class Utils {

    public static void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static int calculateAge(LocalDate birthDate) {
        if (birthDate == null) {
            return 0;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    public static String getFileExtension(Path path) {
        String fileName = path.getFileName().toString();
        return getFileExtension(fileName);
    }
    public static String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex >= 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1);
        } else {
            return ""; // No extension found
        }
    }

}
