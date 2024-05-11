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
import org.example.moodtracker.model.UserSession;

import static org.example.moodtracker.model.UIUtils.setUserInformation;

public class HomepageController implements Initializable {

    @FXML
    private Button button_logout;
    @FXML
    private Button button_close;
    @FXML
    private Button button_table;
    @FXML
    private Label label_welcome;
    @FXML
    private Label current_date;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        button_logout.setOnAction(event -> DBUtils.changeScene(event, "login.fxml", "Log In", null));
        button_close.setOnAction(actionEvent -> UIUtils.closeApp((Stage) button_close.getScene().getWindow()));
        button_table.setOnAction(event -> DBUtils.changeScene(event, "tableView.fxml", "Table View", null));

        // Set user information and current date
        // Retrieve the logged-in username from UserSession
        String username = UserSession.getUsername();

        // Display the username on the homepage
        setUserInformation(label_welcome, username);
        UIUtils.setCurrentDate(current_date);
    }

}