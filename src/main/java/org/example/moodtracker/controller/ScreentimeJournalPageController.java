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

public class ScreentimeJournalPageController implements Initializable {

    @FXML
    private Button buttonScreentimeBack;
    @FXML
    private Button buttonSubmit;
    @FXML
    private Slider screentimeSlider;
    @FXML
    private TextArea taJournalEntry;

    private int userId;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        buttonScreentimeBack.setOnAction(event -> DBUtils.changeScene(event, "activity.fxml", "Activity"));
        buttonSubmit.setOnAction(event -> submitButton(event));

        // Retrieve the user ID when the controller is initialized
        String username = DBUtils.getCurrentUsername();
        userId = DBUtils.getUserId(username);
    }

    private void submitButton(ActionEvent event) {
        int screentimeValue = (int) screentimeSlider.getValue();
        String journalEntry = taJournalEntry.getText();

        if (screentimeValue > 0) {
            LocalDate currentDate = LocalDate.now();

            MoodEntry moodEntry = MoodEntry.getMoodEntry();
            moodEntry.setEntryDate(currentDate);
            moodEntry.setScreenTime(screentimeValue);
            moodEntry.setJournalEntry(journalEntry); // This can be an empty string if not provided

            DBUtils.insertMoodEntries(List.of(moodEntry), userId);

            // Update XP in the database
            int xpToAdd = 15;

            // Add 5 XP for each selected activity
            List<String> selectedActivities = moodEntry.getActivityCategory();
            xpToAdd += selectedActivities.size() * 5;

            DBUtils.updateXpInDatabase(userId, xpToAdd);
            DBUtils.updateLevelInDatabase(userId);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Mood entry completed. You gained " + xpToAdd + " XP!");
            alert.showAndWait();

            DBUtils.changeScene(event, "homepage.fxml", "Home");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please enter a valid screen time value.");
            alert.show();
        }
    }


}
