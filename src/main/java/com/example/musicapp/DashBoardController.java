package com.example.musicapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class DashBoardController {

    @FXML
    private AnchorPane aboutPane;
    @FXML
    private AnchorPane welcomeText;
    @FXML
    private ImageView tune1;
    @FXML
    private ImageView tune2;
    @FXML
    private ImageView tune3;
    @FXML
    private ImageView profileImageView;
    private String profileImageUrl;

    public void initialize() {

    }

    public void handleHome_btn(ActionEvent event) {
        try {
            // Load the music-view.fxml to show the music application
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/musicresources/view/music-view.fxml"));
            Parent root = loader.load();

            // Access the MusicController (assuming you have one)
            MusicController musicController = loader.getController();

            // Pass the profile image URL to the MusicController (only here, not in the dashboard)
            musicController.displayUserProfileImage(profileImageUrl);

            // Launch the MusicApplication
            Stage musicStage = new Stage();
            musicStage.setScene(new Scene(root));
            musicStage.show();

            // Close the current dashboard stage
            Stage dashboardStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            dashboardStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public void handleAbout_btn(ActionEvent event) {
        boolean isAboutPaneVisible = aboutPane.isVisible();

        // Toggle visibility
        aboutPane.setVisible(!isAboutPaneVisible);
        aboutPane.setManaged(!isAboutPaneVisible);

        welcomeText.setVisible(isAboutPaneVisible);
        welcomeText.setManaged(isAboutPaneVisible);
    }

    public void handleSettings(ActionEvent event) {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/musicresources/view/dashBoardSetting.fxml"));
            Parent root = loader.load();

            // Create a new scene with fixed dimensions
            Scene scene = new Scene(root, 556, 558); // Specify the width and height

            // Get the current stage from the event source (the button)
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            // Set the new scene
            stage.setScene(scene);

            // Fix the stage's size and disable resizing
            stage.setResizable(false);
            stage.setWidth(723);  // Fix the width
            stage.setHeight(543); // Fix the height

            stage.show();
        } catch (Exception e) {
            e.printStackTrace(); // Debugging any potential issues
        }
    }

    // This method is called from the login controller to set the profile image URL
    public void setUserProfileImage(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
        // Load the image and set it to the profileImageView
        if (profileImageUrl != null) {
            profileImageView.setImage(new Image(profileImageUrl));
        }
    }

}