package org.example.moodtracker.model;

public class UserSession {
    private static String username;
    private static int user_id; // Static variable to store user_id

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        UserSession.username = username;
    }

    public static int getUserId() {
        return user_id;
    }

    public static void setUserId(int user_id) {
        UserSession.user_id = user_id;
    }
}