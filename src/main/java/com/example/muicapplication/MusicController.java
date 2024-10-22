package com.example.muicapplication;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class MusicController {
    @FXML
    private Label songTitle;
    @FXML
    private Button playButton, pauseButton, nextButton, previousButton, themeToggleButton;
    @FXML
    private AnchorPane rootPane;

    private boolean isDarkMode = false;

    @FXML
    protected void onPlayButtonClick() {
        songTitle.setText("Playing: Song Title");
    }

    @FXML
    protected void onPauseButtonClick() {
        songTitle.setText("Paused");
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
