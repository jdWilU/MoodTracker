package org.example.moodtracker.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.LineChart;
import javafx.scene.paint.Color;
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
    private Pane pane_donut;
    @FXML
    private Pane pane_barchart;
    @FXML
    private Pane pane_linegraph;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //Create Placeholder Pie Graph
        createPieChartDummy();

        //Create Placeholder Bar Graph
        createBarchartDummy();

        //Create Placeholder Line Bar Graph
        createLinechartDummy();

        button_logout.setOnAction(event -> DBUtils.changeScene(event, "login.fxml", "Log In", null));
        button_close.setOnAction(actionEvent -> UIUtils.closeApp((Stage) button_close.getScene().getWindow()));
        button_table.setOnAction(event -> DBUtils.changeScene(event, "tableView.fxml", "Table View", null));
        button_daily_entry.setOnAction(event -> DBUtils.changeScene(event, "mood-tracking-page.fxml", "Mood Tracking", null));
        button_profile.setOnAction(event -> DBUtils.changeScene(event,"profile.fxml","Profile",null));

        // Set user information and current date
        String currentUser = DBUtils.getCurrentUsername();
        if (currentUser != null) {
            UIUtils.setUserInformation(label_welcome, currentUser);
        }
        UIUtils.setCurrentDate(current_date);
    }


    //---------------------------Placeholder functions------------------------------------

    public void createPieChartDummy(){
        // Create dummy data for the pie chart & style the chart
        PieChart.Data slice1 = new PieChart.Data("Category 1", 15);
        PieChart.Data slice2 = new PieChart.Data("Category 2", 25);
        PieChart.Data slice3 = new PieChart.Data("Category 3", 10);
        PieChart.Data slice4 = new PieChart.Data("Category 3", 30);
        PieChart.Data slice5 = new PieChart.Data("Category 3", 20);

        // Create a pie chart
        PieChart pieChart = new PieChart();
        pieChart.getData().addAll(slice1, slice2, slice3, slice4, slice5);
        pieChart.setTitle("Donut Graph");

        // Set custom style for the pie chart (to resemble a donut)
        pieChart.setLabelsVisible(false);
        pieChart.setLegendVisible(true);
        pieChart.setStartAngle(90);

        // Add the pie chart to the pane
        pane_donut.getChildren().add(pieChart);

        // Set the size of the pie chart to match the pane
        pieChart.setPrefSize(pane_donut.getPrefWidth(), pane_donut.getPrefHeight());

    }

    public void createBarchartDummy(){
        // Create a CategoryAxis for the X-axis (days of the week)
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setCategories(FXCollections.observableArrayList(
                "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"
        ));

        // Create a NumberAxis for the Y-axis (hours)
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Hours");

        // Create a BarChart
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Screen Time");

        // Populate the BarChart with dummy data (screen time in hours)
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("Mon", 3));
        series.getData().add(new XYChart.Data<>("Tue", 4));
        series.getData().add(new XYChart.Data<>("Wed", 5));
        series.getData().add(new XYChart.Data<>("Thu", 2));
        series.getData().add(new XYChart.Data<>("Fri", 6));
        series.getData().add(new XYChart.Data<>("Sat", 7));
        series.getData().add(new XYChart.Data<>("Sun", 4));

        // Add the series to the BarChart
        barChart.getData().add(series);

        // Set preferred width and height of the BarChart
        barChart.setPrefSize(320, 210);

        // Add the BarChart to the pane_barchart pane
        pane_barchart.getChildren().add(barChart);
    }

    public void createLinechartDummy(){
        // Create a NumberAxis for the X-axis (days 1 to 25)
        NumberAxis xAxis = new NumberAxis(1, 25, 1);
        xAxis.setLabel("Days");

        // Create a NumberAxis for the Y-axis (five-point scale)
        NumberAxis yAxis = new NumberAxis(1, 5, 1); // Min: 1, Max: 5, Tick Unit: 1
        yAxis.setLabel("Scale");

        // Create a LineChart
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Scale Values per Day");

        // Populate the LineChart with dummy data (scale values)
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        for (int day = 1; day <= 25; day++) {
            series.getData().add(new XYChart.Data<>(day, (int) (Math.random() * 5) + 1));
        }
        lineChart.getData().add(series);

        // Customize line color based on data point value
        for (XYChart.Data<Number, Number> data : series.getData()) {
            double value = data.getYValue().doubleValue();
            Color lineColor = getColorForValue(value);
            data.getNode().setStyle("-fx-stroke: " + colorToHex(lineColor) + ";");
        }

        // Set preferred width and height of the LineChart
        lineChart.setPrefSize(550, 250);

        pane_linegraph.getChildren().add(lineChart);
        pane_linegraph.layoutXProperty().bind(pane_linegraph.widthProperty().subtract(lineChart.getPrefWidth()).divide(2));
    }

    // Helper method to get color based on value (red to green gradient)
    private Color getColorForValue(double value) {
        double hue = (120.0 - (value - 1.0) * 60.0) / 360.0;
        return Color.hsb(hue, 1.0, 1.0);
    }

    // Helper method to convert Color to hexadecimal string
    private String colorToHex(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
}