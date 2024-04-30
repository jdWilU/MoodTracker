package org.example.moodtracker.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.moodtracker.model.DBUtils;
import org.example.moodtracker.model.UIUtils;
import org.example.moodtracker.model.UserInfo;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProfileSettingsController implements Initializable {

    @FXML
    private Button button_logout;
    @FXML
    private Button button_table;
    @FXML
    private Button button_homepage;
    @FXML
    private Button button_close;
    @FXML
    private Label label_welcome;
    @FXML
    private Label current_date;
    @FXML
    private TextField tf_username;
    @FXML
    private TextField tf_email;
    @FXML
    private TextField tf_password;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Button functionality
        button_homepage.setOnAction(event -> DBUtils.changeScene(event,"homepage.fxml", "Home",null));
        button_logout.setOnAction(event -> DBUtils.changeScene(event, "login.fxml", "Log In", null));
        button_table.setOnAction(event -> DBUtils.changeScene(event, "tableView.fxml", "Table View", null));
        button_close.setOnAction(actionEvent -> UIUtils.closeApp((Stage) button_close.getScene().getWindow()));

        // Set user information and current date
        String currentUser = DBUtils.getCurrentUsername();
        if (currentUser != null) {
            UIUtils.setUserInformation(label_welcome, currentUser);
        }
        UIUtils.setCurrentDate(current_date);

        // Retrieve user information
        UserInfo user = DBUtils.getUserInfo(DBUtils.getCurrentUsername());

        // Populate text fields with user information
        if (user != null) {
            tf_username.setText(user.getUsername());
            tf_email.setText(user.getEmail());
            tf_password.setText(user.getPassword());
        }
    }

}
