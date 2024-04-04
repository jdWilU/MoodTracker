package org.example.moodtracker;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.sql.*;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBUtils {
    public static void changeScene(ActionEvent event, String fxmlFile, String title, String username){
        Parent root = null;
        if (username != null){
            try {
                FXMLLoader loader = new FXMLLoader(DBUtils.class.getClassLoader().getResource(fxmlFile));
                root = loader.load();
                LoggedInController loggedInController = loader.getController();
                loggedInController.setUserInformation(username);
            } catch (IOException e){
                Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, "Error loading FXML file: " + fxmlFile, e);
            }
        } else {
            try {
                root = FXMLLoader.load(Objects.requireNonNull(DBUtils.class.getClassLoader().getResource(fxmlFile)));
            } catch(IOException e){
                Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, "Error loading FXML file: " + fxmlFile, e);
            }
        }
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        assert root != null;
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }

    public static void signUpUser(ActionEvent event, String username, String email, String password){
        Connection connection = null;
        PreparedStatement psInsert = null;
        PreparedStatement psCheckUserExists = null;
        ResultSet resultSet = null;

        try{
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/moodtracker", "root", "password");
            psCheckUserExists = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
            psCheckUserExists.setString(1, username);
            resultSet = psCheckUserExists.executeQuery();

            if (resultSet.isBeforeFirst()){
                System.out.println("User already exists!");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("You cannot use this username.");
                alert.show();
            } else {
                psInsert = connection.prepareStatement("INSERT INTO users(username, email, password VALUES (?, ?, ?)");
                psInsert.setString(1, username);
                psInsert.setString(2, email);
                psInsert.setString(3, password);
                psInsert.executeUpdate();

                changeScene(event, "logged-in.fxml", "Welcome", username);
            }
        } catch (SQLException e){
            Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, "Error executing SQL queries", e);
        } finally {
            if (resultSet != null){
                try {
                    resultSet.close();
                } catch (SQLException e){
                    Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, "Error closing ResultSet", e);
                }
            }
            if (psCheckUserExists != null) {
                try{
                    psCheckUserExists.close();
                } catch (SQLException e){
                    Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, "Error closing psCheckUserExists", e);
                }
            }
            if (psInsert != null){
                try{
                    psInsert.close();
                } catch (SQLException e){
                    Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, "Error closing psInsert", e);
                }
            }
            if (connection != null){
                try{
                    connection.close();
                } catch(SQLException e){
                    Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, "Error closing connection", e);
                }
            }
        }
    }

    public static void logInUser(ActionEvent event, String username, String password){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try{
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/moodtracker", "root", "password");
            preparedStatement = connection.prepareStatement(("SELECT password FROM users WHERE username = ?"));
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();

            if (!resultSet.isBeforeFirst()){
                System.out.println("User not found in the database");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Provided credentials are incorrect!");
                alert.show();
            } else {
                while (resultSet.next()) {
                    String retrievedPassword = resultSet.getString("password");
                    if (retrievedPassword.equals(password)){
                        changeScene(event, "logged-in.fxml", "Welcome", username);

                    } else {
                        System.out.println("Passwords do not match!");
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("The provided credentials are incorrect!");
                        alert.show();

                    }
                }
            }
        } catch (SQLException e){
            Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, "Error executing SQL queries", e);
        } finally {
            if (resultSet != null){
                try {
                    resultSet.close();
                } catch (SQLException e){
                    Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, "Error closing ResultSet", e);
                }
            }
            if (preparedStatement != null){
                try {
                    preparedStatement.close();
                } catch (SQLException e){
                    Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, "Error closing PreparedStatement", e);
                }
            }
            if (connection != null){
                try{
                    connection.close();
                } catch (SQLException e){
                    Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, "Error closing Connection", e);
                }
            }
        }
    }
}
