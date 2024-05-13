package org.example.moodtracker.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import org.example.moodtracker.model.DBUtils;
import org.example.moodtracker.model.MoodEntry;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;

public class MoodSelectionController implements Initializable {

    @FXML
    private Button button_moodback;
    @FXML
    private Button button_moodsubmit;
    @FXML
    private MoodEntry moodEntry;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        button_moodback.setOnAction(event -> DBUtils.changeScene(event, "homepage.fxml", "Homepage",null));
        button_moodsubmit.setOnAction(event -> DBUtils.changeScene(event, "activity.fxml","Activity Page",null));
    }

    @FXML
    private void NextButtonClick(ActionEvent event) {
        // Collect mood entry data
        MoodEntry entry = new MoodEntry(entryDate, screenTimeHours, activityCategory, comments);
        // Add the entry to the mood entry manager
        moodEntry.addMoodEntry(entry);
        // Navigate to the next page
    }
}
