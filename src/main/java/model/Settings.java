package model;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class Settings{

    @FXML
    private CheckBox darkModeCheckBox;
    @FXML
    private CheckBox notificationsCheckBox;
    @FXML
    private Button saveSettingsButton;
    @FXML
    private Button cancelSettingsButton;
    @FXML
    private Button changePasswordButton;
    @FXML
    private Label usernameLabel;
    @FXML
    private AnchorPane rootPane;

    private boolean isDarkMode = false;

    public void initialize() {
        setupThemeToggle();
//        loadUserSettings();
        loadSettings();  // Load the settings (without requiring user label)

    }

    private void setupThemeToggle() {
        darkModeCheckBox.setSelected(isDarkMode);
        darkModeCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> toggleTheme(newValue));
    }

    private void toggleTheme(boolean enableDarkMode) {
        rootPane.getScene().getStylesheets().clear();
        if (enableDarkMode) {
            rootPane.getScene().getStylesheets().add(getClass().getResource("/com/example/musicresources/css/dark-theme.css").toExternalForm());
        } else {
            rootPane.getScene().getStylesheets().add(getClass().getResource("/com/example/musicresources/css/light-theme.css").toExternalForm());
        }
        isDarkMode = enableDarkMode;
    }

    private void loadUserSettings() {
        usernameLabel.setText("musicapp");
        notificationsCheckBox.setSelected(true);
    }

    // Load settings (for now, it's just the dark mode toggle)
    private void loadSettings() {
        // You can load other settings here if needed
        notificationsCheckBox.setSelected(true);  // Example of another setting
    }

}
