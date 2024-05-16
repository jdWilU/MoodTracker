package org.example.moodtracker.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.moodtracker.model.DBUtils;
import org.example.moodtracker.model.UIUtils;

import java.net.URL;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class HomepageController implements Initializable {

    @FXML
    private Button button_logout;
    @FXML
    private Button button_close;
    @FXML
    private Button button_table;
    @FXML
    private Button button_daily_entry;
    @FXML
    private Button button_profile;
    @FXML
    private Label label_welcome;
    @FXML
    private Label current_date;
    @FXML
    private PieChart mood_Pie;
    @FXML
    private BarChart<String, Number> screenTime_BarChart;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        button_logout.setOnAction(event -> DBUtils.changeScene(event, "login.fxml", "Log In", null));
        button_close.setOnAction(actionEvent -> UIUtils.closeApp((Stage) button_close.getScene().getWindow()));
        button_table.setOnAction(event -> DBUtils.changeScene(event, "tableView.fxml", "Table View", null));
        button_profile.setOnAction(event -> DBUtils.changeScene(event, "profile.fxml", "Profile", null));
        button_daily_entry.setOnAction(event -> DBUtils.changeScene(event, "mood-tracking-page.fxml", "Mood Tracking", null));
        button_profile.setOnAction(event -> DBUtils.changeScene(event, "profile.fxml", "Profile", null));

        // Load external CSS file for styling PieChart
        String cssPath = "/Styling/Styling.css"; // Path relative to the resources directory
        mood_Pie.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
        System.out.println("Loaded Stylesheets: " + mood_Pie.getStylesheets());

        // Set user information and current date
        String currentUser = DBUtils.getCurrentUsername();
        if (currentUser != null) {
            UIUtils.setUserInformation(label_welcome, currentUser);
            UIUtils.setCurrentDate(current_date);

            try {
                initializeMoodPieChart(currentUser);
                initializeScreenTimeBarChart(currentUser);

            } catch (SQLException e) {
                Logger.getLogger(HomepageController.class.getName()).log(Level.SEVERE, "Error fetching data", e);
            }
        }
    }

    private void initializeMoodPieChart(String currentUser) throws SQLException {
        // Get mood data for the current user
        Map<String, Integer> moodCounts = DBUtils.getMoodCountsForUser(currentUser);

        // Clear existing PieChart data
        mood_Pie.getData().clear();

        // Populate PieChart with mood data
        int colorIndex = 0;
        for (Map.Entry<String, Integer> entry : moodCounts.entrySet()) {
            String mood = entry.getKey();
            int count = entry.getValue();

            // Create PieChart.Data item
            PieChart.Data data = new PieChart.Data(mood, count);

            // Add data to PieChart (this initializes the Node)
            mood_Pie.getData().add(data);

            // Apply style class to the Node of the PieChart.Data
            if (data.getNode() != null) {
                data.getNode().getStyleClass().add("chart-pie-color" + colorIndex);
            }

            colorIndex++; // Increment color index for next mood
        }
    }

    private void initializeScreenTimeBarChart(String currentUser) throws SQLException {
        // Get screen time data for the current user
        Map<String, Integer> screenTimeData = DBUtils.getScreenTimeDataForUser(currentUser);

        // Filter data to only include the last seven days
        Map<String, Integer> filteredData = filterDataByWeek(screenTimeData);

        // Clear existing BarChart data
        screenTime_BarChart.getData().clear();

        // Populate BarChart with screen time data
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Screen Time");

        // Sort the filtered data by date
        Map<String, Integer> sortedData = filteredData.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        // Add sorted screen time data to the series
        for (Map.Entry<String, Integer> entry : sortedData.entrySet()) {
            String date = entry.getKey();
            int screenTimeHours = entry.getValue();

            // Add the date and screen time hours to the series
            series.getData().add(new XYChart.Data<>(date, screenTimeHours));
        }

        // Add the series to the BarChart
        screenTime_BarChart.getData().add(series);

        // Remove the legend
        screenTime_BarChart.setLegendVisible(false);

        // Adjust the bar width to fit all entries on the graph
        int numEntries = sortedData.size();
        if (numEntries > 10) {
            // Reduce the bar width for better visibility
            screenTime_BarChart.setBarGap(0);
            screenTime_BarChart.setCategoryGap(1);
        }
    }

    private Map<String, Integer> filterDataByWeek(Map<String, Integer> data) {
        // Get the current date
        LocalDate currentDate = LocalDate.now();

        // Calculate the start date of the week (6 days ago since we want to include today)
        LocalDate startDate = currentDate.minusDays(6);

        // Create a new map to store filtered data
        Map<String, Integer> filteredData = new LinkedHashMap<>();

        // Iterate through the original data and add entries within the last seven days to the filtered data
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            String date = entry.getKey(); // Assuming date is in a format compatible with LocalDate
            LocalDate entryDate = LocalDate.parse(date); // Parse the date string to LocalDate

            // Check if the entry date is within the last seven days (inclusive)
            if (!entryDate.isBefore(startDate) && !entryDate.isAfter(currentDate)) {
                filteredData.put(date, entry.getValue());
            }
        }

        return filteredData;
    }






}
