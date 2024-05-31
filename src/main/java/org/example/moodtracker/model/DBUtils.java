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
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBUtils {
    private static final String DATABASE_URL = "jdbc:sqlite:moodtracker.db";
    private static String currentUsername;
    private static String currentEmail;

    /**
     * Function to create necessary tables in the SQLite database.
     */
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

    /**
     * Changes the scene in the JavaFX application.
     *
     * @param event    The action event triggering the scene change.
     * @param fxmlFile The FXML file to load.
     * @param title    The title for the new scene.
     */
    public static void changeScene(ActionEvent event, String fxmlFile, String title) {
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

/**
     * Signs up a new user by inserting their details into the database.
     *
     * @param event      The action event triggering the sign-up process.
     * @param username   The username of the new user.
     * @param email      The email of the new user.
     * @param password   The password of the new user.
     * @param errorLabel The label to display error messages.
     */
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
                    changeScene(event, "homepage.fxml", "Welcome");
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, "Error executing SQL queries", e);
            errorLabel.setText("An error occurred while signing up.");
        }
    }

    /**
     * Logs in an existing user by checking their credentials.
     *
     * @param event      The action event triggering the login process.
     * @param username   The username of the user.
     * @param password   The password of the user.
     * @param errorLabel The label to display error messages.
     */
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
                            changeScene(event, "homepage.fxml", "Welcome");
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

    /**
     * Retrieves user information based on the username.
     *
     * @param username The username to look up.
     * @return The user's information or null if not found.
     */
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
    /**
     * Gets the current username.
     *
     * @return The current username.
     */
    public static String getCurrentUsername() {
        return currentUsername;
    }

    /**
     * Sets the current username.
     *
     * @param username The username to set.
     */
    public static void setCurrentUsername(String username) {
        currentUsername = username;
    }

    /**
     * Gets the current email.
     *
     * @return The current email.
     */
    public static String getCurrentEmail() {
        return currentEmail;
    }

    /**
     * Sets the current email.
     *
     * @param email The email to set.
     */
    public static void setCurrentEmail(String email) {
        currentEmail = email;
    }

    /**
     * Sets the current password.
     *
     * @param password The password to set.
     */
    public static void setCurrentPassword(String password) {
        // Static variable to store the current username
    }

    /**
     * Updates user information in the database.
     *
     * @param username        The current username.
     * @param newUsername     The new username.
     * @param newEmail        The new email.
     * @param newPassword     The new password.
     * @param newDisplayName  The new display name.
     * @param newPhoneNumber  The new phone number.
     * @return True if the update was successful, false otherwise.
     */
    public static boolean updateUserInfo(String username, String newUsername, String newEmail, String newPassword, String newDisplayName, String newPhoneNumber) {
        String query = "UPDATE users SET username = ?, email = ?, password = ?, display_name = ?, phone_number = ? WHERE username = ?";
        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, newUsername);
            preparedStatement.setString(2, newEmail);
            preparedStatement.setString(3, newPassword);
            preparedStatement.setString(4, newDisplayName);
            preparedStatement.setString(5, newPhoneNumber);
            preparedStatement.setString(6, username);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, "Error updating user information", e);
            return false;
        }
    }

    /**
     * Deletes a user from the database based on their username.
     *
     * @param username The username of the user to delete.
     * @throws SQLException If an SQL error occurs.
     */
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


    /**
     * Retrieves the count of each mood type for a specific user.
     *
     * @param username The username of the user.
     * @return A map of mood types to their respective counts.
     * @throws SQLException If an SQL error occurs.
     */
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

    /**
     * Retrieves screen time data for a specific user.
     *
     * @param username The username of the user.
     * @return A map of entry dates to screen time hours.
     * @throws SQLException If an SQL error occurs.
     */
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

    /**
     * Retrieves mood data for a specific user.
     *
     * @param username The username of the user.
     * @return A map of entry dates to mood ratings.
     * @throws SQLException If an SQL error occurs.
     */
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

    /**
     * Inserts a list of mood entries for a specific user.
     *
     * @param entries The list of mood entries to insert.
     * @param userId  The ID of the user.
     */
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

    /**
     * Retrieves the ID of a user based on their username.
     *
     * @param username The username to look up.
     * @return The user ID, or -1 if not found.
     */
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

    /**
     * Updates the XP of a user in the database.
     *
     * @param userId   The ID of the user.
     * @param xpToAdd  The amount of XP to add.
     */
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

    /**
     * Retrieves the XP of a user based on their user ID.
     *
     * @param userId The ID of the user.
     * @return The XP of the user.
     */
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

    /**
     * Updates the level of a user based on their XP.
     *
     * @param userId The ID of the user.
     */
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

    /**
     * Retrieves the level of a user based on their user ID.
     *
     * @param userId The ID of the user.
     * @return The level of the user.
     * @throws SQLException If an SQL error occurs.
     */
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

    /**
     * Checks if a mood entry exists for today for a specific user.
     *
     * @param userId The ID of the user.
     * @return True if an entry exists for today, false otherwise.
     * @throws SQLException If an SQL error occurs.
     */
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


    /**
     * Retrieves the longest streak of consecutive days with mood entries for a user.
     *
     * @param userId The ID of the user.
     * @return The longest streak of consecutive days with mood entries.
     * @throws SQLException If an SQL error occurs.
     */
    public static int getUserEntryHistory(int userId) throws SQLException {
        int longestStreak = 0;
        List<LocalDate> entryDates = new ArrayList<>();

        String getEntryDatesSQL = "SELECT entry_date FROM mood_tracking WHERE user_id = ? ORDER BY entry_date";

        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement preparedStatement = connection.prepareStatement(getEntryDatesSQL)) {

            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                while (resultSet.next()) {
                    // Parse the date correctly
                    String dateString = resultSet.getString("entry_date").split(" ")[0];
                    LocalDate entryDate = LocalDate.parse(dateString, formatter);
                    entryDates.add(entryDate);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user's entry history from the database: " + e.getMessage());
            throw e; // Re-throw the SQLException to propagate it to the caller
        }

        // Calculate the longest streak
        if (!entryDates.isEmpty()) {
            Collections.sort(entryDates);
            int streak = 1;

            for (int i = 1; i < entryDates.size(); i++) {
                LocalDate currentDate = entryDates.get(i);
                LocalDate previousDate = entryDates.get(i - 1);

                if (currentDate.equals(previousDate.plusDays(1))) {
                    streak++;
                } else {
                    longestStreak = Math.max(longestStreak, streak);
                    streak = 1;
                }
            }
            longestStreak = Math.max(longestStreak, streak); // In case the longest streak ends at the last date
        }

        return longestStreak;
    }

    /**
     * Retrieves the total number of mood entries for a user.
     *
     * @param userId The ID of the user.
     * @return The total number of mood entries.
     * @throws SQLException If an SQL error occurs.
     */
    public static int getTotalEntries(int userId) throws SQLException {
        int totalEntries = 0;
        String getTotalEntriesSQL = "SELECT COUNT(*) AS total_entries FROM mood_tracking WHERE user_id = ?";

        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement preparedStatement = connection.prepareStatement(getTotalEntriesSQL)) {
            preparedStatement.setInt(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    totalEntries = resultSet.getInt("total_entries");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving total entries from the database: " + e.getMessage());
            throw e;
        }

        return totalEntries;
    }

    /**
     * Updates the 'visited_education' column for a user to true.
     *
     * @param userId The ID of the user.
     * @throws SQLException If an SQL error occurs.
     */
    public static void updateVisitedEducation(int userId) throws SQLException {
        String updateSQL = "UPDATE users SET visited_education = TRUE WHERE user_id = ?";

        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {
            preparedStatement.setInt(1, userId);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Updated 'visited_education' column for user_id: " + userId);

            } else {
                System.err.println("No rows affected for user_id: " + userId);
            }
        } catch (SQLException e) {
            System.err.println("Error updating 'visited_education' column: " + e.getMessage());
            throw e; // Re-throw the SQLException to propagate it to the caller
        }
    }

    /**
     * Checks if a user has visited the education section.
     *
     * @param userId The ID of the user.
     * @return True if the user has visited the education section, false otherwise.
     * @throws SQLException If an SQL error occurs.
     */
    public static boolean hasVisitedEducation(int userId) throws SQLException {
        String querySQL = "SELECT visited_education FROM users WHERE user_id = ?";
        boolean visitedEducation = false;

        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement preparedStatement = connection.prepareStatement(querySQL)) {
            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    visitedEducation = resultSet.getBoolean("visited_education");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking 'visited_education' column: " + e.getMessage());
            throw e; // Re-throw the SQLException to propagate it to the caller
        }

        return visitedEducation;
    }
}
