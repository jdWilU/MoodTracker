package org.example.moodtracker.model;

public class UserInfo {
    private String username;
    private String email;
    private String password;
    private String displayName;
    private String phoneNumber;

    public UserInfo(String username, String email, String password, String displayName, String phoneNumber) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.displayName = displayName;
        this.phoneNumber = phoneNumber;
    }

    // Getters and setters for all fields
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
