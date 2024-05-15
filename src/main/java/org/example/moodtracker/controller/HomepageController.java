package org.example.moodtracker.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.sql.SQLException;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import org.example.moodtracker.model.DBUtils;
import org.example.moodtracker.model.UIUtils;

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        button_logout.setOnAction(event -> DBUtils.changeScene(event, "login.fxml", "Log In", null));
        button_close.setOnAction(actionEvent -> UIUtils.closeApp((Stage) button_close.getScene().getWindow()));
        button_table.setOnAction(event -> DBUtils.changeScene(event, "tableView.fxml", "Table View", null));
        button_profile.setOnAction(event -> DBUtils.changeScene(event, "profile.fxml", "Profile", null));

        // Load external CSS file for styling PieChart
        String cssPath = "/Styling/Styling.css"; // Path relative to the resources directory
        mood_Pie.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
        System.out.println("Loaded Stylesheets: " + mood_Pie.getStylesheets());

        button_daily_entry.setOnAction(event -> DBUtils.changeScene(event, "mood-tracking-page.fxml", "Mood Tracking", null));
        button_profile.setOnAction(event -> DBUtils.changeScene(event,"profile.fxml","Profile",null));

        // Set user information and current date
        String currentUser = DBUtils.getCurrentUsername();
        if (currentUser != null) {
            UIUtils.setUserInformation(label_welcome, currentUser);

            try {
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
            } catch (SQLException e) {
                System.err.println("Error fetching mood data: " + e.getMessage());
            }
        }
        UIUtils.setCurrentDate(current_date);
    }
}
