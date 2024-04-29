package org.example.moodtracker.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.example.moodtracker.model.DBUtils;

import java.net.URL;
import java.util.ResourceBundle;

public class ProfileSettingsController implements Initializable {
    @FXML
    private Button button_logout;
    @FXML
    private Button button_close;
    @FXML
    private Button button_table;
    @FXML
    private Button button_homepage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        button_homepage.setOnAction(event -> DBUtils.changeScene(event,"homepage.fxml", "Home",null));
        button_logout.setOnAction(event -> DBUtils.changeScene(event, "login.fxml", "Log In", null));
        button_table.setOnAction(event -> DBUtils.changeScene(event, "tableView.fxml", "Table View", null));
    }

    // Method to close the entire program
    private void closeApp() {
        Stage stage = (Stage) button_close.getScene().getWindow();
        stage.close();
    }



}
