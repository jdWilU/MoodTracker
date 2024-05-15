package org.example.moodtracker.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import org.example.moodtracker.model.DBUtils;
import org.example.moodtracker.model.MoodEntry;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MoodSelectionController implements Initializable {

    @FXML
    private Button button_moodback;
    @FXML
    private Button button_moodsubmit;
    @FXML
    private ToggleGroup moodToggleGroup;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        button_moodback.setOnAction(event -> DBUtils.changeScene(event, "homepage.fxml", "Homepage",null));
        button_moodsubmit.setOnAction(event -> moodSubmit(event));
    }

    @FXML
    private void moodSubmit(ActionEvent event) {
        RadioButton selectedRadioButton = (RadioButton) moodToggleGroup.getSelectedToggle();
        if (selectedRadioButton != null) {
            String selectedMood = selectedRadioButton.getText();

            MoodEntry moodEntry = MoodEntry.getInstance();
            moodEntry.setSelectedMood(selectedMood);
            MoodEntry.setMoodEntry(moodEntry);

            DBUtils.changeScene(event, "activity.fxml", "Activity Page", null);
        } else {
            // Show an alert if no mood is selected
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please select a mood.");
            alert.show();
        }
    }


}
