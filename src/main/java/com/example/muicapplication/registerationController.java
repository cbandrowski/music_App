package com.example.muicapplication;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class registerationController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Text statusMessage;

    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Basic validation checks
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            statusMessage.setText("All fields are required.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            statusMessage.setText("Passwords do not match.");
            return;
        }

        // TODO: Add logic to save user information (e.g., to a database)

        // Show success message
        statusMessage.setText("Registration successful!");

        // Clear fields after successful registration (optional)
        usernameField.clear();
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
    }
}
