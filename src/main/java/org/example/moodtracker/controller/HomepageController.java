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
import java.time.DayOfWeek;
import java.time.LocalDate;
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
    private Button button_previous_week;
    @FXML
    private Button button_next_week;
    @FXML
    private Label label_welcome;
    @FXML
    private Label current_date;
    @FXML
    private PieChart mood_Pie;
    @FXML
    private BarChart<String, Number> screenTime_BarChart;
    @FXML
    private Label dateRangeLabel;  // Label for displaying date range

    // NumberAxis for Y-axis
    @FXML
    private NumberAxis screenTimeYAxis;

    private LocalDate currentStartDate = LocalDate.now().minusDays(6); // Start date of the current week

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        button_logout.setOnAction(event -> DBUtils.changeScene(event, "login.fxml", "Log In", null));
        button_close.setOnAction(actionEvent -> UIUtils.closeApp((Stage) button_close.getScene().getWindow()));
        button_table.setOnAction(event -> DBUtils.changeScene(event, "tableView.fxml", "Table View", null));
        button_profile.setOnAction(event -> DBUtils.changeScene(event, "profile.fxml", "Profile", null));
        button_daily_entry.setOnAction(event -> DBUtils.changeScene(event, "mood-tracking-page.fxml", "Mood Tracking", null));

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

        // Button functionality for navigating weeks
        button_previous_week.setOnAction(event -> {
            currentStartDate = currentStartDate.minusWeeks(1);
            button_next_week.setVisible(true); // Show the "Next Week" button
            try {
                initializeScreenTimeBarChart(currentUser);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        button_next_week.setOnAction(event -> {
            currentStartDate = currentStartDate.plusWeeks(1);
            if (currentStartDate.equals(LocalDate.now().minusDays(6))) {
                button_next_week.setVisible(false); // Hide the "Next Week" button if the current week is reached
            }
            try {
                initializeScreenTimeBarChart(currentUser);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        // Initially hide the "Next Week" button
        button_next_week.setVisible(false);

        // Load external CSS file for styling PieChart
        String cssPath = "/Styling/Styling.css"; // Path relative to the resources directory
        mood_Pie.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());

        // Set the Y-axis bounds
        screenTimeYAxis.setAutoRanging(false);
        screenTimeYAxis.setLowerBound(0);
        screenTimeYAxis.setUpperBound(8);
        screenTimeYAxis.setTickUnit(1);
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

        // Filter data to include only the current week starting from currentStartDate
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

        // Add sorted screen time data to the series with days of the week as labels
        for (Map.Entry<String, Integer> entry : sortedData.entrySet()) {
            LocalDate date = LocalDate.parse(entry.getKey());
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            String dayLabel = dayOfWeek.toString();

            int screenTimeHours = entry.getValue();
            series.getData().add(new XYChart.Data<>(dayLabel, screenTimeHours));
        }

        // Add the series to the BarChart
        screenTime_BarChart.getData().add(series);

        // Remove the legend
        screenTime_BarChart.setLegendVisible(false);

        // Update the date range label
        LocalDate endDate = currentStartDate.plusDays(6);
        dateRangeLabel.setText(currentStartDate + " - " + endDate);
    }

    private Map<String, Integer> filterDataByWeek(Map<String, Integer> data) {
        // Calculate the end date of the week
        LocalDate endDate = currentStartDate.plusDays(6);

        // Create a new map to store filtered data
        Map<String, Integer> filteredData = new LinkedHashMap<>();

        // Iterate through the original data and add entries within the current week
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            String date = entry.getKey(); // Assuming date is in a format compatible with LocalDate
            LocalDate entryDate = LocalDate.parse(date); // Parse the date string to LocalDate

            // Check if the entry date is within the current week (inclusive)
            if (!entryDate.isBefore(currentStartDate) && !entryDate.isAfter(endDate)) {
                filteredData.put(date, entry.getValue());
            }
        }

        return filteredData;
    }
}
