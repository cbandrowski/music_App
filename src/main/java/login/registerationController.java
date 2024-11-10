package login;

import datab.DataBase;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

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

    // Initialize the controller with a new DataBase instance
    public registerationController() {
        database = new DataBase();
        database.connectToDatabase();
    }

    @FXML
    private void handleRegister() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Basic validation checks
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            statusMessage.setText("All fields are required.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            statusMessage.setText("Passwords do not match.");
            return;
        }

        // Attempt to register the user
        boolean registrationSuccess = database.registerUser(firstName, lastName, email, password);
        if (registrationSuccess) {
            statusMessage.setText("Registration successful!");

            // Clear fields after successful registration (optional)
            firstNameField.clear();
            lastNameField.clear();
            emailField.clear();
            passwordField.clear();
            confirmPasswordField.clear();
        } else {
            statusMessage.setText("Registration failed. Email may already be in use.");
        }
    }
}
