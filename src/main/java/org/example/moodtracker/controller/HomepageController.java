package org.example.moodtracker.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.example.moodtracker.model.DBUtils;
import org.example.moodtracker.model.UIUtils;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class HomepageController implements Initializable {


    @FXML
    private Button button_logout;
    @FXML
    private Button button_close;
    @FXML
    private MFXButton button_table;
    @FXML
    private MFXButton button_daily_entry;
    @FXML
    private MFXButton button_profile;
    @FXML
    private MFXButton button_resources;
    @FXML
    private Button button_previous_week;
    @FXML
    private Button button_next_week;
    @FXML
    private Button button_previous_fortnight;
    @FXML
    private Button button_next_fortnight;
    @FXML
    private MFXButton button_achievement;
    @FXML
    private ProgressBar xpLevelTopBar;
    @FXML
    private Label levelLabelTopBar;
    @FXML
    private Label label_welcome;
    @FXML
    private Label current_date;
    @FXML
    private PieChart mood_Pie;
    @FXML
    private Label badLabel;
    @FXML
    private Label poorLabel;
    @FXML
    private Label okayLabel;
    @FXML
    private Label goodLabel;
    @FXML
    private Label greatLabel;
    @FXML
    private BarChart<String, Number> screenTime_BarChart;
    @FXML
    private Label dateRangeLabel;
    @FXML
    private Label moodDateRangeLabel;
    @FXML
    private NumberAxis screenTimeYAxis;
    @FXML
    private LineChart<String, Number> lineChartMoodFluctuations;
    @FXML
    private CategoryAxis xAxisDates;
    @FXML
    private NumberAxis yAxisMoodRatings;
    @FXML

    private LocalDate currentStartDate = LocalDate.now().minusDays(6); // Start date of the current week
    private LocalDate currentFortnightStartDate = LocalDate.now().minusDays(13);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        button_logout.setOnAction(event -> DBUtils.changeScene(event, "login.fxml", "Log In", null));
        button_close.setOnAction(actionEvent -> UIUtils.closeApp((Stage) button_close.getScene().getWindow()));
        button_table.setOnAction(event -> DBUtils.changeScene(event, "tableView.fxml", "Table View", null));
        button_profile.setOnAction(event -> DBUtils.changeScene(event, "profile.fxml", "Profile", null));
        button_achievement.setOnAction(event -> DBUtils.changeScene(event, "achievementsPage.fxml", "Achievements", null));
        button_resources.setOnAction(event -> DBUtils.changeScene(event, "resources-page.fxml", "Educational Resources", null));

        // Set user information and current date
        String currentUser = DBUtils.getCurrentUsername();
        if (currentUser != null) {
            UIUtils.setUserInformation(label_welcome, currentUser);
            UIUtils.setCurrentDate(current_date);
            initializeXPBar(currentUser);

            try {
                initializeMoodPieChart(currentUser);
                initializeScreenTimeBarChart(currentUser);
                initializeMoodFluctuationsLineChart(currentUser);
            } catch (SQLException e) {
                Logger.getLogger(HomepageController.class.getName()).log(Level.SEVERE, "Error fetching data", e);
            }

            button_daily_entry.setOnAction(event -> {
                try {
                    int userId = DBUtils.getUserId(currentUser);
                    boolean entryExists = DBUtils.entryExistsForToday(userId);
                    if (entryExists) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Daily Entry Already Exists!");
                        alert.setHeaderText(null);
                        alert.setContentText("You have already made a daily entry for today.");
                        alert.showAndWait();
                    } else {
                        DBUtils.changeScene(event, "mood-tracking-page.fxml", "Mood Tracking", null);
                    }
                } catch (SQLException e) {
                    Logger.getLogger(HomepageController.class.getName()).log(Level.SEVERE, "Error checking today's entry", e);
                }
            });
        }

        // Initially hide the "Next Week" button
        button_next_week.setVisible(false);
        button_next_fortnight.setVisible(false);

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

        button_previous_fortnight.setOnAction(event -> {
            currentFortnightStartDate = currentFortnightStartDate.minusDays(14);
            button_next_fortnight.setVisible(true); // Show the "Next Fortnight" button
            try {
                initializeMoodFluctuationsLineChart(currentUser);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        button_next_fortnight.setOnAction(event -> {
            currentFortnightStartDate = currentFortnightStartDate.plusDays(14);
            if (currentFortnightStartDate.plusDays(13).isAfter(LocalDate.now())) {
                button_next_fortnight.setVisible(false); // Hide the "Next Fortnight" button if the current fortnight is reached
            }
            try {
                initializeMoodFluctuationsLineChart(currentUser);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

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

    public void initializeXPBar(String currentUser) {
        if (currentUser != null) {
            try {
                int userId = DBUtils.getUserId(currentUser);
                int xp = DBUtils.getXpForUser(userId);
                int level = DBUtils.getUserLevel(userId);
                int xpForCurrentLevel = xp - (level * 100);
                double progress = (double) xpForCurrentLevel / 100.0;

                // Set progress for the XP bar
                xpLevelTopBar.setProgress(progress);
                levelLabelTopBar.setText("" + level);
            } catch (SQLException e) {
                // Handle SQLException
                Logger.getLogger(HomepageController.class.getName()).log(Level.SEVERE, "Error fetching user's level and XP", e);
            }
        }
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
                    "#fe6969", // Red for BAD mood
                    "#a364f8", // Purple for POOR mood
                    "#2cb2ff", // Blue for GOOD mood
            };

            // Calculate the total count of moods
            int totalCount = moodCounts.values().stream().mapToInt(Integer::intValue).sum();

            // Initialize index for custom colors
            int index = 0;

            // Populate PieChart with mood data and update labels with percentages
            for (Map.Entry<String, Integer> entry : moodCounts.entrySet()) {
                String mood = entry.getKey();
                int count = entry.getValue();
                double percentage = (count * 100.0) / totalCount;

                // Create PieChart.Data item
                PieChart.Data data = new PieChart.Data(mood, count);

                // Add data to PieChart (this initializes the Node)
                mood_Pie.getData().add(data);

                // Apply style class to the Node of the PieChart.Data
                if (data.getNode() != null) {
                    // Set the custom color defined in the CSS
                    data.getNode().setStyle("-fx-pie-color: " + customColors[index] + ";");
                }

                // Update the corresponding label with percentage on a new line
                String labelText = mood + "\n" + String.format("%.1f", percentage) + "%";
                Label moodLabel = null;

                switch (mood.toUpperCase()) {
                    case "BAD":
                        moodLabel = badLabel;
                        break;
                    case "POOR":
                        moodLabel = poorLabel;
                        break;
                    case "OKAY":
                        moodLabel = okayLabel;
                        break;
                    case "GOOD":
                        moodLabel = goodLabel;
                        break;
                    case "GREAT":
                        moodLabel = greatLabel;
                        break;
                }

                if (moodLabel != null) {
                    moodLabel.setText(labelText);
                    moodLabel.setTextAlignment(TextAlignment.CENTER);
                    moodLabel.setAlignment(Pos.CENTER);
                }

                index++; // Move to the next custom color
            }

            // Adjust the radius of the PieChart
            double radius = Math.min(mood_Pie.getWidth(), mood_Pie.getHeight()) / 2;
            mood_Pie.setPrefWidth(radius * 5);
            mood_Pie.setPrefHeight(radius * 5);

            // Apply the CSS stylesheet
            mood_Pie.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/Styling/Styling.css")).toExternalForm());

        } catch (SQLException e) {
            System.err.println("Error fetching mood data: " + e.getMessage());
        }
    }



    private void initializeScreenTimeBarChart(String currentUser) throws SQLException {
        // Get screen time data for the current user
        Map<String, Integer> screenTimeData = DBUtils.getScreenTimeDataForUser(currentUser);

        // Filter data to include only the current week starting from currentStartDate
        Map<String, Integer> filteredData = filterDataByWeek(screenTimeData);

        // Generate the full range of dates for the current week
        Map<String, Integer> completeData = new LinkedHashMap<>();
        LocalDate currentDate = currentStartDate;
        for (int i = 0; i < 7; i++) {
            String dateStr = currentDate.toString();
            completeData.put(dateStr, filteredData.getOrDefault(dateStr, 0));
            currentDate = currentDate.plusDays(1);
        }

        // Clear existing BarChart data
        screenTime_BarChart.getData().clear();

        // Populate BarChart with screen time data
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Screen Time");

        // Sort the complete data by date
        Map<String, Integer> sortedData = completeData.entrySet()
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
            String dayLabel = date.getDayOfWeek().toString().substring(0, 1).toUpperCase() + date.getDayOfWeek().toString().substring(1, 3).toLowerCase(); // Concatenate first letter in uppercase with last two letters in lowercase
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
            data.nodeProperty().addListener((observable, oldValue, node) -> node.setStyle("-fx-bar-fill: " + color + ";"));
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
        // Fetch mood data for the specified fortnight
        ObservableList<XYChart.Data<String, Number>> moodData = FXCollections.observableArrayList();
        ObservableList<String> dates = FXCollections.observableArrayList();

        // Populate dates list with the current fortnight
        LocalDate currentDate = currentFortnightStartDate;
        for (int i = 0; i < 14; i++) {
            String dateStr = currentDate.toString();
            dates.add(dateStr);
            currentDate = currentDate.plusDays(1);
        }

        // Clear and set x-axis categories
        xAxisDates.getCategories().clear();
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
            String moodString = moodEntries.get(date); // Get mood string for the date

            if (moodString != null) {
                String moodColor = moodColors.getOrDefault(moodString.toUpperCase(), "#fe6969"); // Default to red if mood color not found

                int moodRating = switch (moodString.toUpperCase()) {
                    case "GREAT" -> 5;
                    case "GOOD" -> 4;
                    case "OKAY" -> 3;
                    case "POOR" -> 2;
                    case "BAD" -> 1;
                    default -> 0;
                };

                XYChart.Data<String, Number> data = new XYChart.Data<>(date, moodRating);
                moodData.add(data);

                // Customize the symbol (circle) for this data point
                Circle circle = new Circle(5); // Define the size of the circle
                circle.setFill(Color.web(moodColor)); // Set the color based on mood
                data.setNode(circle);
            }
        }

        // Clear existing LineChart data
        lineChartMoodFluctuations.getData().clear();

        // Create series and add data to the line chart
        XYChart.Series<String, Number> series = new XYChart.Series<>(moodData);
        series.setName("Mood Fluctuations");
        lineChartMoodFluctuations.getData().add(series);

        // Customize chart appearance
        yAxisMoodRatings.setAutoRanging(false); // Disable auto-ranging
        yAxisMoodRatings.setLowerBound(0); // Set lower bound to "BAD"
        yAxisMoodRatings.setUpperBound(6); // Set upper bound to "GREAT"
        yAxisMoodRatings.setTickUnit(0.5); // Set tick unit to 1
        yAxisMoodRatings.setTickLabelGap(10); // Adjust gap between tick labels and axis
        yAxisMoodRatings.setTickMarkVisible(false); // Hide tick marks

        // Customize Y-axis tick labels to show mood strings
        yAxisMoodRatings.setTickLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Number object) {
                int moodRating = object.intValue();
                return switch (moodRating) {
                    case 1 -> "BAD";
                    case 2 -> "POOR";
                    case 3 -> "OKAY";
                    case 4 -> "GOOD";
                    case 5 -> "GREAT";
                    default -> "";
                };
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

        // Update the date range label
        LocalDate endDate = currentFortnightStartDate.plusDays(13);
        moodDateRangeLabel.setText(currentFortnightStartDate + " - " + endDate);
    }


}