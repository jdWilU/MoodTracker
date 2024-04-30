package org.example.moodtracker.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class PopulateDatabase {

    private static final String DATABASE_URL = "jdbc:sqlite:moodtracker.db";

    public static void main(String[] args) {
        populateUsersTable();
        populateMoodTrackingTable();
    }

    public static void populateUsersTable() {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             Statement statement = connection.createStatement()) {

            // Insert dummy users
            String insertUser1SQL = "INSERT INTO users (username, email, password) VALUES ('john_doe', 'john@example.com', 'password123')";
            statement.executeUpdate(insertUser1SQL);

            String insertUser2SQL = "INSERT INTO users (username, email, password) VALUES ('jane_smith', 'jane@example.com', 'abc123')";
            statement.executeUpdate(insertUser2SQL);

            System.out.println("Users inserted successfully!");

        } catch (SQLException e) {
            System.err.println("Error populating users table: " + e.getMessage());
        }
    }

    public static void populateMoodTrackingTable() {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             Statement statement = connection.createStatement()) {

            // Get user IDs from users table
            int userId1 = getUserIdByUsername(connection, "john_doe");
            int userId2 = getUserIdByUsername(connection, "jane_smith");

            // Insert dummy mood tracking data for user 1 (john_doe)
            String insertMood1SQL = "INSERT INTO mood_tracking (user_id, entry_date, mood, screen_time_hours, activity_category, comments) " +
                                    "VALUES (" + userId1 + ", '2024-04-29', 'GOOD', 1, 'Exercise', 'Enjoyed a morning jog.')";
            statement.executeUpdate(insertMood1SQL);

            String insertMood2SQL = "INSERT INTO mood_tracking (user_id, entry_date, mood, screen_time_hours, activity_category, comments) " +
                                    "VALUES (" + userId1 + ", '2024-04-30', 'OKAY', 2, 'Socializing', 'Had lunch with friends.')";
            statement.executeUpdate(insertMood2SQL);

            String insertMood3SQL = "INSERT INTO mood_tracking (user_id, entry_date, mood, screen_time_hours, activity_category, comments) " +
                                    "VALUES (" + userId1 + ", '2024-05-01', 'GREAT', 1, 'Meditation', 'Feeling very calm today.')";
            statement.executeUpdate(insertMood3SQL);

            String insertMood4SQL = "INSERT INTO mood_tracking (user_id, entry_date, mood, screen_time_hours, activity_category, comments) " +
                                    "VALUES (" + userId1 + ", '2024-05-02', 'OKAY', 3, 'Hobbies', 'Worked on painting in the evening.')";
            statement.executeUpdate(insertMood4SQL);

            // Insert dummy mood tracking data for user 2 (jane_smith)
            String insertMood5SQL = "INSERT INTO mood_tracking (user_id, entry_date, mood, screen_time_hours, activity_category, comments) " +
                                    "VALUES (" + userId2 + ", '2024-04-29', 'GOOD', 1, 'Exercise', 'Morning yoga session.')";
            statement.executeUpdate(insertMood5SQL);

            String insertMood6SQL = "INSERT INTO mood_tracking (user_id, entry_date, mood, screen_time_hours, activity_category, comments) " +
                                    "VALUES (" + userId2 + ", '2024-04-30', 'OKAY', 2, 'Journaling', 'Wrote in gratitude journal before bed.')";
            statement.executeUpdate(insertMood6SQL);

            System.out.println("Additional mood tracking data inserted successfully!");

        } catch (SQLException e) {
            System.err.println("Error populating mood tracking table: " + e.getMessage());
        }
    }

    public static int getUserIdByUsername(Connection connection, String username) throws SQLException {
        String query = "SELECT id FROM users WHERE username = '" + username + "'";
        try (Statement statement = connection.createStatement()) {
            var resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
        }
        return -1; // Return -1 if user not found (shouldn't happen in this example)
    }
}
