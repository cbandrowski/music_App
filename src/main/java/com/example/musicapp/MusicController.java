package com.example.musicapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;

import java.net.URL;

public class MusicController {
    @FXML
    public ListView<String> currentPlaylist;
    @FXML
    private ImageView albumArt, profileImage;
    @FXML
    private Label songTitle, usernameLabel, firstNameLabel, lastNameLabel, addressLabel;
    @FXML
    private Button playButton, pauseButton, nextButton, previousButton, themeToggleButton, paymentButton;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private StackPane overlayPane;
    @FXML
    private BorderPane profilePane;

    @FXML
    private Text nameId;

    private boolean isDarkMode = false;
    private MediaPlayer mediaPlayer;

    // Variables for tracking drag offsets
    private double xOffset = 0;
    private double yOffset = 0;

    // Make the profilePane draggable by adjusting translateX and translateY
    private void makeProfilePaneDraggable() {
        profilePane.setOnMousePressed(event -> {
            // Capture the initial offset when mouse is pressed
            xOffset = event.getSceneX() - profilePane.getTranslateX();
            yOffset = event.getSceneY() - profilePane.getTranslateY();
        });

        profilePane.setOnMouseDragged(event -> {
            // Adjust translateX and translateY based on current mouse position and offset
            profilePane.setTranslateX(event.getSceneX() - xOffset);
            profilePane.setTranslateY(event.getSceneY() - yOffset);
        });
    }


    public void initialize() {
        initializeMediaPlayer();
        makeProfilePaneDraggable();
    }

    //displays name
    public void setUserName(String userName) {
        System.out.println("Setting user name: " + userName); // Debug line
        if (nameId != null) {
            nameId.setText(userName);
        } else {
            System.out.println("nameId is null!"); // Debug line
        }
    }


    public void initializeMediaPlayer() {
        URL resource = getClass().getResource("/DemoSong.mp3");
        URL imageUrl = getClass().getResource("/DefaultAlbumCoverArt.jpg");
        if (imageUrl != null) {
            albumArt.setImage(new Image(imageUrl.toExternalForm()));
        } else {
            System.out.println("Image file not found at /DefaultAlbumCoverArt.jpg");
        }

        if (resource != null) {
            Media media = new Media(resource.toString());
            mediaPlayer = new MediaPlayer(media);
        } else {
            System.out.println("MP3 file not found!");
        }
    }

    @FXML
    public void handleProfileAction(ActionEvent actionEvent) {
        overlayPane.setVisible(true);
        URL profileImageUrl = getClass().getResource("/ProfileImage.jpg");
        if (profileImageUrl != null) {
            profileImage.setImage(new Image(profileImageUrl.toExternalForm()));
        }
        usernameLabel.setText("johndoe123");
        firstNameLabel.setText("John");
        lastNameLabel.setText("Doe");
        addressLabel.setText("123 Main St, Anytown, USA");
    }

    @FXML
    public void closeProfilePane() {
        overlayPane.setVisible(false);
    }


    public void onPlayButtonClick() {
        if (mediaPlayer != null) {
            System.out.println("Playing audio...");
            mediaPlayer.play();
        }
    }

    public void onPauseButtonClick() {
        if (mediaPlayer != null) {
            System.out.println("Pausing audio...");
            mediaPlayer.pause();
        }
    }

    @FXML
    protected void onNextButtonClick() {
        songTitle.setText("Next Song");
    }

    @FXML
    protected void onPreviousButtonClick() {
        songTitle.setText("Previous Song");
    }

    //toggle for light and dark themes
    @FXML
    protected void onThemeToggleButtonClick() {
        // Get the current scene
        Scene scene = rootPane.getScene();

        // Check if the current theme is dark or light
        if (isDarkMode) {
            System.out.println("LightMode Active");
            // Remove the dark theme and add the light theme
            scene.getStylesheets().clear(); // Clear existing stylesheets
            scene.getStylesheets().add(getClass().getResource("/com/example/musicresources/light-theme.css").toExternalForm()); // Add light theme
        } else {
            // Remove the light theme and add the dark theme
            System.out.println("DarkMode Active");
            scene.getStylesheets().clear(); // Clear existing stylesheets
            scene.getStylesheets().add(getClass().getResource("/com/example/musicresources/dark-theme.css").toExternalForm()); // Add dark theme
        }

        // Toggle the theme mode flag
        isDarkMode = !isDarkMode;
    }



    public void handlePreferencesAction(ActionEvent actionEvent) {
    }

    public void handleHelpAction(ActionEvent actionEvent) {
    }

    public void handleLogoutAction(ActionEvent actionEvent) {
    }

    public void handlePaymentAction(ActionEvent actionEvent) {
    }
}