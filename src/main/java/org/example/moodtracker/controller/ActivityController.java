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
import java.util.List;
import java.util.ResourceBundle;

public class ActivityController implements Initializable {

    @FXML
    private Button button_activityback; // Button to navigate back to the previous screen
    @FXML
    private Button button_activitynext; // Button to navigate to the next screen

    private List<Button> selectedButtons; // List to track selected activity buttons

    /**
     * Description: Function to initialise activity page buttons, and array to track selected buttons
     * @param url The location used to resolve relative paths for the root object, or null if the location is not known.
    * @param resourceBundle The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set the action for the back button to change the scene to the mood tracking page
        button_activityback.setOnAction(event -> DBUtils.changeScene(event, "mood-tracking-page.fxml", "Mood Tracking"));

        // Set the action for the next button to call the activityNext method
        button_activitynext.setOnAction(this::activityNext);

        // Initialize the list to track selected buttons
        selectedButtons = new ArrayList<>();
    }

    /**
     * Description: Function to progress the user on to the next page of the mood entry.
     * The function collects the activities that the user has entered and progresses to the next page when the next button is clicked.
     * @param event Button click event
     */
    @FXML
    private void activityNext(ActionEvent event) {
        if (!selectedButtons.isEmpty()) {
            // Create a list to store the text of selected activities
            List<String> selectedActivities = new ArrayList<>();
            for (Button button : selectedButtons) {
                selectedActivities.add(button.getText()); // Add the text of each selected button to the list
            }

            // Get the singleton instance of MoodEntry and set the selected activities
            MoodEntry moodEntry = MoodEntry.getInstance();
            moodEntry.setSelectedActivities(selectedActivities);
            MoodEntry.setMoodEntry(moodEntry);

            // Change the scene to the screentime journal page
            DBUtils.changeScene(event, "screentime-journal_page.fxml", "Screentime");
        } else {
            // Show an alert if no activity is selected
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please select at least one activity.");
            alert.show();
        }
    }

    /**
     * Description: Function to add the selected styling to activity buttons when clicked.
     * @param event Button click event
     */
    @FXML
    private void handleActivityButtonClick(ActionEvent event) {
        // Check if the source of the event is a button
        if (event.getSource() instanceof Button clickedButton) {
            String currentStyle = clickedButton.getStyle(); // Get the current style of the button
            if (currentStyle.contains("-fx-background-color: #c7c7c7")) {
                // If the button is already selected, remove the selection style
                clickedButton.setStyle("-fx-background-color: transparent; -fx-background-radius: 40;");
                selectedButtons.remove(clickedButton); // Remove the button from the list of selected buttons
            } else {
                // If the button is not selected, add the selection style
                clickedButton.setStyle("-fx-background-color: #c7c7c7; -fx-background-radius: 40;");
                selectedButtons.add(clickedButton); // Add the button to the list of selected buttons
            }
        }
    }
}
