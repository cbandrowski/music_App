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

    private int currentImageIndex = 0;

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
            // Load the MusicApplication FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/musicresources/music-view.fxml"));
            Parent root = loader.load();

            // Access MusicController from the FXMLLoader
            MusicController musicController = loader.getController();

            // Set the profile image URL in MusicController
//            musicController.displayUserProfileImage(profileImageUrl);

            // Launch the MusicApplication
            Stage musicStage = new Stage();
            musicStage.setScene(new Scene(root));
            musicStage.show();

            // Close the login stage
//            Stage loginStage = (Stage) usernameField.getScene().getWindow();
//            loginStage.close();
        } catch (Exception e) {
            e.printStackTrace();
//            statusMessage.setText("Failed to load Music Application.");
        }
    }
}