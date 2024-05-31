package org.example.moodtracker.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import org.example.moodtracker.model.DBUtils;
import org.example.moodtracker.model.MoodEntry;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller class for the Screentime Journal Page view.
 */
public class ScreentimeJournalPageController implements Initializable {

    // FXML elements
    @FXML
    private Button buttonScreentimeBack;
    @FXML
    private Button buttonSubmit;
    @FXML
    private Slider screentimeSlider;
    @FXML
    private TextArea taJournalEntry;

    private int userId;

    /**
     * Initializes the Screentime Journal Page view.
     *
     * @param url            The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeButtons();

        // Get current user's ID
        String username = DBUtils.getCurrentUsername();
        userId = DBUtils.getUserId(username);
    }

    /**
     * Initializes the button actions.
     */
    private void initializeButtons() {
        buttonScreentimeBack.setOnAction(event -> DBUtils.changeScene(event, "activity.fxml", "Activity"));
        buttonSubmit.setOnAction(event -> submitButton(event));
    }

    /**
     * Handles the submission of screen time and journal entry.
     *
     * @param event The action event generated by clicking the submit button.
     */
    private void submitButton(ActionEvent event) {
        // Get values from UI components
        int screentimeValue = (int) screentimeSlider.getValue();
        String journalEntry = taJournalEntry.getText();

        if (screentimeValue > 0) {
            // Get current date
            LocalDate currentDate = LocalDate.now();

            // Create a new MoodEntry object
            MoodEntry moodEntry = MoodEntry.getMoodEntry();
            moodEntry.setEntryDate(currentDate);
            moodEntry.setScreenTime(screentimeValue);
            moodEntry.setJournalEntry(journalEntry);

            // Insert mood entry into the database
            DBUtils.insertMoodEntries(List.of(moodEntry), userId);

            // Calculate XP to add
            int xpToAdd = 15;
            List<String> selectedActivities = moodEntry.getActivityCategory();
            xpToAdd += selectedActivities.size() * 5;

            // Update XP in the database
            DBUtils.updateXpInDatabase(userId, xpToAdd);
            DBUtils.updateLevelInDatabase(userId);

            // Show success message
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Mood entry completed. You gained " + xpToAdd + " XP!");
            alert.showAndWait();

            // Redirect to the homepage
            DBUtils.changeScene(event, "homepage.fxml", "Home");
        } else {
            // Show error message if screen time value is invalid
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please enter a valid screen time value.");
            alert.show();
        }
    }
}
