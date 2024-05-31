package org.example.moodtracker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * The main application class that launches the MoodTracker application.
 */
public class HelloApplication extends Application {

    /**
     * Starts the JavaFX application by loading the login scene.
     *
     * @param primaryStage The primary stage of the application.
     * @throws Exception If an error occurs during application startup.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the login scene
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("login.fxml")));
        primaryStage.setTitle("Log In");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    /**
     * The main method that launches the JavaFX application.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        launch();
    }
}
