package org.example.moodtracker;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class LoggedInController implements Initializable {

    @FXML
    private Button button_logout;
    @FXML
    private Label label_welcome;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        button_logout.setOnAction(event -> DBUtils.changeScene(event, "sample.fxml", "Log In", null));

    }

    public void setUserInformation(String username){
        label_welcome.setText(("Welcome " + username));
    }
}
