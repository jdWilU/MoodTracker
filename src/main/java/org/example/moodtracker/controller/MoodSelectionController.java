package org.example.moodtracker.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import org.example.moodtracker.model.DBUtils;

public class MoodSelectionController {

    @FXML
    private Button button_submit;

    @FXML
    public void initialize() {
        // Configure the submit button action to change scenes
        button_submit.setOnAction(event -> DBUtils.changeScene(event, "activity.fxml", "Activity Selection", null));
    }
}