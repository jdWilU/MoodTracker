package org.example.moodtracker.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXProgressBar;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
    private MFXButton button_table;
    @FXML
    private MFXButton button_homepage;
    @FXML
    private MFXButton button_daily_entry;
    @FXML
    private MFXButton button_resources;
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
    private TextField tf_displayname;
    @FXML
    private TextField tf_phone;
    @FXML
    private PasswordField pw_password;
    @FXML
    private Button button_save;
    @FXML
    private Button button_delete;
    @FXML
    private ProgressBar xpLevelTopBar;
    @FXML
    private Label levelLabelTopBar;
    @FXML
    private Label xpTotal;
    @FXML
    private Label NextLevelLabel;
    @FXML
    private Label xpToNextLevelLabel;
    @FXML
    private ProgressBar xpLevel;
    @FXML
    private Label levelLabel;
    @FXML
    private MFXProgressBar barDailyEntry;
    @FXML
    private MFXProgressBar three_day_streak;
    @FXML
    private MFXProgressBar five_day_streak;
    @FXML
    private MFXProgressBar resources_visited;


    private static final String[] COLORS = {
            "#fe6969", // Red
            "#a364f8", // Purple
            "#838383", // Gray
            "#2cb2ff", // Blue
            "#20e49f"  // Green
    };

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Button functionality
        button_homepage.setOnAction(event -> DBUtils.changeScene(event,"homepage.fxml", "Home"));
        button_logout.setOnAction(event -> DBUtils.changeScene(event, "login.fxml", "Log In"));
        button_table.setOnAction(event -> DBUtils.changeScene(event, "tableView.fxml", "Table View"));
        button_close.setOnAction(actionEvent -> UIUtils.closeApp((Stage) button_close.getScene().getWindow()));
        button_daily_entry.setOnAction(event -> DBUtils.changeScene(event, "mood-tracking-page.fxml", "Mood Tracking"));
        button_resources.setOnAction(event -> DBUtils.changeScene(event, "resources-page.fxml", "Educational Resources"));

        button_save.setOnAction(event -> updateUser());
        button_delete.setOnAction(event -> handleDeleteAccount());

        // Retrieve current user information
        String currentUser = DBUtils.getCurrentUsername();
        if (currentUser != null) {
            UIUtils.setUserInformation(label_welcome, currentUser);
            try {
                int userId = DBUtils.getUserId(currentUser);
                boolean entryExists = DBUtils.entryExistsForToday(userId);
                updateDailyEntryProgress(entryExists); // Update daily entry progress based on whether the entry exists for today
            } catch (SQLException e) {
                Logger.getLogger(ProfileSettingsController.class.getName()).log(Level.SEVERE, "Error checking today's entry", e);
            }
        }

        // Set current date
        UIUtils.setCurrentDate(current_date);

        // Populate user information
        UserInfo user = DBUtils.getUserInfo(DBUtils.getCurrentUsername());
        if (user != null) {
            tf_username.setText(user.getUsername());
            tf_email.setText(user.getEmail());
            pw_password.setText(user.getPassword());
            tf_phone.setText(user.getPhoneNumber());
            tf_displayname.setText(user.getDisplayName());
        }

        //Initialise the user level and achievements
        // Initialize level and XP information
        initializeLevelAndXP();
        initializeStreakCount();
        initialiseResourcesAchievement();
    }

    private void updateUser() {
        String newUsername = tf_username.getText().trim();
        String newEmail = tf_email.getText().trim();
        String newPassword = pw_password.getText().trim();
        String newDisplayName = tf_displayname.getText().trim();
        String newPhoneNumber = tf_phone.getText().trim();
        String currentUser = DBUtils.getCurrentUsername();

        UserInfo userInfo = DBUtils.getUserInfo(currentUser);
        if (userInfo != null) {
            DBUtils.updateUserInfo(currentUser, newUsername, newEmail, newPassword, newDisplayName, newPhoneNumber);

            if (!currentUser.equals(newUsername)) {
                DBUtils.setCurrentUsername(newUsername);
            }

            showAlert(Alert.AlertType.INFORMATION, "Update Successful", "Your information has been updated successfully.");
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
            String username = DBUtils.getCurrentUsername();
            try {
                DBUtils.deleteUser(username);
                UIUtils.logout();

            } catch (SQLException e) {
                Logger.getLogger(ProfileSettingsController.class.getName()).log(Level.SEVERE, "Error deleting user account", e);

                showAlert(Alert.AlertType.ERROR,"Error","An error occurred while deleting your account. Please try again later.");
            }
        }
    }

    private void initializeLevelAndXP() {
        String currentUser = DBUtils.getCurrentUsername();
        if (currentUser != null) {
            try {
                int userId = DBUtils.getUserId(currentUser);

                int xp = DBUtils.getXpForUser(userId);
                int level = DBUtils.getUserLevel(userId);
                int xpToNextLevel = ((level + 1) * 100) - xp;

                int xpForCurrentLevel = xp - (level * 100);
                double progress = (double) xpForCurrentLevel / 100.0;

                levelLabel.setText("Level " + level);
                levelLabelTopBar.setText("" + level);
                xpTotal.setText(xp + " exp points");
                NextLevelLabel.setText("Level " + (level + 1));
                xpToNextLevelLabel.setText(xpToNextLevel + " exp to");

                xpLevel.setProgress(progress);
                xpLevelTopBar.setProgress(progress);
            } catch (SQLException e) {
                Logger.getLogger(ProfileSettingsController.class.getName()).log(Level.SEVERE, "Error fetching user's level and XP", e);
                showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while fetching user's level and XP. Please try again later.");
            }
        }
    }

    private void updateDailyEntryProgress(boolean entryExists) {
        if (entryExists) {
            barDailyEntry.setProgress(1.0);
        } else {
            barDailyEntry.setProgress(0.0);
        }
    }
    private void initializeStreakCount() {
        String currentUser = DBUtils.getCurrentUsername();
        if (currentUser != null) {
            try {
                // Get user ID
                int userId = DBUtils.getUserId(currentUser);

                // Fetch user's longest streak from the database
                int longestStreak = DBUtils.getUserEntryHistory(userId);
                int totalEntries = DBUtils.getTotalEntries(userId);

                // Set the progress for the 3-day streak progress bar
                if (totalEntries >= 3) {
                    if (longestStreak >= 3) {
                        three_day_streak.setProgress(1.0); // 100%
                        int xpToAdd = 15;
                        DBUtils.updateXpInDatabase(userId, xpToAdd);
                        DBUtils.updateLevelInDatabase(userId);
                    } else {
                        three_day_streak.setProgress((double) totalEntries / 3);
                    }
                } else {
                    three_day_streak.setProgress((double) totalEntries / 3);
                }

                // Set the progress for the 5-day streak progress bar
                if (totalEntries >= 5) {
                    if (longestStreak >= 5) {
                        five_day_streak.setProgress(1.0); // 100%
                        int xpToAdd = 50;
                        DBUtils.updateXpInDatabase(userId, xpToAdd);
                        DBUtils.updateLevelInDatabase(userId);
                    } else {
                        five_day_streak.setProgress((double) totalEntries / 5);
                    }
                } else {
                    five_day_streak.setProgress((double) totalEntries / 5);
                }

            } catch (SQLException e) {
                // Handle SQLException
                Logger.getLogger(ProfileSettingsController.class.getName()).log(Level.SEVERE, "Error fetching user's streak count", e);
                // Show error message to the user
                showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while fetching user's streak count. Please try again later.");
            }
        }
    }

    private void initialiseResourcesAchievement() {
        String currentUser = DBUtils.getCurrentUsername();
        if (currentUser != null) {
            try {
                // Get user ID
                int userId = DBUtils.getUserId(currentUser);

                // Check if user has visited the education page
                boolean visitedEducation = DBUtils.hasVisitedEducation(userId);

                // Set progress for the resource_visited progress bar
                if (visitedEducation) {
                    resources_visited.setProgress(1.0); // 100%
                } else {
                    resources_visited.setProgress(0.0); // 0%
                }

            } catch (SQLException e) {
                // Handle SQLException
                System.err.println("Error checking visited_education column: " + e.getMessage());
                showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while checking your resource visit status. Please try again later.");
            }
        }
    }
}
