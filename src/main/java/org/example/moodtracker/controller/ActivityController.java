package org.example.moodtracker.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import org.example.moodtracker.model.DBUtils;
import org.example.moodtracker.model.MoodEntry;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class ActivityController implements Initializable {

    @FXML
    private Button button_activityback;
    @FXML
    private Button button_activitynext;

    private List<Button> selectedButtons; // Track selected buttons

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        button_activityback.setOnAction(event -> DBUtils.changeScene(event, "mood-tracking-page.fxml", "Mood Tracking", null));
        button_activitynext.setOnAction(this::activityNext);
        selectedButtons = new ArrayList<>();
    }

    @FXML
    private void activityNext(ActionEvent event) {
        if (!selectedButtons.isEmpty()) {
            List<String> selectedActivities = new ArrayList<>();
            for (Button button : selectedButtons) {
                selectedActivities.add(button.getText());
            }

            MoodEntry moodEntry = MoodEntry.getInstance();
            moodEntry.setSelectedActivities(selectedActivities);
            MoodEntry.setMoodEntry(moodEntry);

            DBUtils.changeScene(event, "screentime-journal_page.fxml", "Screentime", null);
        } else {
            // Show an alert if no activity is selected
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please select at least one activity.");
            alert.show();
        }
    }

    @FXML
    private void handleActivityButtonClick(ActionEvent event) {
        if (event.getSource() instanceof Button clickedButton) {
            String currentStyle = clickedButton.getStyle();
            if (currentStyle.contains("-fx-background-color: #c7c7c7")) {
                // If already selected, remove the background color
                clickedButton.setStyle("-fx-background-color: transparent; -fx-background-radius: 40;");
                selectedButtons.remove(clickedButton);
            } else {
                // If not selected, set the background color to #c7c7c7
                clickedButton.setStyle("-fx-background-color: #c7c7c7; -fx-background-radius: 40;");
                selectedButtons.add(clickedButton);
            }
        }
    }



}
