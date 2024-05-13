package org.example.moodtracker.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MoodEntry {
    private String entryDate;
    private String mood;
    private String activityCategory;
    private int screenTimeHours;
    private String comments;

    public MoodEntry(String entryDate, String mood, String activityCategory, int screenTimeHours, String comments) {
        this.entryDate = entryDate;
        this.mood = mood;
        this.activityCategory = activityCategory;
        this.screenTimeHours = screenTimeHours;
        this.comments = comments;
    }

    public String getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(String entryDate) {
        this.entryDate = entryDate;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public String getActivityCategory() {
        return activityCategory;
    }

    public void setActivityCategory(String activityCategory) {
        this.activityCategory = activityCategory;
    }

    public int getScreenTimeHours() {
        return screenTimeHours;
    }

    public void setScreenTimeHours(int screenTimeHours) {
        this.screenTimeHours = screenTimeHours;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    private List<MoodEntry> moodEntries = new ArrayList<>();

    public void addMoodEntry(MoodEntry entry) {
        moodEntries.add(entry);
    }

    public List<MoodEntry> getMoodEntries() {
        return moodEntries;
    }

    public void clearEntries() {
        moodEntries.clear();
    }
}
