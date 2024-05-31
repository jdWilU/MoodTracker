
package org.example.moodtracker.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXProgressBar;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.moodtracker.model.DBUtils;
import org.example.moodtracker.model.UIUtils;
import org.example.moodtracker.model.UserInfo;

import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller class for the profile settings page.
 * Allows users to view and update their profile information,
 * including username, email, password, display name, and phone number.
 * Users can also delete their account from this page.
 * Users can also view their current level and achievements.
 */
public class ProfileSettingsController implements Initializable {

    // FXML Elements
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

    private static final Logger LOGGER = Logger.getLogger(ProfileSettingsController.class.getName());

    /**
     * Initializes the profile settings page.
     * Sets up button actions, initializes user information,
     * level, achievements, and handles daily entry progress.
     *
     * @param url            The location used to resolve relative paths for the root object.
     * @param resourceBundle The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String currentUser = DBUtils.getCurrentUsername();

        // Initialize users information, level and achievements
        if (currentUser != null) {
            UIUtils.setUserInformation(label_welcome, currentUser);
            UIUtils.setCurrentDate(current_date);
            populateUserInfo();
            initializeLevelAndXP();
            initializeStreakCount();
            initialiseResourcesAchievement();
            setupButtonActions();

            try {
                int userId = DBUtils.getUserId(currentUser);
                boolean entryExists = DBUtils.entryExistsForToday(userId);
                updateDailyEntryProgress(entryExists);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error checking today's entry", e);
            }
        }
    }

    /**
     * Sets up actions for various buttons on the profile settings page.
     */
    private void setupButtonActions() {
        button_homepage.setOnAction(event -> DBUtils.changeScene(event, "homepage.fxml", "Home"));
        button_logout.setOnAction(event -> DBUtils.changeScene(event, "login.fxml", "Log In"));
        button_table.setOnAction(event -> DBUtils.changeScene(event, "tableView.fxml", "Table View"));
        button_close.setOnAction(actionEvent -> UIUtils.closeApp((Stage) button_close.getScene().getWindow()));
        button_daily_entry.setOnAction(event -> DBUtils.changeScene(event, "mood-tracking-page.fxml", "Mood Tracking"));
        button_resources.setOnAction(event -> DBUtils.changeScene(event, "resources-page.fxml", "Educational Resources"));
        button_save.setOnAction(event -> updateUser());
        button_delete.setOnAction(event -> handleDeleteAccount());
    }

    /**
     * Displays an alert dialog with the specified type, title, and content.
     *
     * @param alertType The type of alert dialog.
     * @param title     The title of the alert dialog.
     * @param content   The content text of the alert dialog.
     */
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Updates the user's profile information with the new values entered by the user.
     * Displays success or error alerts accordingly.
     */
    private void updateUser() {
        String newUsername = tf_username.getText().trim();
        String newEmail = tf_email.getText().trim();
        String newPassword = pw_password.getText().trim();
        String newDisplayName = tf_displayname.getText().trim();
        String newPhoneNumber = tf_phone.getText().trim();
        String currentUser = DBUtils.getCurrentUsername();

        try {
            UserInfo userInfo = DBUtils.getUserInfo(currentUser);
            if (userInfo != null) {
                DBUtils.updateUserInfo(currentUser, newUsername, newEmail, newPassword, newDisplayName, newPhoneNumber);
                if (!currentUser.equals(newUsername)) {
                    DBUtils.setCurrentUsername(newUsername);
                }
                showAlert(Alert.AlertType.INFORMATION, "Update Successful", "Your information has been updated successfully.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating user information", e);
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while updating your information. Please try again later.");
        }
    }

    /**
     * Prompts the user to confirm the deletion of their account.
     * If confirmed, deletes the user's account from the database.
     * If canceled, no action is taken.
     */
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
                LOGGER.log(Level.SEVERE, "Error deleting user account", e);
                showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while deleting your account. Please try again later.");
            }
        }
    }

    /**
     * Initializes the user's current level and XP gain.
     * Retrieves user's XP, level, and XP required for the next level from the database.
     * Calculates the progress towards the next level and updates the progress bars accordingly.
     */
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
                LOGGER.log(Level.SEVERE, "Error fetching user's level and XP", e);
                showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while fetching user's level and XP. Please try again later.");
            }
        }
    }

    /**
     * Updates the daily entry progress bar based on whether an entry exists for the current day.
     * If an entry exists, sets the progress bar to 100% (1.0), otherwise to 0% (0.0).
     *
     * @param entryExists Boolean indicating whether an entry exists for the current day.
     */
    private void updateDailyEntryProgress(boolean entryExists) {
        barDailyEntry.setProgress(entryExists ? 1.0 : 0.0);
    }

    /**
     * Initializes the progress bars for three-day and five-day streak achievements.
     * Retrieves user's longest streak and total entry count from the database.
     * Updates the progress bars based on the user's streak counts and specified streak goals.
     */
    private void initializeStreakCount() {
        String currentUser = DBUtils.getCurrentUsername();
        if (currentUser != null) {
            try {
                int userId = DBUtils.getUserId(currentUser);
                int longestStreak = DBUtils.getUserEntryHistory(userId);
                int totalEntries = DBUtils.getTotalEntries(userId);

                updateStreakProgress(three_day_streak, totalEntries, longestStreak, 3, userId, 15);
                updateStreakProgress(five_day_streak, totalEntries, longestStreak, 5, userId, 50);

            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error fetching user's streak count", e);
                showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while fetching user's streak count. Please try again later.");
            }
        }
    }

    /**
     * Checks the progress towards a streak goal and updates the progress bar accordingly.
     * If the streak goal is achieved, awards XP to the user and updates their level.
     *
     * @param streakBar  The progress bar representing the streak achievement.
     * @param totalEntries  The total number of entries made by the user.
     * @param longestStreak  The user's longest streak of consecutive entries.
     * @param streakGoal  The desired streak goal to be achieved.
     * @param userId  The ID of the user.
     * @param xpToAdd  The amount of XP to be added to the user's account upon achieving the streak goal.
     * @throws SQLException If an SQL exception occurs while fetching user data from the database.
     */
    private void updateStreakProgress(MFXProgressBar streakBar, int totalEntries, int longestStreak, int streakGoal, int userId, int xpToAdd) throws SQLException {
        if (totalEntries >= streakGoal) {
            if (longestStreak >= streakGoal) {
                streakBar.setProgress(1.0);
                DBUtils.updateXpInDatabase(userId, xpToAdd);
                DBUtils.updateLevelInDatabase(userId);
            } else {
                streakBar.setProgress((double) totalEntries / streakGoal);
            }
        } else {
            streakBar.setProgress((double) totalEntries / streakGoal);
        }
    }

    /**
     * Initializes the progress bar for the educational resources achievement.
     * Checks whether the user has visited the educational resources page based on database records.
     * Updates the progress bar to reflect the visit status.
     */
    private void initialiseResourcesAchievement() {
        String currentUser = DBUtils.getCurrentUsername();
        if (currentUser != null) {
            try {
                int userId = DBUtils.getUserId(currentUser);
                boolean visitedEducation = DBUtils.hasVisitedEducation(userId);
                resources_visited.setProgress(visitedEducation ? 1.0 : 0.0);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error checking visited_education column", e);
                showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while checking your resource visit status. Please try again later.");
            }
        }
    }

    /**
     * Populates the user information text fields on the profile settings page.
     * Retrieves user information from the database and sets the text fields accordingly.
     */
    private void populateUserInfo() {
        String currentUser = DBUtils.getCurrentUsername();
        UserInfo user = DBUtils.getUserInfo(currentUser);
        if (user != null) {
            tf_username.setText(user.getUsername());
            tf_email.setText(user.getEmail());
            pw_password.setText(user.getPassword());
            tf_phone.setText(user.getPhoneNumber());
            tf_displayname.setText(user.getDisplayName());
        }
    }
}

