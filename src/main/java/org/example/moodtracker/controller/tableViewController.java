package org.example.moodtracker.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.filter.IntegerFilter;
import io.github.palexdev.materialfx.filter.StringFilter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import org.example.moodtracker.model.DBUtils;
import org.example.moodtracker.model.UIUtils;
import org.example.moodtracker.model.MoodEntry;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class tableViewController implements Initializable {

    @FXML
    private MFXButton button_homepage;
    @FXML
    private Button button_close;
    @FXML
    private Button button_logout;
    @FXML
    private MFXButton button_profile;
    @FXML
    private MFXButton button_resources;
    @FXML
    private MFXButton button_daily_entry;
    @FXML
    private ProgressBar xpLevelTopBar;
    @FXML
    private Label levelLabelTopBar;
    @FXML
    private Label label_welcome;
    @FXML
    private Label current_date;
    @FXML
    private MFXTableView<MoodEntry> MFXTableView;

    private static final String DATABASE_URL = "jdbc:sqlite:moodtracker.db";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Button functionality
        button_homepage.setOnAction(event -> DBUtils.changeScene(event, "homepage.fxml", "Home"));
        button_logout.setOnAction(event -> DBUtils.changeScene(event, "login.fxml", "Log In"));
        button_close.setOnAction(actionEvent -> UIUtils.closeApp((Stage) button_close.getScene().getWindow()));
        button_profile.setOnAction(event -> DBUtils.changeScene(event, "profile.fxml", "Profile"));
        button_daily_entry.setOnAction(event -> DBUtils.changeScene(event, "mood-tracking-page.fxml", "Mood Tracking"));
        button_resources.setOnAction(event -> DBUtils.changeScene(event, "resources-page.fxml", "Educational Resources"));

        // Set user information and current date
        String currentUser = DBUtils.getCurrentUsername();
        if (currentUser != null) {
            UIUtils.setUserInformation(label_welcome, currentUser);
            initializeXPBar(currentUser);
        }
        UIUtils.setCurrentDate(current_date);

        // Set up the MaterialFX table
        setupMFXTable();

        // Load data into the table view
        loadData();
    }

    public void initializeXPBar(String currentUser) {
        if (currentUser != null) {
            try {
                int userId = DBUtils.getUserId(currentUser);
                int xp = DBUtils.getXpForUser(userId);
                int level = DBUtils.getUserLevel(userId);
                int xpForCurrentLevel = xp - (level * 100);
                double progress = (double) xpForCurrentLevel / 100.0;

                // Set progress for the XP bar
                xpLevelTopBar.setProgress(progress);
                levelLabelTopBar.setText("" + level);
            } catch (SQLException e) {
                // Handle SQLException
                Logger.getLogger(HomepageController.class.getName()).log(Level.SEVERE, "Error fetching user's level and XP", e);
            }
        }
    }

    private void setupMFXTable() {
        MFXTableColumn<MoodEntry> dateColumn = new MFXTableColumn<>("Date", true, Comparator.comparing(MoodEntry::getEntryDate));
        MFXTableColumn<MoodEntry> moodColumn = new MFXTableColumn<>("Mood", true, Comparator.comparing(MoodEntry::getMood));
        MFXTableColumn<MoodEntry> activitiesColumn = new MFXTableColumn<>("Activities", true, Comparator.comparing(entry -> String.join(",", entry.getActivityCategory())));
        MFXTableColumn<MoodEntry> screenTimeColumn = new MFXTableColumn<>("Screen Time", true, Comparator.comparingInt(MoodEntry::getScreenTimeHours));
        MFXTableColumn<MoodEntry> commentsColumn = new MFXTableColumn<>("Comments", true, Comparator.comparing(MoodEntry::getComments));

        dateColumn.setRowCellFactory(entry -> new MFXTableRowCell<>(MoodEntry::getEntryDate));
        moodColumn.setRowCellFactory(entry -> new MFXTableRowCell<>(MoodEntry::getMood));
        activitiesColumn.setRowCellFactory(entry -> new MFXTableRowCell<>(MoodEntry -> String.join(",", entry.getActivityCategory())));
        screenTimeColumn.setRowCellFactory(entry -> new MFXTableRowCell<>(MoodEntry::getScreenTimeHours) {{
            setAlignment(Pos.CENTER_RIGHT);
        }});
        commentsColumn.setRowCellFactory(entry -> new MFXTableRowCell<>(MoodEntry::getComments));

        MFXTableView.getTableColumns().addAll(dateColumn, moodColumn, activitiesColumn, screenTimeColumn, commentsColumn);

        MFXTableView.getFilters().addAll(
            new StringFilter<>("Date", entry -> entry.getEntryDate().toString()),  // Convert LocalDate to String
            new StringFilter<>("Mood", MoodEntry::getMood),
            new StringFilter<>("Activities", entry -> String.join(",", entry.getActivityCategory())),
            new IntegerFilter<>("Screen Time", MoodEntry::getScreenTimeHours),
            new StringFilter<>("Comments", MoodEntry::getComments)
        );
    }


    private void loadData() {
        ObservableList<MoodEntry> moodEntries = FXCollections.observableArrayList();

        String loggedInUsername = DBUtils.getCurrentUsername();

        if (loggedInUsername != null && !loggedInUsername.isEmpty()) {
            try (Connection conn = DriverManager.getConnection(DATABASE_URL);
                 PreparedStatement getUserIdStmt = conn.prepareStatement("SELECT user_id FROM users WHERE username = ?");
                 PreparedStatement getMoodEntriesStmt = conn.prepareStatement("SELECT * FROM mood_tracking WHERE user_id = ?")) {

                getUserIdStmt.setString(1, loggedInUsername);

                int loggedInUserId = -1;
                try (ResultSet userIdResult = getUserIdStmt.executeQuery()) {
                    if (userIdResult.next()) {
                        loggedInUserId = userIdResult.getInt("user_id");
                    }
                }

                if (loggedInUserId > 0) {
                    getMoodEntriesStmt.setInt(1, loggedInUserId);

                    try (ResultSet moodEntriesResult = getMoodEntriesStmt.executeQuery()) {
                        while (moodEntriesResult.next()) {
                            String entryDateString = moodEntriesResult.getString("entry_date");
                            LocalDate entryDate = LocalDate.parse(entryDateString); // Directly parse the date string

                            String mood = moodEntriesResult.getString("mood");
                            String activityCategory = moodEntriesResult.getString("activity_category");
                            int screenTimeHours = moodEntriesResult.getInt("screen_time_hours");
                            String comments = moodEntriesResult.getString("comments");

                            List<String> activityList = new ArrayList<>();
                            if (activityCategory != null && !activityCategory.isEmpty()) {
                                activityList = Arrays.asList(activityCategory.split(","));
                            }

                            MoodEntry moodEntry = new MoodEntry();
                            moodEntry.setEntryDate(entryDate);
                            moodEntry.setSelectedMood(mood);
                            moodEntry.setSelectedActivities(activityList);
                            moodEntry.setScreenTime(screenTimeHours);
                            moodEntry.setJournalEntry(comments);

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

        MFXTableView.setItems(moodEntries);
    }
}
