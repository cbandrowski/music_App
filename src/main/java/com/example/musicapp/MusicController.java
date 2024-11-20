package com.example.musicapp;

import com.azure.storage.blob.BlobClient;
import datab.MusicDB;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;

public class MusicController {
    private static final String CONNECTION_STRING = "DefaultEndpointsProtocol=https;AccountName=musicappdb;AccountKey=/TxkG8DnJ6NGWCEnv/82FiqesEi04JLZ/s6qd5Ox78qGJuxETnxCrpVs6C42jsmTzNUQ65iZ5cLn+AStfJBFbw==;EndpointSuffix=core.windows.net";
    private static final String CONTAINER_NAME = "media-files";
     MusicDB store = new MusicDB();
    @FXML
    public ListView<String> currentPlaylist;
    @FXML
    private ImageView albumArt, profileImage;

    @FXML
    private ImageView profilePic; // next to the username should display a pic of the user and should be allowed to change it
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


    public void handlePaymentAction(ActionEvent actionEvent) {
    }

    public void handleSetting_btn(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/musicresources/settings.fxml"));
        Parent root = loader.load();

//        // Get the controller from the FXMLLoader
//        MusicController musicController = loader.getController();
//        musicController.setUserName(fullName); // Set the full name in the controller

        // Launch the MusicApplication
        Stage settingsStage = new Stage();
        settingsStage.setScene(new Scene(root));
        settingsStage.show();

    }

    public void handleLikes_btn(ActionEvent event) {
    }

    public void handlePlayList_btn(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/musicresources/playlist-view.fxml"));
        Parent root = loader.load();

//        // Get the controller from the FXMLLoader
//        MusicController musicController = loader.getController();
//        musicController.setUserName(fullName); // Set the full name in the controller

        // Launch the MusicApplication
        Stage playListStage = new Stage();
        playListStage.setScene(new Scene(root));
        playListStage.show();
    }

    public void handleLibrary_btn(ActionEvent event) {
    }

    public void handleSearch_btn(ActionEvent event) {
    }

    public void handleHome_btn(ActionEvent event) {
    }

    //should bring user back to the loin in screen
    public void handleLogOutAction(ActionEvent event) {

        try {
            // Load the login screen FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/musicresources/login.fxml"));
            Parent loginRoot = loader.load();

            // Get the current stage (window) from the button or any other control in the scene
            Stage currentStage = (Stage) rootPane.getScene().getWindow();

            // Set the new scene (login screen) on the current stage
            currentStage.setScene(new Scene(loginRoot));

            // Optional: Add title for the login screen
            currentStage.setTitle("Login");

        } catch (IOException e) {
            System.err.println("Failed to load login screen: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    public void handleUpload_btn(ActionEvent actionEvent) {
        // Open a file chooser for the user to select a file
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*"),
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.mkv")
        );
        File selectedFile = fileChooser.showOpenDialog(((Node) actionEvent.getSource()).getScene().getWindow());

        if (selectedFile != null) {
            // Create a progress bar and display it
            ProgressBar progressBar = new ProgressBar(0);
            Label statusLabel = new Label("Uploading...");
            VBox vbox = new VBox(statusLabel, progressBar);
            vbox.setSpacing(10);
            vbox.setAlignment(Pos.CENTER);
            Stage progressStage = new Stage();
            progressStage.setScene(new Scene(vbox, 300, 100));
            progressStage.setTitle("Upload Progress");
            progressStage.initModality(Modality.APPLICATION_MODAL);
            progressStage.show();

            // Create the upload task
            Task<Void> uploadTask = createUploadTask(selectedFile);
            progressBar.progressProperty().bind(uploadTask.progressProperty());
            statusLabel.textProperty().bind(uploadTask.messageProperty());

            // Handle task completion
            uploadTask.setOnSucceeded(event -> {
                progressStage.close(); // Close the progress window
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "File uploaded successfully!", ButtonType.OK);
                alert.showAndWait();
            });

            uploadTask.setOnFailed(event -> {
                progressStage.close(); // Close the progress window
                Alert alert = new Alert(Alert.AlertType.ERROR, "File upload failed: " + uploadTask.getException().getMessage(), ButtonType.OK);
                alert.showAndWait();
                System.out.println(uploadTask.getException().getMessage());
            });

            // Start the upload task in a new thread
            new Thread(uploadTask).start();
        }
    }


    private Task<Void> createUploadTask(File file) {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                BlobClient blobClient = store.getContainerClient().getBlobClient(file.getName());
                long fileSize = Files.size(file.toPath());
                long uploadedBytes = 0;

                updateMessage("Starting upload...");

                try (FileInputStream fileInputStream = new FileInputStream(file);
                     OutputStream blobOutputStream = blobClient.getBlockBlobClient().getBlobOutputStream()) {

                    byte[] buffer = new byte[1024 * 1024]; // 1 MB buffer size
                    int bytesRead;

                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        blobOutputStream.write(buffer, 0, bytesRead);
                        uploadedBytes += bytesRead;

                        // Calculate and update progress (range: 0.0 to 1.0)
                        updateProgress(uploadedBytes, fileSize);

                        // Update status message
                        updateMessage(String.format("Uploading... %.2f%%", (uploadedBytes / (double) fileSize) * 100));
                    }
                } catch (Exception e) {
                    // Update the message if an error occurs
                    updateMessage("File upload failed: " + e.getMessage());
                    throw e;
                }

                // Update the message on success
                updateMessage("Upload complete");
                return null;
            }
        };
    }



}