package org.example.moodtracker.controller;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import org.example.moodtracker.model.DBUtils;

import java.net.URL;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    @FXML
    private Button button_login;
    @FXML
    private Button button_signup;
    @FXML
    private MFXTextField mfx_username;
    @FXML
    private MFXPasswordField mxf_password;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        button_login.setOnAction(event -> DBUtils.logInUser(event, mfx_username.getText(), mxf_password.getText()));
        button_signup.setOnAction(event -> DBUtils.changeScene(event, "sign-up.fxml", "Sign Up", null));
    }
}