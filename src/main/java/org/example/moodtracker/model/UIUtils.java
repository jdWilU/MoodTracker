package org.example.moodtracker.model;

import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UIUtils {

    // Method to set user information in a label
    public static void setUserInformation(Label label, String username) {
        label.setText("Welcome, " + username + "!");
    }

    // Method to set the current date in a label
    public static void setCurrentDate(Label label) {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = currentDate.format(formatter);
        label.setText("Today's Date: " + formattedDate);
    }

    // Method to close the application window
    public static void closeApp(Stage stage) {
        stage.close();
    }
}