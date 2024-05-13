package org.example.moodtracker.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import org.example.moodtracker.model.DBUtils;

import java.net.URL;
import java.util.ResourceBundle;

public class MoodSelectionController implements Initializable {

    @FXML
    private Button button_moodback;
    @FXML
    private Button button_moodsubmit;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        button_moodback.setOnAction(event -> DBUtils.changeScene(event, "homepage.fxml", "Homepage",null));
        button_moodsubmit.setOnAction(event -> DBUtils.changeScene(event, "activity.fxml","Activity Page",null));
    }
}
