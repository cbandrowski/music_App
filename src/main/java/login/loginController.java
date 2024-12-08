package login;

import com.example.musicapp.DashBoardController;
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

    Task<Boolean> loginTask = new Task<>() {
        private int userId;
        private String fullName;
        private String profileImageUrl;

        @Override
        protected Boolean call() throws Exception {
            database.connectToDatabase();
            if (database.loginUser(email, password)) {
                userId = database.getUserId(email);
                fullName = database.getUserFullName(email);
                profileImageUrl = database.getUserProfileImageUrl(userId);
                return userId > 0 && fullName != null;
            }
            return false;
        }

        protected void succeeded() {
            // Check if the email or password fields are empty
            if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
                // If either field is empty, show a message asking the user to enter both
                statusMessage.setText("Please enter both email and password.");
            } else {
                // Proceed with the login logic if both fields are filled
                if (getValue()) {
                    UserSession session = UserSession.getInstance(userId, email, fullName);
                    statusMessage.setText("Login successful!");

                    try {
                        // Load the dashboard FXML
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/musicresources/dashboard.fxml"));
                        Parent root = loader.load();

                        // Access the DashboardController
                        DashBoardController dashboardController = loader.getController();

                        // Pass the profile image URL to the dashboard controller (remove if no image is needed on dashboard)
                        dashboardController.setUserProfileImage(profileImageUrl);

                        // Create a new stage for the dashboard
                        Stage dashboardStage = new Stage();
                        dashboardStage.setScene(new Scene(root));

                        dashboardStage.setHeight(794);
                        dashboardStage.setResizable(true);

                        // Show the dashboard window
                        dashboardStage.show();

                        // Close the login stage
                        Stage loginStage = (Stage) usernameField.getScene().getWindow();
                        loginStage.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        statusMessage.setText("Failed to load Dashboard.");
                    }
                } else {
                    statusMessage.setText("Invalid username or password.");
                }
            }
        }


        @Override
        protected void failed() {
            statusMessage.setText("An error occurred during login.");
            getException().printStackTrace();
        }
    };

    new Thread(loginTask).start();
}


    // Method to open the registration screen
    public void handleRegister(ActionEvent event) throws Exception {
        register screen = new register();
        screen.start(new Stage());
        // Close the login window
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
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
