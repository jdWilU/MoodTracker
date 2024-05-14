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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBUtils {
    private static final String DATABASE_URL = "jdbc:sqlite:moodtracker.db";
    private static String currentUsername;
    private static String currentEmail;
    private static String currentPassword; // Static variable to store the current username



    // Function to create necessary tables in SQLite database
    public static void createDatabase() {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             Statement statement = connection.createStatement()) {
            // Create User table
            String createUserTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                    "user_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT UNIQUE," +
                    "email TEXT," +
                    "password TEXT)";
            statement.execute(createUserTableSQL);
            Logger.getLogger(DBUtils.class.getName()).log(Level.INFO, "User Database created successfully!");

            // Create mood tracking table
            String createMoodTableSQL = "CREATE TABLE IF NOT EXISTS mood_tracking (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER," +  // Foreign key to link to users table
                    "entry_date DATE," +   // Date of the entry
                    "mood TEXT CHECK (mood IN ('BAD', 'POOR', 'OKAY', 'GOOD', 'GREAT'))," +  // Mood category
                    "screen_time_hours INTEGER," +  // Screen time in hours
                    "activity_category TEXT CHECK (activity_category IN ('Exercise', 'Meditation', 'Socializing', 'Sleep ', 'Journaling', 'Hobbies', 'Helping Others'))," +  // Category of activity
                    "comments TEXT," +  // Additional comments
                    "FOREIGN KEY(user_id) REFERENCES users(user_id))";  // Foreign key constraint
            statement.execute(createMoodTableSQL);
            Logger.getLogger(DBUtils.class.getName()).log(Level.INFO, "Mood Tracking Table created successfully!");

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

                    setCurrentUsername(username);
                    setCurrentEmail(email);
                    setCurrentPassword(password);
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
                            setCurrentUsername(username);
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

    // User details: getters & setters
    public static String getCurrentUsername() {
        return currentUsername;
    }
    public static void setCurrentUsername(String username) {
        currentUsername = username;
    }
    public static String getCurrentEmail() {
        return currentEmail;
    }
    public static void setCurrentEmail(String email) {
        currentEmail = email;
    }
    public static String getCurrentPassword() {
        return currentPassword;
    }
    public static void setCurrentPassword(String password) {
        currentPassword = password;
    }

    public static void updateUserInfo(String username, String newUsername, String newEmail, String newPassword) throws SQLException {
        String query = "UPDATE users SET username = ?, email = ?, password = ? WHERE username = ?";
        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, newUsername);
            preparedStatement.setString(2, newEmail);
            preparedStatement.setString(3, newPassword);
            preparedStatement.setString(4, username);
            preparedStatement.executeUpdate();
        }
    }

    public static void deleteUser(String username) throws SQLException {
        // SQL query to delete the user's record from the database
        String query = "DELETE FROM users WHERE username = ?";

        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            // Set the username parameter
            preparedStatement.setString(1, username);

            // Execute the DELETE query
            preparedStatement.executeUpdate();

            Logger.getLogger(DBUtils.class.getName()).log(Level.INFO, "User deleted successfully");
        } catch (SQLException e) {
            // Log any SQL exceptions that occur
            Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, "Error deleting user", e);
            throw e; // Re-throw the exception to be handled by the caller
        }
    }


    public static Map<String, Integer> getMoodCountsForUser(String username) throws SQLException {
        String query = "SELECT mood, COUNT(*) AS count FROM mood_tracking WHERE user_id = (SELECT user_id FROM users WHERE username = ?) GROUP BY mood";
        Map<String, Integer> moodCounts = new HashMap<>();

        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String mood = resultSet.getString("mood");
                int count = resultSet.getInt("count");
                moodCounts.put(mood, count);
            }
        }

        return moodCounts;
    }
}