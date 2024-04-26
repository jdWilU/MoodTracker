package org.example.moodtracker.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.moodtracker.model.DBUtils;

public class LoggedInController implements Initializable {

    @FXML
    private Button button_logout;
    @FXML
    private Button button_close;
    @FXML
    private Label label_welcome;
    @FXML
    private Label current_date;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        button_logout.setOnAction(event -> DBUtils.changeScene(event, "login.fxml", "Log In", null));
        button_close.setOnAction(actionEvent -> closeApp());
    }

    public void setUserInformation(String username){
        label_welcome.setText(("Welcome, " + username + " !"));
    }

    // Method to set the current date in the label
    public void setCurrentDate() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = currentDate.format(formatter);
        current_date.setText("Today's Date: " + formattedDate);
    }

    // Method to close the entire program
    private void closeApp() {
        Stage stage = (Stage) button_close.getScene().getWindow();
        stage.close();
    }

}
