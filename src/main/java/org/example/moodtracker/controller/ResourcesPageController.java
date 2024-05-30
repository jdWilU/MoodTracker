package org.example.moodtracker.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.example.moodtracker.model.DBUtils;
import org.example.moodtracker.model.UIUtils;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ResourcesPageController implements Initializable {

    @FXML
    private MFXButton button_homepage;
    @FXML
    private Button button_close;
    @FXML
    private Button button_logout;
    @FXML
    private MFXButton button_profile;
    @FXML
    private MFXButton button_table;
    @FXML
    private MFXButton button_daily_entry;
    @FXML
    private Label label_welcome;
    @FXML
    private Label current_date;
    @FXML
    private Pane details_pane;
    @FXML
    private Button resource_button_1;
    @FXML
    private Button resource_button_2;
    @FXML
    private Button resource_button_3;
    @FXML
    private Button resource_button_4;
    @FXML
    private Button exit_resource_window_button;
    @FXML
    private Label resources_title;
    @FXML
    private Label resources_text;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Update column in user database for achievement check
        String currentUser = DBUtils.getCurrentUsername();
        if (currentUser != null) {
            try {
                // Get user ID
                int userId = DBUtils.getUserId(currentUser);

                // Update visited_education column for the user
                DBUtils.updateVisitedEducation(userId);
            } catch (SQLException e) {
                // Handle SQLException
                System.err.println("Error updating visited_education column: " + e.getMessage());
            }
        }

        // Button functionality
        button_close.setOnAction(actionEvent -> UIUtils.closeApp((Stage) button_close.getScene().getWindow()));

        button_homepage.setOnAction(event -> DBUtils.changeScene(event, "homepage.fxml", "Home", null));
        button_logout.setOnAction(event -> DBUtils.changeScene(event, "login.fxml", "Log In", null));
        button_table.setOnAction(event -> DBUtils.changeScene(event, "tableView.fxml", "Table View", null));
        button_profile.setOnAction(event -> DBUtils.changeScene(event, "profile.fxml", "Profile", null));
        button_daily_entry.setOnAction(event -> DBUtils.changeScene(event, "mood-tracking-page.fxml", "Mood Tracking", null));

        // Set user information and current date
        if (currentUser != null) {
            UIUtils.setUserInformation(label_welcome, currentUser);
        }
        UIUtils.setCurrentDate(current_date);

        details_pane.setVisible(false);

        resource_button_1.setOnAction(event -> EnableDetailPane1());
        resource_button_2.setOnAction(event -> EnableDetailPane2());
        resource_button_3.setOnAction(event -> EnableDetailPane3());
        resource_button_4.setOnAction(event -> EnableDetailPane4());
        exit_resource_window_button.setOnAction(event -> details_pane.setVisible(false));
    }
    private void EnableDetailPanel()
    {
        details_pane.setVisible(true);
    }

    private void EnableDetailPane1 () {
        EnableDetailPanel();
        resources_text.setText("Begin by defining specific, achievable fitness goals. Whether you aim to lose weight, build muscle, improve endurance, or enhance overall health, having clear objectives will guide your workout routine and keep you motivated. Use the SMART criteria (Specific, Measurable, Achievable, Relevant, Time-bound) to set your goals.\n" +
                "Design a workout plan that includes a mix of cardiovascular exercise, strength training, flexibility, and balance exercises. A balanced routine ensures that you address all aspects of fitness and reduce the risk of injury. Aim for at least 150 minutes of moderate aerobic activity or 75 minutes of vigorous activity per week, along with muscle-strengthening activities on two or more days a week.");
        resources_title.setText("Working Out");
    }
    private void EnableDetailPane2 () {
        EnableDetailPanel();
        resources_text.setText("Design a daily schedule that allocates time for screen-based activities as well as offline pursuits. Include dedicated periods for work or study, exercise, hobbies, and social interactions that don't involve screens. By scheduling your time intentionally, you can ensure a balanced and fulfilling routine.\n" +
                "Establish certain areas in your home as screen-free zones, such as the dining room, bedroom, or living room during family time. This helps create physical boundaries between screen activities and other aspects of your life, encouraging more face-to-face interactions and better sleep hygiene.");
        resources_title.setText("Screen Time");
    }
    private void EnableDetailPane3 () {
        EnableDetailPanel();
        resources_text.setText("Start by identifying your most important tasks. Use the Eisenhower Matrix to categorize tasks based on their urgency and importance. Focus on completing high-priority tasks first to ensure that you are making progress on what truly matters. By prioritizing effectively, you can avoid the trap of spending too much time on less critical activities.\n" +
                "Take a few minutes each morning to plan your day. Create a to-do list and allocate specific time slots for each task. This helps in maintaining a structured approach and ensures that you are aware of what needs to be accomplished. Make sure to include breaks and buffer times to account for unexpected interruptions or delays.\n");
        resources_title.setText("Time Management");
    }
    private void EnableDetailPane4 () {
        EnableDetailPanel();
        resources_text.setText("Define clear, achievable goals for each study session. Break down larger tasks into smaller, manageable chunks and set deadlines for each. This approach not only makes studying less overwhelming but also provides a sense of accomplishment as you complete each task.\n" +
                "Consistency is key to effective studying. Create a study schedule that outlines what you need to study and when. Allocate specific time slots for each subject and stick to the schedule as closely as possible. Regular study sessions help reinforce learning and improve retention.");
        resources_title.setText("Study Habits");
    }


}
