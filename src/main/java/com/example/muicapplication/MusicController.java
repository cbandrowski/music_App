package com.example.muicapplication;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;
import java.nio.file.Paths;


public class MusicController {
    public ImageView albumArt;
    @FXML
    private Label songTitle;
    @FXML
    private Button playButton, pauseButton, nextButton, previousButton, themeToggleButton;
    @FXML
    private AnchorPane rootPane;

    private boolean isDarkMode = false;
    private MediaPlayer mediaPlayer;


    public void initialize() {
        initializeMediaPlayer();
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

    public void onPlayButtonClick() {
        URL resource = getClass().getResource("/DemoSong.mp3");
        if (mediaPlayer != null) {
            System.out.println("Playing audio...");
            mediaPlayer.play();

            // Extract the file name from the URL
            String fileName = Paths.get(resource.getPath()).getFileName().toString();
            songTitle.setText(fileName);

        } else {
            System.out.println("MediaPlayer is not initialized.");
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

    @FXML
    protected void onThemeToggleButtonClick() {
        if (isDarkMode) {
            rootPane.getStylesheets().clear();
            rootPane.getStylesheets().add(getClass().getResource("light-theme.css").toExternalForm());
        } else {
            rootPane.getStylesheets().clear();
            rootPane.getStylesheets().add(getClass().getResource("dark-theme.css").toExternalForm());
        }
        isDarkMode = !isDarkMode;
    }
}
