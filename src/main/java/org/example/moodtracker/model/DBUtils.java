package org.example.moodtracker.model;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
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
                    "user_id INTEGER," +
                    "entry_date DATE," +
                    "mood TEXT CHECK (mood IN ('BAD', 'POOR', 'OKAY', 'GOOD', 'GREAT'))," +
                    "screen_time_hours INTEGER," +
                    "activity_category TEXT," +
                    "comments TEXT," +
                    "FOREIGN KEY(user_id) REFERENCES users(user_id))";
            statement.execute(createMoodTableSQL);
            Logger.getLogger(DBUtils.class.getName()).log(Level.INFO, "Mood Tracking Table created successfully!");

            Logger.getLogger(DBUtils.class.getName()).log(Level.INFO, "Database created successfully");
        } catch (SQLException e) {
            Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, "Error creating database", e);
        }
    }

    public static void changeScene(ActionEvent event, String fxmlFile, String title, String username) {
        Parent root = null;
        try {
            URL resource = DBUtils.class.getClassLoader().getResource(fxmlFile);
            if (resource == null) {
                System.err.println("Resource is null! Check the file path: " + fxmlFile);
                return;
            }
            System.out.println("Loading FXML from: " + resource);
            FXMLLoader loader = new FXMLLoader(resource);
            root = loader.load();
        } catch (IOException e) {
            Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, "Error loading FXML file: " + fxmlFile, e);
        }

        if (root == null) {
            System.err.println("Root is null, cannot change scene. Check FXML file: " + fxmlFile);
            return;
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root, 800, 600));
        stage.show();
    }

    public static void signUpUser(ActionEvent event, String username, String email, String password, Label errorLabel) {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement psCheckUserExists = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
             PreparedStatement psInsert = connection.prepareStatement("INSERT INTO users (username, email, password) VALUES (?, ?, ?)")) {

            psCheckUserExists.setString(1, username);
            try (ResultSet resultSet = psCheckUserExists.executeQuery()) {
                if (resultSet.isBeforeFirst()) {
                    System.out.println("User already exists!");
                    errorLabel.setText("You cannot use this username.");
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
            errorLabel.setText("An error occurred while signing up.");
        }
    }

    public static void logInUser(ActionEvent event, String username, String password, Label errorLabel) {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT password FROM users WHERE username = ?")) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (!resultSet.isBeforeFirst()) {
                    System.out.println("User not found in the database");
                    errorLabel.setText("Password or Username are Incorrect!");
                } else {
                    while (resultSet.next()) {
                        String retrievedPassword = resultSet.getString("password");
                        if (retrievedPassword.equals(password)) {
                            setCurrentUsername(username);
                            changeScene(event, "homepage.fxml", "Welcome", username);
                        } else {
                            System.out.println("Passwords do not match!");
                            errorLabel.setText("Password or Username are Incorrect!");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, "Error executing SQL queries", e);
            errorLabel.setText("An error occurred while logging in.");
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
                    String displayName = resultSet.getString("display_name"); // Retrieve display name
                    String phoneNumber = resultSet.getString("phone_number"); // Retrieve phone number
                    return new UserInfo(retrievedUsername, email, password, displayName, phoneNumber);
                } else {
                    Logger.getLogger(DBUtils.class.getName()).log(Level.WARNING, "User not found in the database");
                    return null;
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, "Error executing SQL query", e);
            return null;
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

    public static void updateUserInfo(String username, String newUsername, String newEmail, String newPassword, String newDisplayName, String newPhoneNumber) throws SQLException {
        String query = "UPDATE users SET username = ?, email = ?, password = ?, display_name = ?, phone_number = ? WHERE username = ?";
        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, newUsername);
            preparedStatement.setString(2, newEmail);
            preparedStatement.setString(3, newPassword);
            preparedStatement.setString(4, newDisplayName);
            preparedStatement.setString(5, newPhoneNumber);
            preparedStatement.setString(6, username);
            preparedStatement.executeUpdate();
        }
    }


    public static void deleteUser(String username) throws SQLException {
        String query = "DELETE FROM users WHERE username = ?";

        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);

            preparedStatement.executeUpdate();

            Logger.getLogger(DBUtils.class.getName()).log(Level.INFO, "User deleted successfully");
        } catch (SQLException e) {
            Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, "Error deleting user", e);
            throw e;
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

    public static Map<String, Integer> getScreenTimeDataForUser(String username) throws SQLException {
        String query = "SELECT entry_date, screen_time_hours FROM mood_tracking WHERE user_id = (SELECT user_id FROM users WHERE username = ?) ORDER BY entry_date";
        Map<String, Integer> screenTimeData = new HashMap<>();

        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String date = resultSet.getString("entry_date"); // Read date directly as a string
                int screenTimeHours = resultSet.getInt("screen_time_hours");
                screenTimeData.put(date, screenTimeHours);
            }
        } catch (SQLException e) {
            Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, "Error fetching screen time data", e);
            throw e;
        }

        return screenTimeData;
    }

    public static Map<String, String> getMoodDataForUser(String username) throws SQLException {
        String query = "SELECT entry_date, mood FROM mood_tracking WHERE user_id = (SELECT user_id FROM users WHERE username = ?) ORDER BY entry_date";
        Map<String, String> moodData = new HashMap<>();

        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String date = resultSet.getString("entry_date"); // Read date directly as a string
                String moodRating = resultSet.getString("mood");

                moodData.put(date, moodRating);
            }
        } catch (SQLException e) {
            Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, "Error fetching mood data", e);
            throw e;
        }

        return moodData;
    }

    public static void insertMoodEntries(List<MoodEntry> entries, int userId) {
        String insertMoodEntrySQL = "INSERT INTO mood_tracking (user_id, entry_date, mood, screen_time_hours, activity_category, comments) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement preparedStatement = connection.prepareStatement(insertMoodEntrySQL)) {
            for (MoodEntry entry : entries) {
                StringBuilder activityStringBuilder = new StringBuilder();
                for (String activity : entry.getActivityCategory()) {
                    activityStringBuilder.append(activity).append(",");
                }
                String activityCategory = activityStringBuilder.toString().replaceAll(",$", ""); // Remove trailing comma

                preparedStatement.setInt(1, userId);
                String formattedDate = entry.getEntryDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
                preparedStatement.setString(2, formattedDate);
                preparedStatement.setString(3, entry.getMood());
                preparedStatement.setInt(4, entry.getScreenTimeHours());
                preparedStatement.setString(5, activityCategory); // Insert all activities concatenated into a single string
                preparedStatement.setString(6, entry.getComments());
                preparedStatement.executeUpdate();
            }
            Logger.getLogger(DBUtils.class.getName()).log(Level.INFO, "Mood entries inserted successfully");
        } catch (SQLException e) {
            Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, "Error inserting mood entries", e);
        }
    }

    public static int getUserId(String username) {
        String query = "SELECT user_id FROM users WHERE username = ?";
        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("user_id");
                } else {
                    Logger.getLogger(DBUtils.class.getName()).log(Level.WARNING, "User not found in the database");
                    return -1;
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, "Error executing SQL query", e);
            return -1;
        }
    }

    public static void updateXpInDatabase(int userId, int xpToAdd) {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement preparedStatement = connection.prepareStatement("UPDATE users SET xp = xp + ? WHERE user_id = ?")) {

            preparedStatement.setInt(1, xpToAdd);
            preparedStatement.setInt(2, userId);
            preparedStatement.executeUpdate();

            Logger.getLogger(DBUtils.class.getName()).log(Level.INFO, "Added " + xpToAdd + " XP to user with ID " + userId);
        } catch (SQLException e) {
            Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, "Error updating XP in database", e);
        }
    }

    public static int getXpForUser(int userId) {
        int xp = 0;
        String getXpSQL = "SELECT xp FROM users WHERE user_id = ?";
        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement preparedStatement = connection.prepareStatement(getXpSQL)) {
            preparedStatement.setInt(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    xp = resultSet.getInt("xp");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving XP from the database: " + e.getMessage());
        }
        return xp;
    }

    public static void updateLevelInDatabase(int userId) {
        int xp = getXpForUser(userId);
        int level = xp / 100;

        String updateLevelSQL = "UPDATE users SET level = ? WHERE user_id = ?";
        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement preparedStatement = connection.prepareStatement(updateLevelSQL)) {
            preparedStatement.setInt(1, level);
            preparedStatement.setInt(2, userId);
            preparedStatement.executeUpdate();
            System.out.println("Level updated successfully!");
        } catch (SQLException e) {
            System.err.println("Error updating level in the database: " + e.getMessage());
        }
    }

    public static int getUserLevel(int userId) throws SQLException {
        int level = 0;
        String getLevelSQL = "SELECT level FROM users WHERE user_id = ?";
        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement preparedStatement = connection.prepareStatement(getLevelSQL)) {
            preparedStatement.setInt(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    level = resultSet.getInt("level");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user's level from the database: " + e.getMessage());
            throw e;
        }
        return level;
    }

    public static boolean entryExistsForToday(int userId) throws SQLException {
        String query = "SELECT COUNT(*) FROM mood_tracking WHERE user_id = ? AND DATE(entry_date) = ?";
        LocalDate today = LocalDate.now();
        String formattedDate = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, formattedDate);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        }

        return false;
    }


}