package org.example.moodtracker.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import org.example.moodtracker.model.DBUtils;

public class MoodSelectionController {

    @FXML
    private Button button_submit;

    @FXML
    private RadioButton rb1, rb2, rb3, rb4, rb5;

    private ToggleGroup group;

    @FXML
    public void initialize() {
        // Initialize the ToggleGroup
        group = new ToggleGroup();
        rb1.setToggleGroup(group);
        rb2.setToggleGroup(group);
        rb3.setToggleGroup(group);
        rb4.setToggleGroup(group);
        rb5.setToggleGroup(group);

        // Configure the submit button action
        button_submit.setOnAction(event -> DBUtils.changeScene(event, "activity.fxml", "Activity Selection", null));
    }
}