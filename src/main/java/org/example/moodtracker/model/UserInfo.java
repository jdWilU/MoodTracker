package org.example.moodtracker.model;

/**
 * Class representing user information.
 */
public class UserInfo {
    private String username;
    private String email;
    private String password;
    private String displayName;
    private String phoneNumber;

    /**
     * Constructs a UserInfo object with the specified attributes.
     *
     * @param username     The username of the user.
     * @param email        The email address of the user.
     * @param password     The password of the user.
     * @param displayName  The display name of the user.
     * @param phoneNumber  The phone number of the user.
     */
    public UserInfo(String username, String email, String password, String displayName, String phoneNumber) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.displayName = displayName;
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets the username of the user.
     *
     * @return The username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user.
     *
     * @param username The username to set.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the email address of the user.
     *
     * @return The email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the user.
     *
     * @param email The email address to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the password of the user.
     *
     * @return The password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of the user.
     *
     * @param password The password to set.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the display name of the user.
     *
     * @return The display name.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets the display name of the user.
     *
     * @param displayName The display name to set.
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the phone number of the user.
     *
     * @return The phone number.
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the phone number of the user.
     *
     * @param phoneNumber The phone number to set.
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
