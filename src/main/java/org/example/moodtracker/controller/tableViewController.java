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
import org.example.moodtracker.model.UserSession;

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

        // Set user information and current date
        String loggedInUsername = "Username Goes Here"; // Replace with actual logged-in username
        UIUtils.setUserInformation(label_welcome, loggedInUsername);
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

        // Retrieve the logged-in user's user_id (assuming UserSession is used to store user information)
        int loggedInUserId = UserSession.getUserId();

        //Check correct user id
        System.out.println(loggedInUserId);

        if (loggedInUserId > 0) { // Check if user_id is valid (greater than 0)
            try (Connection conn = DriverManager.getConnection(DATABASE_URL);
                 PreparedStatement stmt = conn.prepareStatement(
                         "SELECT m.* FROM mood_tracking m INNER JOIN users u ON m.user_id = u.user_id WHERE u.user_id = ?")) {

                // Set the user_id parameter in the prepared statement
                stmt.setInt(1, loggedInUserId);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String entryDate = rs.getString("entry_date");
                        String mood = rs.getString("mood");
                        String activityCategory = rs.getString("activity_category");
                        int screenTimeHours = rs.getInt("screen_time_hours");
                        String comments = rs.getString("comments");

                        MoodEntry moodEntry = new MoodEntry(entryDate, mood, activityCategory, screenTimeHours, comments);
                        moodEntries.add(moodEntry);
                    }
                }

            } catch (SQLException e) {
                System.err.println("Error loading data from database: " + e.getMessage());
            }
        } else {
            System.err.println("Logged-in user_id is invalid or not available.");
        }

        // Set the items in the table view
        tableView.setItems(moodEntries);
    }

}
