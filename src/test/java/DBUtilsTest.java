
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.moodtracker.model.DBUtils;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.sql.*;
import java.util.concurrent.*;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

class DBUtilsTest {

    private static final String DATABASE_URL = "jdbc:sqlite:moodtracker.db";
    private static Connection connection;

    @BeforeAll
    static void setUp() throws InterruptedException {
        // Set up JavaFX environment
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(() -> {
            new JFXPanel(); // Initializes the JavaFX Toolkit
            latch.countDown();
        });
        latch.await(5, TimeUnit.SECONDS);

        // Set up SQLite in-memory database
        try {
            connection = DriverManager.getConnection(DATABASE_URL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        DBUtils.createDatabase();
    }

    @AfterAll
    static void tearDown() throws SQLException, InterruptedException {
        // Shut down JavaFX environment
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            Platform.exit();
            latch.countDown();
        });
        latch.await(5, TimeUnit.SECONDS);

        if (connection != null) {
            connection.close();
        }
    }

    @Test
    void testDatabaseConnection() {
        // Attempt to establish a connection to the SQLite database
        try (Connection connection = DriverManager.getConnection(DATABASE_URL)) {
            // Check if connection is not null
            assertNotNull(connection);
            // Check if the connection is valid
            assertTrue(connection.isValid(5)); // Timeout in seconds (5 seconds timeout)
        } catch (SQLException e) {
            fail("Failed to connect to the database: " + e.getMessage());
        }
    }

    @Test
    void testCreateDatabase() {
        DBUtils.createDatabase();
        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             Statement statement = connection.createStatement()) {
            // Check if the users table exists
            ResultSet resultSet = statement.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='users'");
            boolean tableExists = resultSet.next();
            assertEquals(true, tableExists);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testChangeScene() {
        // Test with username
        ActionEvent eventWithUsername = new ActionEvent(new Button(), null);
        String fxmlFileWithUsername = "logged-in.fxml";
        String titleWithUsername = "Welcome";
        String username = "testuser";

        DBUtils.changeScene(eventWithUsername, fxmlFileWithUsername, titleWithUsername);

        Stage stageWithUsername = (Stage) eventWithUsername.getSource();
        assertNotNull(stageWithUsername);
        assertEquals(titleWithUsername, stageWithUsername.getTitle());
        Scene sceneWithUsername = stageWithUsername.getScene();
        assertNotNull(sceneWithUsername);
        Parent rootWithUsername = sceneWithUsername.getRoot();
        assertNotNull(rootWithUsername);

        // Test without username
        ActionEvent eventWithoutUsername = new ActionEvent(new Button(), null);
        String fxmlFileWithoutUsername = "logged-in.fxml";
        String titleWithoutUsername = "Welcome";

        DBUtils.changeScene(eventWithoutUsername, fxmlFileWithoutUsername, titleWithoutUsername);

        Stage stageWithoutUsername = (Stage) eventWithoutUsername.getSource();
        assertNotNull(stageWithoutUsername);
        assertEquals(titleWithoutUsername, stageWithoutUsername.getTitle());
        Scene sceneWithoutUsername = stageWithoutUsername.getScene();
        assertNotNull(sceneWithoutUsername);
        Parent rootWithoutUsername = sceneWithoutUsername.getRoot();
        assertNotNull(rootWithoutUsername);

        // Test with invalid file
        ActionEvent eventInvalidFile = new ActionEvent(new Button(), null);
        String fxmlFileInvalid = "non-existing-file.fxml";
        String titleInvalid = "Welcome";

        assertThrows(IOException.class, () -> DBUtils.changeScene(eventInvalidFile, fxmlFileInvalid, titleInvalid));
    }

    @Test
    void signUpUser() {
        // Test signing up a new user
        ActionEvent event = new ActionEvent();
        String username = "testuser";
        String email = "test@example.com";
        String password = "password";

        // Wrap JavaFX-related code inside Platform.runLater() to ensure it's executed on the FX application thread
        Platform.runLater(() -> {
            DBUtils.signUpUser(event, username, email, password, null);

            // Check if the user is inserted into the database
            assertDoesNotThrow(() -> connection.prepareStatement("SELECT * FROM users WHERE username = '" + username + "'").execute());
        });
    }

}
