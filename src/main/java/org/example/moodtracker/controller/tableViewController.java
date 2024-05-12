package org.example.moodtracker.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import org.example.moodtracker.model.MoodEntry;

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
    private Button button_profile;
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
    private TableColumn<MoodEntry, Integer > screenTimeColumn;
    @FXML
    private TableColumn<MoodEntry, String> commentsColumn;

    private static final String DATABASE_URL = "jdbc:sqlite:moodtracker.db";


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Button functionality
        button_homepage.setOnAction(event -> DBUtils.changeScene(event, "homepage.fxml", "Home", null));
        button_logout.setOnAction(event -> DBUtils.changeScene(event, "login.fxml", "Log In", null));
        button_close.setOnAction(actionEvent -> UIUtils.closeApp((Stage) button_close.getScene().getWindow()));

        button_profile.setOnAction(event -> DBUtils.changeScene(event,"profile.fxml","Profile",null));

        // Set user information and current date
        String currentUser = DBUtils.getCurrentUsername();
        if (currentUser != null) {
            UIUtils.setUserInformation(label_welcome, currentUser);
        }
        UIUtils.setCurrentDate(current_date);

        // Configure table columns to use properties of MoodEntry
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("entryDate"));
        moodColumn.setCellValueFactory(new PropertyValueFactory<>("mood"));
        activitiesColumn.setCellValueFactory(new PropertyValueFactory<>("activityCategory"));
        screenTimeColumn.setCellValueFactory(new PropertyValueFactory<>("screenTimeHours"));
        commentsColumn.setCellValueFactory(new PropertyValueFactory<>("comments"));

        // Load data into the table view
        loadData();
    }

    private void loadData() {
        ObservableList<MoodEntry> moodEntries = FXCollections.observableArrayList();

        // Retrieve the logged-in user's username
        String loggedInUsername = DBUtils.getCurrentUsername();
        System.out.println(loggedInUsername);

        if (loggedInUsername != null && !loggedInUsername.isEmpty()) {
            try (Connection conn = DriverManager.getConnection(DATABASE_URL);
                 PreparedStatement getUserIdStmt = conn.prepareStatement(
                         "SELECT user_id FROM users WHERE username = ?");
                 PreparedStatement getMoodEntriesStmt = conn.prepareStatement(
                         "SELECT * FROM mood_tracking WHERE user_id = ?")) {

                // Set parameter for retrieving user_id based on username
                getUserIdStmt.setString(1, loggedInUsername);

                // Execute query to retrieve user_id
                int loggedInUserId = -1; // Initialize user_id with default value
                try (ResultSet userIdResult = getUserIdStmt.executeQuery()) {
                    if (userIdResult.next()) {
                        loggedInUserId = userIdResult.getInt("user_id"); // Retrieve user_id
                    }
                }

                if (loggedInUserId > 0) { // Check if valid user_id is retrieved
                    // Set parameter for retrieving mood entries based on user_id
                    getMoodEntriesStmt.setInt(1, loggedInUserId);

                    // Execute query to retrieve mood entries for the logged-in user
                    try (ResultSet moodEntriesResult = getMoodEntriesStmt.executeQuery()) {
                        while (moodEntriesResult.next()) {
                            String entryDate = moodEntriesResult.getString("entry_date");
                            String mood = moodEntriesResult.getString("mood");
                            String activityCategory = moodEntriesResult.getString("activity_category");
                            int screenTimeHours = moodEntriesResult.getInt("screen_time_hours");
                            String comments = moodEntriesResult.getString("comments");

                            MoodEntry moodEntry = new MoodEntry(entryDate, mood, activityCategory, screenTimeHours, comments);
                            moodEntries.add(moodEntry);
                        }
                    }
                } else {
                    System.err.println("User not found or invalid user_id retrieved for the logged-in user.");
                }

            } catch (SQLException e) {
                System.err.println("Error loading data from database: " + e.getMessage());
            }
        } else {
            System.err.println("Logged-in username is invalid or not available.");
        }

        // Set the items in the table view
        tableView.setItems(moodEntries);
    }

}
