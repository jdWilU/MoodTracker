package org.example.moodtracker.controller;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import org.example.moodtracker.model.DBUtils;
import java.net.URL;
import java.util.ResourceBundle;

public class SignUpController implements Initializable {
    @FXML
    private Label errorLabel ;
    @FXML
    private Button button_signup;
    @FXML
    private Button button_login;
    @FXML
    private MFXTextField mfx_username;
    @FXML
    private MFXTextField mfx_email;
    @FXML
    private MFXPasswordField mxf_password;
    @FXML
    private ProgressBar strengthProgressBar;
    @FXML
    private Label strengthLabel;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set action for sign up button
        button_signup.setOnAction(event -> {
            if (!mfx_username.getText().trim().isEmpty() && !mfx_email.getText().trim().isEmpty() && !mxf_password.getText().trim().isEmpty()) {
                // Check password strength and sign up user
                PasswordStrength strength = calculatePasswordStrength(mxf_password.getText());
                if (strength != null) {
                    if (strength == PasswordStrength.WEAK) {
                        errorLabel.setText("Password is too weak. Please choose a stronger password.");
                    } else {
                        DBUtils.signUpUser(event, mfx_username.getText(), mfx_email.getText(), mxf_password.getText(), errorLabel);
                    }
                }
            } else {
                System.out.println("Please fill in all information");
                errorLabel.setText("Please fill in all information to sign up");
            }
        });

        // Set action for login button
        button_login.setOnAction(event -> DBUtils.changeScene(event, "login.fxml", "Log In", null));

        // Update password strength indicator as password is typed
        mxf_password.textProperty().addListener((observable, oldValue, newValue) -> {
            PasswordStrength strength = calculatePasswordStrength(newValue);
            updatePasswordStrengthIndicator(strength);
        });
    }

    // Method to calculate password strength
    private PasswordStrength calculatePasswordStrength(String password) {
        if (password.length() < 8) {
            return PasswordStrength.WEAK;
        } else if (password.matches(".*[a-z].*") && password.matches(".*[A-Z].*") &&
                   password.matches(".*\\d.*") && password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            return PasswordStrength.STRONG;
        } else {
            return PasswordStrength.MEDIUM;
        }
    }

    // Method to update password strength indicator
    private void updatePasswordStrengthIndicator(PasswordStrength strength) {
        if (strength != null) {
            switch (strength) {
                case WEAK:
                    strengthProgressBar.setProgress(0.33);
                    strengthLabel.setText("Weak");
                    strengthLabel.setTextFill(Color.RED);
                    setProgressBarColor(strengthProgressBar, Color.RED);

                    break;
                case MEDIUM:
                    strengthProgressBar.setProgress(0.66);
                    strengthLabel.setText("Medium");
                    strengthLabel.setTextFill(Color.ORANGE);
                    setProgressBarColor(strengthProgressBar, Color.ORANGE);
                    break;
                case STRONG:
                    strengthProgressBar.setProgress(1.0);
                    strengthLabel.setText("Strong");
                    strengthLabel.setTextFill(Color.GREEN);
                    setProgressBarColor(strengthProgressBar, Color.GREEN);
                    break;
                default:
                    strengthProgressBar.setProgress(0.0);
                    strengthLabel.setText("");
                    break;
            }
        } else {
            strengthProgressBar.setProgress(0.0);
            strengthLabel.setText("");
        }
    }

    // Method to set the color of the ProgressBar
    private void setProgressBarColor(ProgressBar progressBar, Color color) {
        String colorStyle = String.format("-fx-accent: #%02x%02x%02x;",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
        progressBar.setStyle(colorStyle);
    }

    // Enum representing password strength levels
    private enum PasswordStrength {
        WEAK, MEDIUM, STRONG
    }
}
