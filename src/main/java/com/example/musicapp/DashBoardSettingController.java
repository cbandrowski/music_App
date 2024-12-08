package com.example.musicapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import service.UserSession;

public class DashBoardSettingController {
    @FXML
    private Text email;

    @FXML
    private Button send;
    @FXML
    private Text name;

    @FXML
    private TextArea help_text_view;


    @FXML
    public void initialize() {
        // Get the UserSession instance
        UserSession userSession = UserSession.getInstance();

        // Set the user details in the Text fields
        name.setText(userSession.getUserName());
        email.setText(userSession.getEmail());
    }
    public void backToDashBoard_btn(ActionEvent event) {
        try {
            // Load the FXML file for the dashboard
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/musicresources/dashBoard.fxml"));
            Parent root = loader.load();

            // Create a new scene with the original dimensions for the dashboard
            Scene scene = new Scene(root, 1015, 690); // Specify width and height for dashboard

            // Get the current stage from the event source (the button)
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            // Set the new scene and resize the stage
            stage.setScene(scene);
            stage.setWidth(1015); // Restore width for dashboard
            stage.setHeight(794); // Restore height for dashboard
            stage.setResizable(true); // Allow resizing if required for the dashboard

            stage.show();
        } catch (Exception e) {
            e.printStackTrace(); // Debugging any potential issues
        }
    }

    public void handleSupport(ActionEvent event) {
        boolean isVisible = help_text_view.isVisible();
        help_text_view.setVisible(!isVisible);
        send.setVisible(!isVisible);

        // Set placeholder text when making the TextArea visible
        if (!isVisible) {
            help_text_view.setPromptText("Write your comments here, e.g., 'The UI is amazing!' or 'The UI might need improvements.'");
        }
    }

    @FXML
    void handleSend_btn(ActionEvent event) {

        // Retrieve the user's input
        String userComments = help_text_view.getText();

        if (userComments != null && !userComments.trim().isEmpty()) {
            // For now, print the feedback to the console (you can save it to a file or database)
            System.out.println("User Feedback: " + userComments);

            // Provide a confirmation or clear the TextArea
            help_text_view.clear(); // Optional: Clear the input field after submission
            help_text_view.setPromptText("Thank you for your feedback!");

            // Optionally hide the TextArea and Send button after submission
            help_text_view.setVisible(false);
            send.setVisible(false);
        } else {
            // Prompt the user to enter valid feedback
            help_text_view.setPromptText("Please enter your comments before sending.");
        }


    }
}
