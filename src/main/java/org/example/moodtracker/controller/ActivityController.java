package org.example.moodtracker.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import org.example.moodtracker.model.DBUtils;

public class ActivityController {

    @FXML
    private Button button_back;

    @FXML
    private Button button_next;


    @FXML
    public void initialize() {
        // Configure the BACK button action to change to a previous scene or activity
        button_back.setOnAction(event -> DBUtils.changeScene(event, "mood-tracking-page.fxml", "Mood Selection", null));

        // Configure the NEXT button action to change to a next scene or activity
        button_next.setOnAction(event -> DBUtils.changeScene(event, "Screentime-Journal_page.fxml", "Screen Time", null));
    }
}