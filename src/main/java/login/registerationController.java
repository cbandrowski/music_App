package login;

import datab.DataBase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import service.UserSession;

import java.io.File;
import java.io.IOException;

public class registerationController {

    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Text statusMessage;

    private DataBase database;
    private Runnable onRegistrationSuccess;  // Callback for successful registration
    private String localStoragePath;         // Selected storage location


    @FXML
    public void initialize() {
        // Initialize database and connect
        database = new DataBase();
        database.connectToDatabase();
    }

    // Setter for the success callback
    public void setOnRegistrationSuccess(Runnable onRegistrationSuccess) {
        this.onRegistrationSuccess = onRegistrationSuccess;
    }

    @FXML
    private void handleRegister() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Basic validation checks
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            statusMessage.setText("All fields are required.");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            statusMessage.setText("Please enter a valid email address.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            statusMessage.setText("Passwords do not match.");
            return;
        }

        if (localStoragePath == null || localStoragePath.isEmpty()) {
            statusMessage.setText("Please select a storage location.");
            return;
        }

        // Attempt to register the user
        try {
            boolean registrationSuccess = database.registerUser(firstName, lastName, email, password, localStoragePath);
            if (registrationSuccess) {
                statusMessage.setText("Registration successful!");

                // Redirect to login page after successful registration


            } else {
                // Handle duplicate or other failure scenarios
                statusMessage.setText("Registration failed. Email may already be in use.");
            }
        } catch (Exception e) {
            // Catch unexpected errors
            statusMessage.setText("An unexpected error occurred during registration.");
            System.err.println("Registration error: " + e.getMessage());
        }
    }

    public void handleBackToLogin() {
        try {
            // Correct path to FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/musicresources/view/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) statusMessage.getScene().getWindow(); // Or another reference to your stage
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
        } catch (IOException e) {
            System.err.println("Error loading login page: " + e.getMessage());
        }
    }


    public void pickStorageLocation(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Storage Location");

        Stage stage = (Stage) statusMessage.getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            localStoragePath = selectedDirectory.getAbsolutePath();
            UserSession session = UserSession.getInstance();

            // Save the storage location in the database
            if (database.saveStorageLocation(session.getEmail(), localStoragePath)) {
                session.setLocalStoragePath(localStoragePath); // Update in session
                statusMessage.setText("Storage location saved: " + localStoragePath);
            } else {
                statusMessage.setText("Failed to save storage location.");
            }
        } else {
            statusMessage.setText("No directory selected.");
        }
    }


}


