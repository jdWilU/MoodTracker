package org.example.moodtracker.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import org.example.moodtracker.model.DBUtils;
import org.example.moodtracker.model.MoodEntry;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller class for the mood selection page.
 * Allows users to select their current mood and submit it.
 */
public class MoodSelectionController implements Initializable {

    @FXML
    private Button button_moodback;
    @FXML
    private Button button_moodsubmit;
    @FXML
    private ToggleGroup moodToggleGroup;

    /**
     * Initializes the mood selection page.
     * Sets action event handlers for the back and submit buttons.
     *
     * @param url            The location used to resolve relative paths for the root object.
     * @param resourceBundle The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        button_moodback.setOnAction(event -> DBUtils.changeScene(event, "homepage.fxml", "Homepage"));
        button_moodsubmit.setOnAction(event -> moodSubmit(event));
    }

    /**
     * Handles the submission of the selected mood.
     * If a mood is selected, it sets the selected mood and navigates to the activity page.
     * If no mood is selected, it displays an error alert.
     *
     * @param event The action event triggered by clicking the submit button.
     */
    @FXML
    private void moodSubmit(ActionEvent event) {
        RadioButton selectedRadioButton = (RadioButton) moodToggleGroup.getSelectedToggle();
        if (selectedRadioButton != null) {
            String selectedMood = selectedRadioButton.getText();

            MoodEntry moodEntry = MoodEntry.getInstance();
            moodEntry.setSelectedMood(selectedMood);
            MoodEntry.setMoodEntry(moodEntry);

            DBUtils.changeScene(event, "activity.fxml", "Activity Page");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please select a mood.");
            alert.show();
        }
    }
}
