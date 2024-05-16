package org.example.moodtracker.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.example.moodtracker.model.DBUtils;
import org.example.moodtracker.model.UIUtils;

import java.net.URL;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
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
    private Label dateRangeLabel;
    @FXML
    private NumberAxis screenTimeYAxis;
    @FXML
    private LineChart<String, Number> lineChartMoodFluctuations;
    @FXML
    private CategoryAxis xAxisDates;
    @FXML
    private NumberAxis yAxisMoodRatings;

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
                initializeMoodFluctuationsLineChart(currentUser);
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
        System.out.println("Loaded Stylesheets: " + mood_Pie.getStylesheets());

        // Set the Y-axis bounds
        screenTimeYAxis.setAutoRanging(false);
        screenTimeYAxis.setLowerBound(0);
        screenTimeYAxis.setUpperBound(16);
        screenTimeYAxis.setTickUnit(1);
    }

    private void initializeMoodPieChart(String currentUser) {
        try {
            // Get mood data for the current user
            Map<String, Integer> moodCounts = DBUtils.getMoodCountsForUser(currentUser);

            // Clear existing PieChart data
            mood_Pie.getData().clear();

            // Define the custom colors from the CSS file
            String[] customColors = {
                    "#838383", // Gray for OKAY mood
                    "#20e49f", // Green for GREAT mood
                    "#fe6969",  // Red for BAD mood
                    "#a364f8", // Purple for POOR mood
                    "#2cb2ff", // Blue for GOOD mood

            };

            // Populate PieChart with mood data
            int index = 0;
            for (Map.Entry<String, Integer> entry : moodCounts.entrySet()) {
                String mood = entry.getKey();
                int count = entry.getValue();

                // Create PieChart.Data item
                PieChart.Data data = new PieChart.Data(mood, count);

                // Add data to PieChart (this initializes the Node)
                mood_Pie.getData().add(data);

                // Apply style class to the Node of the PieChart.Data
                if (data.getNode() != null) {
                    // Set the custom color defined in the CSS
                    data.getNode().setStyle("-fx-pie-color: " + customColors[index] + ";");

                }

                index++; // Move to the next custom color
            }

        } catch (SQLException e) {
            System.err.println("Error fetching mood data: " + e.getMessage());
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
            String dayLabel = date.getDayOfWeek().toString().substring(0, 1) +
                    date.getDayOfWeek().toString().substring(1, 2).toLowerCase(); // Convert the second letter to lowercase
            int screenTimeHours = entry.getValue();
            XYChart.Data<String, Number> data = new XYChart.Data<>(dayLabel, screenTimeHours);

            // Set color based on screen time
            String color;
            if (screenTimeHours <= 1) {
                color = "#20e49f"; // Green
            } else if (screenTimeHours == 2) {
                color = "#2cb2ff"; // Blue
            } else if (screenTimeHours >= 3 && screenTimeHours <= 4) {
                color = "#838383"; // Gray
            } else if (screenTimeHours >= 5 && screenTimeHours <= 6) {
                color = "#a364f8"; // Purple
            } else {
                color = "#fe6969"; // Red
            }

            // Set color dynamically and add data to series
            data.nodeProperty().addListener((observable, oldValue, node) -> {
                node.setStyle("-fx-bar-fill: " + color + ";");
            });
            series.getData().add(data);
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

    private void initializeMoodFluctuationsLineChart(String currentUser) throws SQLException {
        // Fetch mood data for the last 14 days
        ObservableList<XYChart.Data<String, Number>> moodData = FXCollections.observableArrayList();
        ObservableList<String> dates = FXCollections.observableArrayList();

        // Populate dates list with the last 14 days
        for (int i = 13; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            dates.add(date.toString());
        }

        // Set x-axis categories
        xAxisDates.setCategories(dates);

        // Get mood data for the user
        Map<String, String> moodEntries = DBUtils.getMoodDataForUser(currentUser);

        // Map mood strings to corresponding ratings and colors
        Map<String, String> moodColors = new HashMap<>();
        moodColors.put("BAD", "#fe6969");   // Red
        moodColors.put("POOR", "#a364f8");  // Purple
        moodColors.put("OKAY", "#838383");  // Gray
        moodColors.put("GOOD", "#2cb2ff");  // Blue
        moodColors.put("GREAT", "#20e49f"); // Green

        // Populate moodData with mood fluctuations for each date
        for (String date : dates) {
            String moodString = moodEntries.getOrDefault(date, "BAD"); // Default mood to "BAD" if no entry found for the date
            String moodColor = moodColors.getOrDefault(moodString.toUpperCase(), "#fe6969"); // Default to red if mood color not found

            int moodRating = 0;
            switch (moodString.toUpperCase()) {
                case "GREAT":
                    moodRating = 5;
                    break;
                case "GOOD":
                    moodRating = 4;
                    break;
                case "OKAY":
                    moodRating = 3;
                    break;
                case "POOR":
                    moodRating = 2;
                    break;
                case "BAD":
                    moodRating = 1;
                    break;
            }

            XYChart.Data<String, Number> data = new XYChart.Data<>(date, moodRating);
            moodData.add(data);

            // Customize the symbol (circle) for this data point
            Circle circle = new Circle(5); // Define the size of the circle
            circle.setFill(Color.web(moodColor)); // Set the color based on mood
            data.setNode(circle);
        }

        // Create series and add data to the line chart
        XYChart.Series<String, Number> series = new XYChart.Series<>(moodData);
        series.setName("Mood Fluctuations");
        lineChartMoodFluctuations.getData().add(series);

        // Customize chart appearance
        yAxisMoodRatings.setLowerBound(1); // Set lower bound to "BAD"
        yAxisMoodRatings.setUpperBound(5); // Set upper bound to "GREAT"
        yAxisMoodRatings.setTickUnit(1); // Set tick unit to 1

        // Customize Y-axis tick labels to show mood strings
        yAxisMoodRatings.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                int moodRating = object.intValue();
                switch (moodRating) {
                    case 1:
                        return "BAD";
                    case 2:
                        return "POOR";
                    case 3:
                        return "OKAY";
                    case 4:
                        return "GOOD";
                    case 5:
                        return "GREAT";
                    default:
                        return "";
                }
            }

            @Override
            public Number fromString(String string) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });

        // Customize line chart appearance
        Node line = series.getNode().lookup(".chart-series-line");
        if (line != null) {
            line.setStyle("-fx-stroke: #838383;");
        }

        lineChartMoodFluctuations.setLegendVisible(false); // Remove the legend
    }


}


