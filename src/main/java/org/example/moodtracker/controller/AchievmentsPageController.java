package org.example.moodtracker.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import org.example.moodtracker.model.DBUtils;

import java.net.URL;
import java.util.ResourceBundle;

public class AchievmentsPageController implements Initializable {

    @FXML
    private Button buttonAchievementBack;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        buttonAchievementBack.setOnAction(event -> DBUtils.changeScene(event,"homepage.fxml","Welcome",null));
    }
}
