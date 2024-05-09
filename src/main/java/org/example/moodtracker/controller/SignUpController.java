package org.example.moodtracker.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.moodtracker.model.DBUtils;

import java.net.URL;
import java.util.ResourceBundle;

public class SignUpController implements Initializable {
    @FXML
    private Button button_signup;
    @FXML
    private Button button_login;
    @FXML
    private TextField tf_username;
    @FXML
    private TextField tf_email;
    @FXML
    private PasswordField pf_password;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        button_signup.setOnAction(event -> {
            if(!tf_username.getText().trim().isEmpty() && !tf_email.getText().trim().isEmpty() && !pf_password.getText().trim().isEmpty()) {
                DBUtils.signUpUser(event, tf_username.getText(), tf_email.getText(), pf_password.getText());
            } else {
                System.out.println("Please fill in all information");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Please fill in all information to sign up");
                alert.show();
            }
        });

        button_login.setOnAction(event -> DBUtils.changeScene(event, "login.fxml", "Log In", null));
    }
}

