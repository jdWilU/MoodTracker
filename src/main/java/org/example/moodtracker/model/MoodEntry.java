package org.example.moodtracker.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MoodEntry {
    private static MoodEntry moodEntry;

    private LocalDate entryDate;
    private String mood;
    private List<String> activityCategory;
    private int screenTimeHours;
    private String comments;

    public MoodEntry() {
        // Initialize the activity list
        activityCategory = new ArrayList<>();
    }

    public static MoodEntry getInstance() {
        if (moodEntry == null) {
            moodEntry = new MoodEntry();
        }
        return moodEntry;
    }

    public void setSelectedMood(String selectedMood) {
        this.mood = selectedMood;
    }

    public void setSelectedActivities(List<String> selectedActivities) {
        if (selectedActivities != null) {
            for (String activity : selectedActivities) {
                activityCategory.add(activity);
            }
        }
    }


    public void setScreenTime(int screenTime) {
        this.screenTimeHours = screenTime;
    }

    public void setJournalEntry(String comments) {
        this.comments = comments;
    }

    public static MoodEntry getMoodEntry() {
        return moodEntry;
    }

    public static void setMoodEntry(MoodEntry entry) {
        moodEntry = entry;
    }

    public String getMood() {
        return mood;
    }

    public List<String> getActivityCategory() {
        return activityCategory;
    }

    public int getScreenTimeHours() {
        return screenTimeHours;
    }

    public String getComments() {
        return comments;
    }

    public LocalDate getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(LocalDate entryDate) {
        this.entryDate = entryDate;
    }

}
