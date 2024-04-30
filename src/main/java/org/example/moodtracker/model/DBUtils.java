package org.example.moodtracker.model;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBUtils {
    private static final String DATABASE_URL = "jdbc:sqlite:moodtracker.db";
    private static String currentUsername; // Static variable to store the current username

    // Function to create necessary tables in SQLite database
    public static void createDatabase() {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             Statement statement = connection.createStatement()) {
            String createUserTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT UNIQUE," +
                    "email TEXT," +
                    "password TEXT)";
            statement.execute(createUserTableSQL);
            Logger.getLogger(DBUtils.class.getName()).log(Level.INFO, "Database created successfully");
        } catch (SQLException e) {
            Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, "Error creating database", e);
        }
    }

    public static void changeScene(ActionEvent event, String fxmlFile, String title, String username){
        Parent root = null;
        if (username != null){
            try {
                FXMLLoader loader = new FXMLLoader(DBUtils.class.getClassLoader().getResource(fxmlFile));
                root = loader.load();
                //HomepageController loggedInController = loader.getController();
            } catch (IOException e){
                Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, "Error loading FXML file: " + fxmlFile, e);
            }
        } else {
            try {
                root = FXMLLoader.load(Objects.requireNonNull(DBUtils.class.getClassLoader().getResource(fxmlFile)));
            } catch(IOException e){
                Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, "Error loading FXML file: " + fxmlFile, e);
            }
        }
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        assert root != null;
        stage.setScene(new Scene(root, 800, 600));
        stage.show();
    }

    public static void signUpUser(ActionEvent event, String username, String email, String password) {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement psCheckUserExists = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
             PreparedStatement psInsert = connection.prepareStatement("INSERT INTO users (username, email, password) VALUES (?, ?, ?)")) {

            psCheckUserExists.setString(1, username);
            try (ResultSet resultSet = psCheckUserExists.executeQuery()) {
                if (resultSet.isBeforeFirst()) {
                    System.out.println("User already exists!");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("You cannot use this username.");
                    alert.show();
                } else {
                    psInsert.setString(1, username);
                    psInsert.setString(2, email);
                    psInsert.setString(3, password);
                    psInsert.executeUpdate();

                    changeScene(event, "homepage.fxml", "Welcome", username);
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, "Error executing SQL queries", e);
        }
    }

    public static void logInUser(ActionEvent event, String username, String password) {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT password FROM users WHERE username = ?")) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (!resultSet.isBeforeFirst()) {
                    System.out.println("User not found in the database");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Provided credentials are incorrect!");
                    alert.show();
                } else {
                    while (resultSet.next()) {
                        String retrievedPassword = resultSet.getString("password");
                        if (retrievedPassword.equals(password)) {
                            changeScene(event, "homepage.fxml", "Welcome", username);
                        } else {
                            System.out.println("Passwords do not match!");
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setContentText("The provided credentials are incorrect!");
                            alert.show();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, "Error executing SQL queries", e);
        }
    }

    public static UserInfo getUserInfo(String username) {
        String query = "SELECT * FROM users WHERE username = ?";
        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String retrievedUsername = resultSet.getString("username");
                    String email = resultSet.getString("email");
                    String password = resultSet.getString("password");
                    return new UserInfo(retrievedUsername, email, password);
                } else {
                    // Log a warning instead of throwing an exception
                    Logger.getLogger(DBUtils.class.getName()).log(Level.WARNING, "User not found in the database");
                    return null; // Return null to indicate user not found
                }
            }
        } catch (SQLException e) {
            // Log any SQL exceptions that occur
            Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, "Error executing SQL query", e);
            return null; // Return null to indicate an error occurred
        }
    }

    public static String getCurrentUsername() {
        return currentUsername;
    }

    public static void setCurrentUsername(String username) {
        currentUsername = username;
    }
}