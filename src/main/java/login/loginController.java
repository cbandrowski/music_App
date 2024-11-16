package login;

import com.example.musicapp.MusicController;
import datab.DataBase;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class loginController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Text statusMessage;

    // Instance of the Database class to check credentials
    private DataBase database = new DataBase();
    MusicController mC = new MusicController();

    @FXML
    private void handleLogin() {

        String email = usernameField.getText();
        String password = passwordField.getText();

        // Use the database login verification
        if (database.loginUser(email, password)) {
            statusMessage.setText("Login successful!");
            System.out.println("Login successful");

            try {
                // Fetch the user's full name from the database
                String fullName = database.getUserFullName(email);
                System.out.println("Retrieved user full name: " + fullName); // Debugging line

                if (fullName != null) {
                    // Load the MusicApplication FXML file
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/musicresources/music-view.fxml"));
                    Parent root = loader.load();

                    // Get the controller from the FXMLLoader
                    MusicController musicController = loader.getController();
                    musicController.setUserName(fullName); // Set the full name in the controller

                    // Launch the MusicApplication
                    Stage musicStage = new Stage();
                    musicStage.setScene(new Scene(root));
                    musicStage.show();

                    // Close the login stage
                    Stage loginStage = (Stage) usernameField.getScene().getWindow();
                    loginStage.close();
                } else {
                    statusMessage.setText("Error fetching user details.");
                }

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
