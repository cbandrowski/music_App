package login;

import datab.DataBase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;

public class forgotPassController {

    // Injected fields from FXML
    @FXML
    private TextField emailField;  // The field for the user's email
    @FXML
    private PasswordField newPasswordField;  // The field for the new password
    @FXML
    private Label statusLabel;  // Label to display status messages

    // Instantiate the DataBase class
    private DataBase database = new DataBase();

    // Initialize method to check if FXML elements are properly injected
    @FXML
    public void initialize() {
        // Make sure the FXML fields are injected correctly
        assert emailField != null : "emailField was not injected!";
        assert newPasswordField != null : "newPasswordField was not injected!";
        assert statusLabel != null : "statusLabel was not injected!";
    }

    // Method to handle cancel action (you can implement the functionality later if needed)
    public void handleCancelAction(ActionEvent event) {
        // Add functionality for cancel action (if needed)
    }

    // Method to handle the password reset action
    public void handleResetPasswordAction(ActionEvent event) {
        // Get the text values from the input fields
        String email = emailField.getText();
        String newPassword = newPasswordField.getText();

        // Check if both fields are filled
        if (email.isEmpty() || newPassword.isEmpty()) {
            statusLabel.setText("Please fill in both fields.");
            return;  // Exit the method if any field is empty
        }

        // Call the changePassword method to update the password in the database
        boolean isUpdated = database.changePassword(email, newPassword);

        // Display the appropriate status message based on the result
        if (isUpdated) {
            statusLabel.setText("Password updated successfully!");
        } else {
            statusLabel.setText("Failed to update password. Check the email.");
        }
    }
}
