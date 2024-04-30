package org.example.moodtracker.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.Node;

public class ActivityController {

    @FXML
    private GridPane activityGrid; // Reference to the GridPane to access all buttons.

    @FXML
    private void handleIconClick(MouseEvent event) {
        Button clickedButton = (Button) event.getSource();
        // Toggle selected style
        if (clickedButton.getStyle().contains("lightgray")) {
            clickedButton.setStyle("-fx-background-color: transparent; -fx-padding: 10;");
        } else {
            clickedButton.setStyle("-fx-background-color: lightgray; -fx-padding: 10;");
        }
    }

    @FXML
    public void initialize() {
        // Ensure all buttons have the initial correct style set
        for (Node node : activityGrid.getChildren()) {
            if (node instanceof Button) {
                Button button = (Button) node;
                button.setStyle("-fx-background-color: transparent; -fx-padding: 10;");
            }
        }


    }
}