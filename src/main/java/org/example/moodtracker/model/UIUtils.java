package org.example.moodtracker.model;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.example.moodtracker.model.DBUtils.*;

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

    // Method to log out & return to login screen
    public static void logout() {
        // Set the current username to null
        setCurrentUsername(null);

        // Redirect to the login page
        Platform.runLater(() -> {
            try {
                Stage primaryStage = new Stage();
                Parent root = FXMLLoader.load(Objects.requireNonNull(DBUtils.class.getClassLoader().getResource("login.fxml")));
                primaryStage.setScene(new Scene(root, 800, 600));
                primaryStage.setTitle("Log In");
                primaryStage.show();
            } catch (IOException e) {
                Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, "Error loading login scene", e);
            }
        });
    }
}