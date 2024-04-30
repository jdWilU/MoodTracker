package org.example.moodtracker.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.moodtracker.model.DBUtils;
import org.example.moodtracker.model.UIUtils;
import org.example.moodtracker.model.MoodEntry; // Assuming you have a MoodEntry model class

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class tableViewController implements Initializable {

    @FXML
    private Button button_homepage;
    @FXML
    private Button button_close;
    @FXML
    private Button button_logout;
    @FXML
    private Label label_welcome;
    @FXML
    private Label current_date;
    @FXML
    private TableView<MoodEntry> tableView;
    @FXML
    private TableColumn<MoodEntry, String> dateColumn;
    @FXML
    private TableColumn<MoodEntry, String> moodColumn;
    @FXML
    private TableColumn<MoodEntry, String> activitiesColumn;
    @FXML
    private TableColumn<MoodEntry, Integer> screenTimeColumn;
    @FXML
    private TableColumn<MoodEntry, String> commentsColumn;

    private static final String DATABASE_URL = "jdbc:sqlite:moodtracker.db";


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Button functionality
        button_homepage.setOnAction(event -> DBUtils.changeScene(event, "homepage.fxml", "Home", null));
        button_logout.setOnAction(event -> DBUtils.changeScene(event, "login.fxml", "Log In", null));
        button_close.setOnAction(actionEvent -> UIUtils.closeApp((Stage) button_close.getScene().getWindow()));

        // Set user information and current date
        String loggedInUsername = "Username Goes Here"; // Replace with actual logged-in username
        UIUtils.setUserInformation(label_welcome, loggedInUsername);
        UIUtils.setCurrentDate(current_date);

        // Set up table columns
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("entryDate"));
        moodColumn.setCellValueFactory(new PropertyValueFactory<>("mood"));
        activitiesColumn.setCellValueFactory(new PropertyValueFactory<>("activityCategory"));
        screenTimeColumn.setCellValueFactory(new PropertyValueFactory<>("screenTimeHours"));
        commentsColumn.setCellValueFactory(new PropertyValueFactory<>("comments"));

        // Populate table with mood tracking data for the logged-in user
        populateMoodTrackingTable(loggedInUsername);
    }

    private void populateMoodTrackingTable(String username) {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL)) {
            // Retrieve user ID based on username
            int userId = DBUtils.getUserIdByUsername(connection, username);

            if (userId != -1) {
                // Prepare SQL query to fetch mood tracking data for the user
                String selectMoodDataSQL = "SELECT entry_date, mood, activity_category, screen_time_hours, comments " +
                                           "FROM mood_tracking WHERE user_id = ?";

                try (PreparedStatement preparedStatement = connection.prepareStatement(selectMoodDataSQL)) {
                    preparedStatement.setInt(1, userId);
                    ResultSet resultSet = preparedStatement.executeQuery();

                    // Clear existing items in the table
                    tableView.getItems().clear();

                    // Iterate through the result set and add mood entries to the table
                    while (resultSet.next()) {
                        String entryDate = resultSet.getString("entry_date");
                        String mood = resultSet.getString("mood");
                        String activityCategory = resultSet.getString("activity_category");
                        int screenTimeHours = resultSet.getInt("screen_time_hours");
                        String comments = resultSet.getString("comments");

                        MoodEntry moodEntry = new MoodEntry(entryDate, mood, activityCategory, screenTimeHours, comments);
                        tableView.getItems().add(moodEntry);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
