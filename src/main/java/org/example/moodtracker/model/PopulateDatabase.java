package org.example.moodtracker.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


// Utility Class to populate database with information to be manipulated. Only needs to be run once.

public class PopulateDatabase {

    private static final String DATABASE_URL = "jdbc:sqlite:moodtracker.db";

    public static void main(String[] args) {
        populateMoodTrackingTable();
    }

    public static void populateMoodTrackingTable() {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             Statement statement = connection.createStatement()) {

            // Get user IDs from users table
            int userId1 = getUserIdByUsername(connection, "john_doe");

            // Insert dummy mood tracking data for user 1 (john_doe)
            String insertMood1SQL = "INSERT INTO mood_tracking (user_id, entry_date, mood, screen_time_hours, activity_category, comments) " +
                                    "VALUES (" + userId1 + ", '2024-05-02', 'OKAY', 1, 'Exercise', 'Spent time with pet')";
            statement.executeUpdate(insertMood1SQL);

            String insertMood2SQL = "INSERT INTO mood_tracking (user_id, entry_date, mood, screen_time_hours, activity_category, comments) " +
                                    "VALUES (" + userId1 + ", '2024-05-03', 'BAD', 2, 'Journaling', '')";
            statement.executeUpdate(insertMood2SQL);

            String insertMood3SQL = "INSERT INTO mood_tracking (user_id, entry_date, mood, screen_time_hours, activity_category, comments) " +
                                    "VALUES (" + userId1 + ", '2024-05-04', 'POOR', 4, 'Meditation', 'Feeling very calm today.')";
            statement.executeUpdate(insertMood3SQL);

            String insertMood4SQL = "INSERT INTO mood_tracking (user_id, entry_date, mood, screen_time_hours, activity_category, comments) " +
                                    "VALUES (" + userId1 + ", '2024-05-05', 'OKAY', 3, 'Hobbies', 'Was great weather today')";
            statement.executeUpdate(insertMood4SQL);

            // Insert dummy mood tracking data for user 2 (jane_smith)
            String insertMood5SQL = "INSERT INTO mood_tracking (user_id, entry_date, mood, screen_time_hours, activity_category, comments) " +
                                    "VALUES (" + userId1 + ", '2024-05-06', 'GOOD', 1, 'Hobbies', 'Spent time coding.')";
            statement.executeUpdate(insertMood5SQL);

            String insertMood6SQL = "INSERT INTO mood_tracking (user_id, entry_date, mood, screen_time_hours, activity_category, comments) " +
                                    "VALUES (" + userId1 + ", '2024-05-07', 'OKAY', 2, 'Exercise', 'Wrote in gratitude journal before bed.')";
            statement.executeUpdate(insertMood6SQL);

            System.out.println("Additional mood tracking data inserted successfully!");

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
