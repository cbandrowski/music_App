package com.example.musicapp;

import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class DashBoardController {


//    private int currentImageIndex = 0;

    @FXML
    private ImageView imageView1;
    @FXML
    private ImageView imageView2;
    @FXML
    private ImageView imageView3;
    @FXML
    private AnchorPane aboutPane;
    @FXML
    private AnchorPane welcomeText;
    private int currentImageIndex = 0;
    @FXML
    private ImageView profileImageView;

    private String profileImageUrl;

    private final String[] images = {
            getClass().getResource("/com/example/musicresources/com/example/images/dasahBoardPic.jpg").toString(),
            getClass().getResource("/com/example/musicresources/com/example/images/dashBoardPic2.jpg").toString(),
            getClass().getResource("/com/example/musicresources/com/example/images/dashBoardPic3.jpg").toString()
    };

    public void initialize() {
        // Set the initial images
        imageView1.setImage(new Image(images[0])); // Load image for imageView1
        imageView2.setImage(new Image(images[1])); // Load image for imageView2
        imageView3.setImage(new Image(images[2])); // Load image for imageView3


        // Start the fade animation
        playFadeAnimation();
    }



    private void playFadeAnimation() {
        // Create FadeTransitions for each image
        FadeTransition fadeOutImageView1 = createFadeTransition(imageView1, 1.0, 0.0);
        FadeTransition fadeInImageView2 = createFadeTransition(imageView2, 0.0, 1.0);
        FadeTransition fadeInImageView3 = createFadeTransition(imageView3, 0.0, 1.0);

        // Create a sequential transition for smooth animation order
        SequentialTransition sequentialTransition = new SequentialTransition();

        // Add the fade transitions to the sequential transition
        sequentialTransition.getChildren().addAll(fadeOutImageView1, fadeInImageView2, fadeInImageView3);

        // Set an event for when the animation finishes
        sequentialTransition.setOnFinished(e -> {
            // Alternate images for the next cycle
            currentImageIndex = (currentImageIndex + 1) % images.length;
            Image nextImage = new Image(images[currentImageIndex]);

            // Check which image view has an opacity of 0 and replace its image
            if (imageView1.getOpacity() == 0) {
                imageView1.setImage(nextImage);
            } else if (imageView2.getOpacity() == 0) {
                imageView2.setImage(nextImage);
            } else {
                imageView3.setImage(nextImage);
            }

            // Restart the animation for the next cycle
            playFadeAnimation();
        });

        // Play the transition animation
        sequentialTransition.play();
    }

    private FadeTransition createFadeTransition(ImageView imageView, double from, double to) {
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(3), imageView); // Use 3 seconds for smooth transition
        fadeTransition.setFromValue(from);
        fadeTransition.setToValue(to);
        return fadeTransition;
    }

    public void handleHome_btn(ActionEvent event) {
        try {
            // Load the music-view.fxml to show the music application
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/musicresources/music-view.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/musicresources/dashBoardSetting.fxml"));
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