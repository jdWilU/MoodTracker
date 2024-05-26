package org.example.moodtracker.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.moodtracker.model.DBUtils;
import org.example.moodtracker.model.UIUtils;

import java.io.Console;
import java.net.URL;
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
    private MFXButton button_achievement;
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
        // Button functionality
        button_close.setOnAction(actionEvent -> UIUtils.closeApp((Stage) button_close.getScene().getWindow()));

        button_homepage.setOnAction(event -> DBUtils.changeScene(event, "homepage.fxml", "Home", null));
        button_logout.setOnAction(event -> DBUtils.changeScene(event, "login.fxml", "Log In", null));
        button_table.setOnAction(event -> DBUtils.changeScene(event, "tableView.fxml", "Table View", null));
        button_profile.setOnAction(event -> DBUtils.changeScene(event, "profile.fxml", "Profile", null));
        button_daily_entry.setOnAction(event -> DBUtils.changeScene(event, "mood-tracking-page.fxml", "Mood Tracking", null));
        button_achievement.setOnAction(event -> DBUtils.changeScene(event, "achievementsPage.fxml", "Achievements", null));

        // Set user information and current date
        String currentUser = DBUtils.getCurrentUsername();
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
        resources_text.setText("test text");
        resources_title.setText("test text");
    }

    private void EnableDetailPane1 () {
        EnableDetailPanel();
    }
    private void EnableDetailPane2 () {
        EnableDetailPanel();
    }
    private void EnableDetailPane3 () {
        EnableDetailPanel();
    }
    private void EnableDetailPane4 () {
        EnableDetailPanel();
    }
}
