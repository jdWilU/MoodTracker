package org.example.moodtracker.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a mood entry with details such as mood, activities, screen time, and comments.
 * This class uses a singleton pattern to ensure only one instance exists.
 */
public class MoodEntry {
    private static MoodEntry moodEntry;

    private LocalDate entryDate;
    private String mood;
    private final List<String> activityCategory;
    private int screenTimeHours;
    private String comments;

    /**
     * Default constructor initializing the activity list.
     */
    public MoodEntry() {
        activityCategory = new ArrayList<>();
    }

    /**
     * Retrieves the singleton instance of MoodEntry.
     *
     * @return The singleton instance of MoodEntry.
     */
    public static MoodEntry getInstance() {
        if (moodEntry == null) {
            moodEntry = new MoodEntry();
        }
        return moodEntry;
    }

    /**
     * Sets the selected mood.
     *
     * @param selectedMood The selected mood.
     */
    public void setSelectedMood(String selectedMood) {
        this.mood = selectedMood;
    }

    /**
     * Sets the selected activities.
     *
     * @param selectedActivities The list of selected activities.
     */
    public void setSelectedActivities(List<String> selectedActivities) {
        if (selectedActivities != null) {
            activityCategory.addAll(selectedActivities);
        }
    }

    /**
     * Sets the screen time in hours.
     *
     * @param screenTime The screen time in hours.
     */
    public void setScreenTime(int screenTime) {
        this.screenTimeHours = screenTime;
    }

    /**
     * Sets the journal entry comments.
     *
     * @param comments The journal entry comments.
     */
    public void setJournalEntry(String comments) {
        this.comments = comments;
    }

    /**
     * Retrieves the current mood entry instance.
     *
     * @return The current mood entry instance.
     */
    public static MoodEntry getMoodEntry() {
        return moodEntry;
    }

    /**
     * Sets the current mood entry instance.
     *
     * @param entry The mood entry instance to set.
     */
    public static void setMoodEntry(MoodEntry entry) {
        moodEntry = entry;
    }

    /**
     * Retrieves the selected mood.
     *
     * @return The selected mood.
     */
    public String getMood() {
        return mood;
    }

    /**
     * Retrieves the list of selected activities.
     *
     * @return The list of selected activities.
     */
    public List<String> getActivityCategory() {
        return activityCategory;
    }

    /**
     * Retrieves the screen time in hours.
     *
     * @return The screen time in hours.
     */
    public int getScreenTimeHours() {
        return screenTimeHours;
    }

    /**
     * Retrieves the journal entry comments.
     *
     * @return The journal entry comments.
     */
    public String getComments() {
        return comments;
    }

    /**
     * Retrieves the entry date.
     *
     * @return The entry date.
     */
    public LocalDate getEntryDate() {
        return entryDate;
    }

    /**
     * Sets the entry date.
     *
     * @param entryDate The entry date to set.
     */
    public void setEntryDate(LocalDate entryDate) {
        this.entryDate = entryDate;
    }
}
