package login;

import com.example.musicapp.MusicController;
import datab.DataBase;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import service.UserSession;

import java.io.IOException;

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

    public void handleLogin() {
        String email = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        // Asynchronous login task
        Task<Boolean> loginTask = new Task<>() {
            private int userId;
            private String fullName;

            private String profileImageUrl; // holds the profile image URL


            @Override
            protected Boolean call() throws Exception {
                database.connectToDatabase();

                // Verify user credentials
                if (database.loginUser(email, password)) {
                    // Fetch user details
                    userId = database.getUserId(email);
                    fullName = database.getUserFullName(email);
                    profileImageUrl = database.getUserProfileImageUrl(userId); // gets profile image URL


                    // Ensure valid user data is fetched
                    return userId > 0 && fullName != null;
                }
                return false; // Invalid login credentials
            }

            @Override
            protected void succeeded() {
                if (getValue()) {
                    // Login successful, set up the user session
                    UserSession session = UserSession.getInstance(userId, email, fullName);

                    statusMessage.setText("Login successful!");

                    try {
                        // Load the MusicApplication FXML
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/musicresources/music-view.fxml"));
                        Parent root = loader.load();

                        // Access MusicController from the FXMLLoader
                        MusicController musicController = loader.getController();

                        // Set the profile image URL in MusicController
                        musicController.displayUserProfileImage(profileImageUrl);

                        // Launch the MusicApplication
                        Stage musicStage = new Stage();
                        musicStage.setScene(new Scene(root));
                        musicStage.show();

                        // Close the login stage
                        Stage loginStage = (Stage) usernameField.getScene().getWindow();
                        loginStage.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                        statusMessage.setText("Failed to load Music Application.");
                    }
                } else {
                    statusMessage.setText("Invalid username or password.");
                    System.out.println("Invalid login attempt for email: " + email);
                }
            }

            @Override
            protected void failed() {
                statusMessage.setText("An error occurred during login.");
                getException().printStackTrace();
            }
        };

        // Run the task on a background thread
        new Thread(loginTask).start();
    }



    // Method to open the registration screen
    public void handleRegister() throws Exception {
        register screen = new register();
        screen.start(new Stage());
    }

    public void handleForgotPassword(ActionEvent event) {
        try {
            // Load the forgotPass.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/musicresources/forgotPass.fxml")); // Replace with your actual FXML file path
            Parent root = loader.load();

            // Create a new stage for the forgot password screen
            Stage stage = new Stage();
            stage.setTitle("Forgot Password");
            stage.setScene(new Scene(root));
            stage.show();

            // Optionally close the current window
             ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();

        } catch (IOException e) {
            e.printStackTrace(); // Log the error
        }
    }
}
