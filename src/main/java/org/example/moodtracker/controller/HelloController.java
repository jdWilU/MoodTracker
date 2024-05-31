package org.example.moodtracker.controller;

import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import org.example.moodtracker.model.DBUtils;
import java.net.URL;
import java.util.ResourceBundle;

public class HelloController implements Initializable {

    @FXML
    private Label errorLabel; // Label to display error messages
    @FXML
    private Button button_login; // Button to trigger the login action
    @FXML
    private Button button_signup; // Button to navigate to the sign-up page
    @FXML
    private MFXTextField mfx_username; // Text field to enter the username
    @FXML
    private MFXPasswordField mxf_password; // Password field to enter the password

    /**
     * Initializes the controller class. This method is automatically called after the FXML file has been loaded.
     *
     * @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set the action for the login button to call the logInUser method in DBUtils
        button_login.setOnAction(event -> DBUtils.logInUser(event, mfx_username.getText(), mxf_password.getText(), errorLabel));

        // Set the action for the sign-up button to change the scene to the sign-up page
        button_signup.setOnAction(event -> DBUtils.changeScene(event, "sign-up.fxml", "Sign Up"));
    }
}
