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

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM mood_tracking")) {

            while (rs.next()) {
                String entryDate = rs.getString("entry_date");
                String mood = rs.getString("mood");
                String activityCategory = rs.getString("activity_category");
                int screenTimeHours = rs.getInt("screen_time_hours");
                String comments = rs.getString("comments");

                MoodEntry moodEntry = new MoodEntry(entryDate, mood, activityCategory, screenTimeHours, comments);
                moodEntries.add(moodEntry);
            }

        } catch (SQLException e) {
            System.err.println("Error loading data from database: " + e.getMessage());
        }

        // Set the items in the table view
        tableView.setItems(moodEntries);
    }

}
