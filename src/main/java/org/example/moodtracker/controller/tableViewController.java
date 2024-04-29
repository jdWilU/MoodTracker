package org.example.moodtracker.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import org.example.moodtracker.model.DBUtils;
import org.example.moodtracker.model.UIUtils;


public class tableViewController implements Initializable{

    @FXML
    private Button button_homepage;
    @FXML
    private Button button_close;
    @FXML
    private Button button_logout;
    @FXML
    private Label label_welcome;
    @FXML
    private Label current_date;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Button functionality
        button_homepage.setOnAction(event -> DBUtils.changeScene(event, "homepage.fxml", "Home", null));
        button_logout.setOnAction(event -> DBUtils.changeScene(event, "login.fxml", "Log In", null));
        button_close.setOnAction(actionEvent -> UIUtils.closeApp((Stage) button_close.getScene().getWindow()));


        // Set user information and current date
        UIUtils.setUserInformation(label_welcome, "Username Goes Here");
        UIUtils.setCurrentDate(current_date);
    }
}
