package login;

import com.example.musicapp.MusicApplication;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import datab.DataBase;

public class loginController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Text statusMessage;

    // Instance of the Database class to check credentials
    private DataBase database = new DataBase();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Use the database login verification
        if (database.loginUser(username, password)) {
            statusMessage.setText("Login successful!");
            System.out.println("Login successful");

            try {
                // Launch the MusicApplication
                MusicApplication musicApp = new MusicApplication();
                musicApp.start(new Stage());

                // Close the login stage
                Stage loginStage = (Stage) usernameField.getScene().getWindow();
                loginStage.close();

            } catch (Exception e) {
                e.printStackTrace();
                statusMessage.setText("Failed to load Music Application.");
            }
        } else {
            statusMessage.setText("Invalid username or password.");
        }
    }

    // Method to open the registration screen
    public void handleRegister() throws Exception {
        register screen = new register();
        screen.start(new Stage());
    }
}
