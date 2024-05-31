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

/**
 * Utility class for common UI-related tasks.
 */
public class UIUtils {

    /**
     * Sets user information in a label.
     *
     * @param label    The label to display user information.
     * @param username The username of the current user.
     */
    public static void setUserInformation(Label label, String username) {
        UserInfo userInfo = DBUtils.getUserInfo(username);
        if (userInfo != null && userInfo.getDisplayName() != null && !userInfo.getDisplayName().isEmpty()) {
            label.setText("Welcome, " + userInfo.getDisplayName() + "!");
        } else {
            label.setText("Welcome, " + username + "!");
        }
    }

    /**
     * Sets the current date in a label.
     *
     * @param label The label to display the current date.
     */
    public static void setCurrentDate(Label label) {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = currentDate.format(formatter);
        label.setText("Today's Date: " + formattedDate);
    }

    /**
     * Closes the application window.
     *
     * @param stage The stage of the application.
     */
    public static void closeApp(Stage stage) {
        stage.close();
    }

    /**
     * Logs out the user and returns to the login screen.
     */
    public static void logout() {
        // Set the current username to null
        DBUtils.setCurrentUsername(null);

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
