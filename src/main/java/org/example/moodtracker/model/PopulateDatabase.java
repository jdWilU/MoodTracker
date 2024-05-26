package org.example.moodtracker.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.example.moodtracker.model.DBUtils.updateLevelInDatabase;
import static org.example.moodtracker.model.DBUtils.updateXpInDatabase;


// Utility Class to populate database with information to be manipulated. Only needs to be run once.

public class PopulateDatabase {

    private static final String DATABASE_URL = "jdbc:sqlite:moodtracker.db";

    public static void main(String[] args) {
        populateMoodTrackingTable();
    }

    public static void populateMoodTrackingTable() {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             Statement statement = connection.createStatement()) {

            // Delete existing entries in the mood tracking table
            String deleteEntriesSQL = "DELETE FROM mood_tracking";
            statement.executeUpdate(deleteEntriesSQL);

            // Get user IDs from users table
            int userId1 = getUserIdByUsername(connection, "john_doe");

            String insertMood1SQL = "INSERT INTO mood_tracking (user_id, entry_date, mood, screen_time_hours, activity_category, comments) " +
                    "VALUES (" + userId1 + ", '2024-05-08', 'OKAY', 1, 'Exercise', 'Spent time with pet')";
            statement.executeUpdate(insertMood1SQL);

            String insertMood2SQL = "INSERT INTO mood_tracking (user_id, entry_date, mood, screen_time_hours, activity_category, comments) " +
                    "VALUES (" + userId1 + ", '2024-05-09', 'BAD', 2, 'Journaling', '')";
            statement.executeUpdate(insertMood2SQL);

            String insertMood3SQL = "INSERT INTO mood_tracking (user_id, entry_date, mood, screen_time_hours, activity_category, comments) " +
                    "VALUES (" + userId1 + ", '2024-05-10', 'POOR', 4, 'Meditation', 'Feeling very calm today.')";
            statement.executeUpdate(insertMood3SQL);

            String insertMood4SQL = "INSERT INTO mood_tracking (user_id, entry_date, mood, screen_time_hours, activity_category, comments) " +
                    "VALUES (" + userId1 + ", '2024-05-11', 'OKAY', 3, 'Hobbies', 'Was great weather today')";
            statement.executeUpdate(insertMood4SQL);

            String insertMood5SQL = "INSERT INTO mood_tracking (user_id, entry_date, mood, screen_time_hours, activity_category, comments) " +
                    "VALUES (" + userId1 + ", '2024-05-12', 'GREAT', 6, 'Gaming', 'Man I love dogs')";
            statement.executeUpdate(insertMood5SQL);

            String insertMood6SQL = "INSERT INTO mood_tracking (user_id, entry_date, mood, screen_time_hours, activity_category, comments) " +
                    "VALUES (" + userId1 + ", '2024-05-13', 'BAD', 3, 'School', '')";
            statement.executeUpdate(insertMood6SQL);

            String insertMood7SQL = "INSERT INTO mood_tracking (user_id, entry_date, mood, screen_time_hours, activity_category, comments) " +
                    "VALUES (" + userId1 + ", '2024-05-14', 'GOOD', 5, 'Reading', 'Finished a great book')";
            statement.executeUpdate(insertMood7SQL);

            String insertMood8SQL = "INSERT INTO mood_tracking (user_id, entry_date, mood, screen_time_hours, activity_category, comments) " +
                    "VALUES (" + userId1 + ", '2024-05-15', 'POOR', 2, 'Netflix', 'Watched a boring movie')";
            statement.executeUpdate(insertMood8SQL);

            String insertMood9SQL = "INSERT INTO mood_tracking (user_id, entry_date, mood, screen_time_hours, activity_category, comments) " +
                    "VALUES (" + userId1 + ", '2024-05-16', 'OKAY', 4, 'Cooking', 'Made pasta for dinner')";
            statement.executeUpdate(insertMood9SQL);

            String insertMood10SQL = "INSERT INTO mood_tracking (user_id, entry_date, mood, screen_time_hours, activity_category, comments) " +
                    "VALUES (" + userId1 + ", '2024-05-17', 'GREAT', 8, 'Gardening', 'Planted new flowers')";
            statement.executeUpdate(insertMood10SQL);

            String insertMood11SQL = "INSERT INTO mood_tracking (user_id, entry_date, mood, screen_time_hours, activity_category, comments) " +
                    "VALUES (" + userId1 + ", '2024-05-18', 'BAD', 3, 'Walking', 'Took a long walk in the park')";
            statement.executeUpdate(insertMood11SQL);

            String insertMood12SQL = "INSERT INTO mood_tracking (user_id, entry_date, mood, screen_time_hours, activity_category, comments) " +
                    "VALUES (" + userId1 + ", '2024-05-19', 'GOOD', 5, 'Painting', 'Started a new art project')";
            statement.executeUpdate(insertMood12SQL);

            String insertMood13SQL = "INSERT INTO mood_tracking (user_id, entry_date, mood, screen_time_hours, activity_category, comments) " +
                    "VALUES (" + userId1 + ", '2024-05-20', 'OKAY', 3, 'Cooking', 'Tried a new recipe')";
            statement.executeUpdate(insertMood13SQL);

            String insertMood14SQL = "INSERT INTO mood_tracking (user_id, entry_date, mood, screen_time_hours, activity_category, comments) " +
                    "VALUES (" + userId1 + ", '2024-05-21', 'GREAT', 1, 'Exercise', 'Went to the gym')";
            statement.executeUpdate(insertMood14SQL);

            String insertMood15SQL = "INSERT INTO mood_tracking (user_id, entry_date, mood, screen_time_hours, activity_category, comments) " +
                    "VALUES (" + userId1 + ", '2024-05-22', 'POOR', 2, 'Nothing', 'Stayed in bed all day')";
            statement.executeUpdate(insertMood15SQL);

            String insertMood16SQL = "INSERT INTO mood_tracking (user_id, entry_date, mood, screen_time_hours, activity_category, comments) " +
                    "VALUES (" + userId1 + ", '2024-05-23', 'GREAT', 10, 'Surfing', 'Went to the beach')";
            statement.executeUpdate(insertMood16SQL);

            String insertMood17SQL = "INSERT INTO mood_tracking (user_id, entry_date, mood, screen_time_hours, activity_category, comments) " +
                    "VALUES (" + userId1 + ", '2024-05-24', 'BAD', 6, 'Gaming', 'Played club penguin')";
            statement.executeUpdate(insertMood17SQL);


            System.out.println("Additional mood tracking data inserted successfully!");

            String deleteXPAndLevelSQL = "UPDATE users SET xp = 0, level = 0 WHERE user_id = " + userId1;
            statement.executeUpdate(deleteXPAndLevelSQL);

            updateLevelInDatabase(userId1);
            updateXpInDatabase(userId1, 340);



        } catch (SQLException e) {
            System.err.println("Error populating mood tracking table: " + e.getMessage());
        }
    }

    public static int getUserIdByUsername(Connection connection, String username) throws SQLException {
        String query = "SELECT user_id FROM users WHERE username = '" + username + "'";
        try (Statement statement = connection.createStatement()) {
            var resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                return resultSet.getInt("user_id");
            }
        }
        return -1; // Return -1 if user not found (shouldn't happen in this example)
    }
}
