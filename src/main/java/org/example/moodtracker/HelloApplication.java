package org.example.moodtracker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

import static org.example.moodtracker.model.DBUtils.createDatabase;

// purple: #a364f8
// red: #fe6969
// blue: #2cb2ff
// green: #20e49f

public class HelloApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        createDatabase();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("login.fxml")));
        primaryStage.setTitle("Log In");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}