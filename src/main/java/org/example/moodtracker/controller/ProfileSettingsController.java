package org.example.moodtracker.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.moodtracker.model.DBUtils;
import org.example.moodtracker.model.UIUtils;
import org.example.moodtracker.model.UserInfo;

import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProfileSettingsController implements Initializable {

    @FXML
    private Button button_logout;
    @FXML
    private Button button_table;
    @FXML
    private Button button_homepage;
    @FXML
    private Button button_close;
    @FXML
    private Label label_welcome;
    @FXML
    private Label current_date;
    @FXML
    private TextField tf_username;
    @FXML
    private TextField tf_email;
    @FXML
    private TextField tf_password;
    @FXML
    private Button button_save;
    @FXML
    private Button button_delete;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Button functionality
        button_homepage.setOnAction(event -> DBUtils.changeScene(event,"homepage.fxml", "Home",null));
        button_logout.setOnAction(event -> DBUtils.changeScene(event, "login.fxml", "Log In", null));
        button_table.setOnAction(event -> DBUtils.changeScene(event, "tableView.fxml", "Table View", null));
        button_close.setOnAction(actionEvent -> UIUtils.closeApp((Stage) button_close.getScene().getWindow()));
        // Update button functionality
        button_save.setOnAction(event -> updateUser());
        button_delete.setOnAction(event -> handleDeleteAccount());

        // Set user information and current date for top of homepage
        String currentUser = DBUtils.getCurrentUsername();
        if (currentUser != null) {
            UIUtils.setUserInformation(label_welcome, currentUser);
        }
        UIUtils.setCurrentDate(current_date);

        // Retrieve and populate text fields with user information
        UserInfo user = DBUtils.getUserInfo(DBUtils.getCurrentUsername());
        if (user != null) {
            tf_username.setText(user.getUsername());
            tf_email.setText(user.getEmail());
            tf_password.setText(user.getPassword());
        }
    }

    private void updateUser() {
        // Get updated information from text fields
        String newUsername = tf_username.getText().trim();
        String newEmail = tf_email.getText().trim();
        String newPassword = tf_password.getText().trim();
        String currentUser = DBUtils.getCurrentUsername();

        try {
            // Retrieve user information from the database
            UserInfo userInfo = DBUtils.getUserInfo(currentUser);
            if (userInfo != null) {
                // Update user information in the database
                DBUtils.updateUserInfo(currentUser, newUsername, newEmail, newPassword);
                // Update current username if changed
                if (!currentUser.equals(newUsername)) {
                    DBUtils.setCurrentUsername(newUsername);
                }
                // Inform the user that the update was successful
                showAlert(Alert.AlertType.INFORMATION, "Update Successful", "Your information has been updated successfully.");
            }
        } catch (SQLException e) {
            Logger.getLogger(ProfileSettingsController.class.getName()).log(Level.SEVERE, "Error updating user information", e);
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while updating your information. Please try again later.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void handleDeleteAccount() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Delete Account");
        alert.setContentText("Are you sure you want to delete your account? This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // User confirmed deletion
            String username = DBUtils.getCurrentUsername();
            try {
                DBUtils.deleteUser(username);
                // Logout and redirect to login page
                UIUtils.logout();
            } catch (SQLException e) {
                // Handle deletion error
                Logger.getLogger(ProfileSettingsController.class.getName()).log(Level.SEVERE, "Error deleting user account", e);
                // Show error message to the user
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText("Account Deletion Failed");
                errorAlert.setContentText("An error occurred while deleting your account. Please try again later.");
                errorAlert.showAndWait();
            }
        }
    }

}
