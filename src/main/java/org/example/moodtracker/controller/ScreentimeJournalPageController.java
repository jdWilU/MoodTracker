package org.example.moodtracker.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import org.example.moodtracker.model.DBUtils;
import org.sqlite.core.DB;

import java.net.URL;
import java.util.ResourceBundle;

public class ScreentimeJournalPageController implements Initializable {

    @FXML
    private Button button_screentime_back;
    @FXML
    private Button button_submitscreentime;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        button_screentime_back.setOnAction(event -> DBUtils.changeScene(event,"activity.fxml","Activity",null));
        button_submitscreentime.setOnAction(event -> DBUtils.changeScene(event,"homepage.fxml","Homepage",null));
    }
}
