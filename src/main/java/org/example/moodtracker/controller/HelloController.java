package org.example.moodtracker.controller;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import org.example.moodtracker.model.DBUtils;
import java.net.URL;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    @FXML
    private Label errorLabel;
    @FXML
    private Button button_login;
    @FXML
    private Button button_signup;
    @FXML
    private MFXTextField mfx_username;
    @FXML
    private MFXPasswordField mxf_password;

    // Login button action
    @FXML
    public void handleLogin(ActionEvent event) {
        String username = mfx_username.getText();
        String password = mxf_password.getText();
        DBUtils.logInUser(event, username, password, errorLabel);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        button_login.setOnAction(event -> DBUtils.logInUser(event, mfx_username.getText(), mxf_password.getText(), errorLabel));
        button_signup.setOnAction(event -> DBUtils.changeScene(event, "sign-up.fxml", "Sign Up", null));
    }
}