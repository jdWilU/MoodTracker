package org.example.moodtracker.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.Node;
import org.example.moodtracker.model.DBUtils;

import java.net.URL;
import java.util.ResourceBundle;

public class ActivityController implements Initializable {

    @FXML
    private Button button_activityback;
    @FXML
    private Button button_activitynext;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        button_activityback.setOnAction(event -> DBUtils.changeScene(event, "mood-tracking-page.fxml","Mood Tracking",null));
        button_activitynext.setOnAction(event -> DBUtils.changeScene(event, "screentime-journal_page.fxml","Screentime",null));
    }
}