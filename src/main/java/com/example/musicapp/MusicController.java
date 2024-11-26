package com.example.musicapp;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.models.BlobItem;
import datab.DataBase;
import datab.MusicDB;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
import javafx.util.Callback;
import javafx.util.Duration;
import model.Metadata;
import model.MetadataExtractor;
import service.UserSession;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

public class MusicController {
    private static final String CONNECTION_STRING = "DefaultEndpointsProtocol=https;AccountName=musicappdb;AccountKey=/TxkG8DnJ6NGWCEnv/82FiqesEi04JLZ/s6qd5Ox78qGJuxETnxCrpVs6C42jsmTzNUQ65iZ5cLn+AStfJBFbw==;EndpointSuffix=core.windows.net";
    private static final String CONTAINER_NAME = "media-files";
    public Button refreshUserLibButton;
    public ProgressBar downloadProgressBar = new ProgressBar(0);;
    public TextArea searchBar;
    @FXML
    public TableColumn nameResultColumn;
    @FXML
    public TableColumn artistResultColumn;
    @FXML
    public TableColumn albumResultColumn;
    @FXML
    public ComboBox<String> sourceComboBox;
    public Button searchBtn;
    @FXML
    private StackPane searchPane;

    @FXML
    public TableView resultsTable;
    @FXML
    private ListView<String> playlistListView; // Displays playlist names

    @FXML
    private Label headerLabel; // A label above the table to indicate the current view context (e.g., "My Library" or "Playlist: <Name>")

    @FXML
    private TableView<Metadata> userLib;
    private boolean isUserLibrary = true; // Flag to differentiate between library and playlist
    private DataBase database = new DataBase(); // Database instance
    @FXML
    private TableColumn<Metadata, String> userLibSongNameColumn;
    @FXML
    private TableColumn<Metadata, String> userLibArtistColumn;
    @FXML
    private TableColumn<Metadata, String> userLibDurationColumn;
    @FXML
    private TableView<Metadata> metadataTable;
    @FXML
    private TableColumn<Metadata, Void> actionColumn;
    public TableColumn<Metadata, Void> userDownloadColumn;
    @FXML
    private TableColumn<Metadata, String> songNameColumn;

    @FXML
    private TableColumn<Metadata, String> artistColumn;

    @FXML
    private TableColumn<Metadata, String> durationColumn;

    @FXML
    private TableColumn<Metadata, String> albumColumn;

    @FXML
    private TableColumn<Metadata, String> genreColumn;


    private MusicDB musicBlobDB;
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
    private TextArea searchBar; //used to search up songs

    @FXML
    private ListView<String> songListView; //displays the result

    private ObservableList<String> allSongs; // Complete list of songs

    private MusicDB musicDB; // Reference to your MusicDB class
    @FXML
    private Text nameId;
    private String userEmail;


    private boolean isDarkMode = false;
    private MediaPlayer mediaPlayer;

    // Variables for tracking drag offsets
    private double xOffset = 0;
    private double yOffset = 0;
    // Flag to track the visibility state of the playlist view
    private boolean isPlaylistViewVisible = false;
    @FXML
    private ObservableList<Metadata> currentPlaylist = FXCollections.observableArrayList(); // Holds the current playlist or user library
    private int currentIndex = 0; // Tracks the currently playing song


    public void initialize() {

        ///to display search bar
        displaySearchBar();

        // Retrieve user session details
        UserSession session = UserSession.getInstance();

        // Use session details to load user-specific data
        int userId = session.getUserId();
        String email = session.getEmail();
        String fullName = session.getUserName();

// Initially hide the playlist view and header label
        headerLabel.setVisible(false);
        playlistListView.setVisible(false);

        // Display user information
        usernameLabel.setText(email);
        nameId.setText(fullName);
        // Set up the download column
        addOptionsMenuToTable();

        // Validate existing downloads
        validateDownloadedSongs();

        // Load user-specific data (e.g., library, playlists)
        loadUserLibrary(userId);
        makeProfilePaneDraggable();
        musicBlobDB = new MusicDB();
        // Set up columns for user library
        userLibSongNameColumn.setCellValueFactory(new PropertyValueFactory<>("songName"));
        userLibArtistColumn.setCellValueFactory(new PropertyValueFactory<>("artist"));
        userLibDurationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));
        addDoubleClickToPlay();
        loadUserPlaylists();
        initializeSearchTable();

        // Set default header label to "My Library"
        headerLabel.setText("My Playlists");
        // Set up property value factories for other columns
        songNameColumn.setCellValueFactory(new PropertyValueFactory<>("songName"));
        artistColumn.setCellValueFactory(new PropertyValueFactory<>("artist"));
        albumColumn.setCellValueFactory(new PropertyValueFactory<>("album"));
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));

        durationColumn.setCellFactory(new Callback<TableColumn<Metadata, String>, TableCell<Metadata, String>>() {
            @Override
            public TableCell<Metadata, String> call(TableColumn<Metadata, String> param) {
                return new TableCell<Metadata, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            // Convert string to integer and format as "Xm Ys"
                            try {
                                int seconds = Integer.parseInt(item);
                                int minutes = seconds / 60;
                                int remainingSeconds = seconds % 60;
                                setText(minutes + "m " + remainingSeconds + "s");
                            } catch (NumberFormatException e) {
                                setText("Invalid duration"); // Handle invalid data gracefully
                            }
                        }
                    }
                };
            }
        });

        // Load metadata into the table
        loadMetadataIntoTable();
        addButtonToTable();
    }
    public void initializeSearchTable(){
        nameResultColumn.setCellValueFactory(new PropertyValueFactory<>("songName"));
        artistResultColumn.setCellValueFactory(new PropertyValueFactory<>("artist"));
        albumResultColumn.setCellValueFactory(new PropertyValueFactory<>("album"));
        resultsTable.setVisible(false);
        // Add a listener to the searchBar to detect text changes
        searchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) {
                resultsTable.setVisible(false); // Hide the table if the search bar is empty
            }
        });
        rootPane.setOnMouseClicked(event -> {
            // Check if the click occurred outside the table
            if (!resultsTable.isHover()) {
                hideTableWithFade();
            }
        });
        // Set placeholder for empty results
        resultsTable.setPlaceholder(new Label("No results found."));

    }
    @FXML
    private void performSearchDBS() {
        String query = searchBar.getText().trim();
        if (query.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid Search");
            alert.setContentText("Please enter a search query.");
            alert.showAndWait();
            return;
        }

        System.out.println("Performing search with query: " + query); // Debug log

        ObservableList<Metadata> results = searchBlobStorage(query);

        if (results.isEmpty()) {
            System.out.println("No results found for query: " + query); // Debug log
            resultsTable.setPlaceholder(new Label("No results found."));
        } else {
            System.out.println("Results found: " + results.size()); // Debug log
        }

        resultsTable.setItems(results);
        showTableWithFade(); // Make table visible with fade animation
    }


    private void showTableWithFade() {
        resultsTable.toFront();
        resultsTable.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), resultsTable);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    private void hideTableWithFade() {
        if (resultsTable.isVisible()) {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), resultsTable);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(event -> resultsTable.setVisible(false));
            fadeOut.play();
        }
    }
    private ObservableList<Metadata> searchBlobStorage(String query) {
        ObservableList<Metadata> metadataList = FXCollections.observableArrayList();

        // Normalize the query for case-insensitive matching
        String normalizedQuery = query.toLowerCase();

        // Fetch metadata from Blob Storage
        for (BlobItem blobItem : musicBlobDB.getContainerClient().listBlobs()) {
            try {
                // Create a BlobClient for the current blob
                BlobClient blobClient = musicBlobDB.getContainerClient().getBlobClient(blobItem.getName());

                // Use MetadataExtractor to extract metadata from the blob
                Metadata metadata = MetadataExtractor.extractMetadataDB(blobClient, blobItem.getName());

                // Check if the metadata matches the query
                if (matchesQuery(metadata, normalizedQuery)) {
                    metadataList.add(metadata); // Add matching metadata to the list
                }
            } catch (Exception e) {
                // Log the error and continue processing other blobs
                System.err.println("Error fetching metadata for blob: " + blobItem.getName() + " - " + e.getMessage());
            }
        }

        return metadataList;
    }
    private boolean matchesQuery(Metadata metadata, String query) {
        return (metadata.getSongName() != null && metadata.getSongName().toLowerCase().contains(query)) ||
                (metadata.getArtist() != null && metadata.getArtist().toLowerCase().contains(query)) ||
                (metadata.getAlbum() != null && metadata.getAlbum().toLowerCase().contains(query));
    }


    private ObservableList<Metadata> searchUserLibrary(int userId, String query) {
        // Split the query into parts (e.g., "Song Name;Artist;Album Name")
        String[] queryParts = query.split(";");
        String songName = queryParts.length > 0 ? queryParts[0].trim() : null;
        String artist = queryParts.length > 1 ? queryParts[1].trim() : null;
        String albumName = queryParts.length > 2 ? queryParts[2].trim() : null;

        // Delegate the search to the database class
        return database.searchSongInUserLibrary(userId, songName, artist, albumName);
    }


    private void validateDownloadedSongs() {
        ObservableList<Metadata> library = userLib.getItems();

        for (Metadata metadata : library) {
            String filePath = UserSession.getInstance().getLocalStoragePath() + File.separator + metadata.getBlobName();
            File file = new File(filePath);

            // Mark as downloaded if the file exists
            if (file.exists()) {
                metadata.setDownloaded(true);
            } else {
                metadata.setDownloaded(false);
            }
        }

        // Refresh the table to update button states
        userLib.refresh();
    }
    private void loadMetadataIntoTable() {
        ObservableList<Metadata> metadataList = FXCollections.observableArrayList();

        // Fetch metadata from MusicDB
        for (BlobItem blobItem : musicBlobDB.getContainerClient().listBlobs()) {
            try {
                // Create a BlobClient for the current blob
                BlobClient blobClient = musicBlobDB.getContainerClient().getBlobClient(blobItem.getName());

                // Use MetadataExtractor to extract metadata from the blob
                Metadata metadata = MetadataExtractor.extractMetadataDB(blobClient, blobItem.getName());

                // Add metadata to the list
                metadataList.add(metadata);
            } catch (Exception e) {
                // Log the error and continue processing other blobs
                System.err.println("Error fetching metadata for blob: " + blobItem.getName() + " - " + e.getMessage());
            }
        }

        // Set the items for the table
        metadataTable.setItems(metadataList);

        // Show placeholder if the table is empty
        if (metadataList.isEmpty()) {
            metadataTable.setPlaceholder(new Label("No metadata available."));
        }
    }
    private void loadUserLibrary(int userId) {
        // Fetch the user's library from the database
        ObservableList<Metadata> library = FXCollections.observableArrayList(database.getUserLibrary(userId));
        currentIndex = 0;
        // Populate the userLib TableView
        userLib.setItems(library);
        setCurrentPlaylist(library); // Set the user library as the current playlist

        // Show placeholder if the table is empty
        if (library.isEmpty()) {
            userLib.setPlaceholder(new Label("Your library is empty."));
        }
    }
    @FXML
    private void openPlaylist(String playlistName) {
        ObservableList<Metadata> playlistSongs = database.getSongsInPlaylist(UserSession.getInstance().getUserId(), playlistName);
        setCurrentPlaylist(playlistSongs); // Set the selected playlist as the current playlist
    }

    private void addOptionsMenuToTable() {
        userDownloadColumn.setCellFactory(param -> new TableCell<Metadata, Void>() {
            private final MenuButton optionsMenu = new MenuButton("Options");
            private final MenuItem downloadOption = new MenuItem("Download");
            private final MenuItem createPlaylistOption = new MenuItem("Create Playlist");
            private final MenuItem addToPlaylistOption = new MenuItem("Add to Playlist"); // Updated option
            private final MenuItem deleteFromPlaylistOption = new MenuItem("Delete from Playlist"); // New Option

            {
                // Add menu items to the MenuButton
                optionsMenu.getItems().addAll(downloadOption, createPlaylistOption, addToPlaylistOption, deleteFromPlaylistOption);

                // Download Option
                downloadOption.setOnAction(event -> {
                    Metadata metadata = getTableView().getItems().get(getIndex());
                    String blobName = metadata.getBlobName();
                    String localStoragePath = UserSession.getInstance().getLocalStoragePath();

                    if (localStoragePath == null || localStoragePath.isEmpty()) {
                        System.err.println("Local storage path is not set.");
                        return;
                    }

                    String filePath = localStoragePath + File.separator + blobName;

                    Task<Void> downloadTask = new Task<>() {
                        @Override
                        protected Void call() throws Exception {
                            try {
                                updateProgress(0, 1);
                                Platform.runLater(() -> downloadProgressBar.setVisible(true));

                                musicBlobDB.downloadFileWithProgress(blobName, filePath, this::updateProgress);

                                updateProgress(1, 1);
                                return null;
                            } catch (Exception e) {
                                System.err.println("Error during download: " + e.getMessage());
                                throw e;
                            }
                        }
                    };

                    downloadProgressBar.progressProperty().bind(downloadTask.progressProperty());
                    Thread downloadThread = new Thread(downloadTask);
                    downloadThread.setDaemon(true);
                    downloadThread.start();

                    downloadTask.setOnSucceeded(event1 -> {
                        Platform.runLater(() -> {
                            downloadProgressBar.setVisible(false);
                            System.out.println("Download complete: " + blobName);
                        });
                    });

                    downloadTask.setOnFailed(event1 -> {
                        Platform.runLater(() -> {
                            downloadProgressBar.progressProperty().unbind();
                            downloadProgressBar.setProgress(0);
                            downloadProgressBar.setVisible(false);
                            System.err.println("Download failed for: " + blobName);
                        });
                    });
                });

                createPlaylistOption.setOnAction(event -> {
                    Metadata metadata = getTableView().getItems().get(getIndex());

                    // Prompt user to enter playlist name
                    TextInputDialog playlistNameDialog = new TextInputDialog();
                    playlistNameDialog.setTitle("Create New Playlist");
                    playlistNameDialog.setHeaderText("Create a new playlist");
                    playlistNameDialog.setContentText("Enter the playlist name:");

                    Optional<String> result = playlistNameDialog.showAndWait();
                    result.ifPresent(playlistName -> {
                        if (playlistName.isBlank()) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText("Invalid Playlist Name");
                            alert.setContentText("The playlist name cannot be empty.");
                            alert.showAndWait();
                            return;
                        }

                        // Get the current user ID
                        int userId = UserSession.getInstance().getUserId();

                        // Create a new playlist in the database
                        boolean created = database.createPlaylist(userId, playlistName);
                        if (created) {
                            // Add the current song to the newly created playlist
                            boolean added = database.addSongToPlaylist(metadata, playlistName, userId);
                            if (added) {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Playlist Created");
                                alert.setHeaderText("Playlist Created Successfully");
                                alert.setContentText("Playlist '" + playlistName + "' created and '" +
                                        metadata.getSongName() + "' added.");
                                alert.showAndWait();
                            } else {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Error");
                                alert.setHeaderText("Song Addition Failed");
                                alert.setContentText("The song could not be added to the playlist.");
                                alert.showAndWait();
                            }
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText("Playlist Creation Failed");
                            alert.setContentText("The playlist could not be created.");
                            alert.showAndWait();
                        }
                    });
                });


                // Add to Existing Playlist Option
                addToPlaylistOption.setOnAction(event -> {
                    Metadata metadata = getTableView().getItems().get(getIndex());
                    int userId = UserSession.getInstance().getUserId();

                    // Fetch user's existing playlists
                    ObservableList<String> playlists = database.getUserPlaylists(userId);

                    // Create a ListView to display playlists
                    ListView<String> playlistListView = new ListView<>(playlists);
                    playlistListView.setPrefHeight(200);

                    // Dialog to select a playlist
                    Dialog<String> dialog = new Dialog<>();
                    dialog.setTitle("Add to Playlist");
                    dialog.setHeaderText("Select a playlist to add the song:");

                    ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
                    dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

                    // Set ListView as dialog content
                    dialog.getDialogPane().setContent(playlistListView);

                    // Result converter
                    dialog.setResultConverter(dialogButton -> {
                        if (dialogButton == addButtonType) {
                            return playlistListView.getSelectionModel().getSelectedItem();
                        }
                        return null;
                    });

                    Optional<String> result = dialog.showAndWait();
                    result.ifPresent(playlistName -> {
                        // Add song to the selected playlist
                        boolean added = database.addSongToPlaylist(metadata, playlistName, userId);
                        if (added) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Success");
                            alert.setHeaderText("Song Added");
                            alert.setContentText("'" + metadata.getSongName() + "' has been added to playlist '" + playlistName + "'.");
                            alert.showAndWait();
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText("Song Addition Failed");
                            alert.setContentText("The song could not be added to the playlist.");
                            alert.showAndWait();
                        }
                    });
                });

                // Set up action for the download option
                downloadOption.setOnAction(event -> {
                    Metadata metadata = getTableView().getItems().get(getIndex());
                    String blobName = metadata.getBlobName();
                    String localStoragePath = UserSession.getInstance().getLocalStoragePath();

                    if (localStoragePath == null || localStoragePath.isEmpty()) {
                        System.err.println("Local storage path is not set.");
                        return;
                    }

                    String filePath = localStoragePath + File.separator + blobName;

                    Task<Void> downloadTask = new Task<>() {
                        @Override
                        protected Void call() throws Exception {
                            try {
                                updateProgress(0, 1);
                                Platform.runLater(() -> downloadProgressBar.setVisible(true));

                                musicBlobDB.downloadFileWithProgress(blobName, filePath, this::updateProgress);

                                updateProgress(1, 1);
                                return null;
                            } catch (Exception e) {
                                System.err.println("Error during download: " + e.getMessage());
                                throw e;
                            }
                        }
                    };

                    downloadProgressBar.progressProperty().bind(downloadTask.progressProperty());
                    Thread downloadThread = new Thread(downloadTask);
                    downloadThread.setDaemon(true);
                    downloadThread.start();

                    downloadTask.setOnSucceeded(event1 -> {
                        Platform.runLater(() -> {
                            downloadProgressBar.setVisible(false);
                            System.out.println("Download complete: " + blobName);
                        });
                    });

                    downloadTask.setOnFailed(event1 -> {
                        Platform.runLater(() -> {
                            downloadProgressBar.progressProperty().unbind();
                            downloadProgressBar.setProgress(0);
                            downloadProgressBar.setVisible(false);
                            System.err.println("Download failed for: " + blobName);
                        });
                    });
                });

                // Set up action for the "Delete from Playlist" option
                deleteFromPlaylistOption.setOnAction(event -> {
                    Metadata metadata = getTableView().getItems().get(getIndex());
                    int userId = UserSession.getInstance().getUserId();

                    // Confirm deletion
                    Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmationAlert.setTitle("Delete Song");
                    confirmationAlert.setHeaderText("Are you sure you want to delete this song?");
                    confirmationAlert.setContentText("This action will remove '" + metadata.getSongName() +
                            "' from the current playlist.");

                    Optional<ButtonType> result = confirmationAlert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        // Perform deletion from database
                        boolean deleted = database.deleteSongFromPlaylist(userId, metadata.getBlobName());
                        System.out.println(deleted);
                        System.out.println(userId);
                        System.out.println(metadata.getSongName());
                        System.out.println(metadata.getBlobName());
                        if (deleted) {
                            // Remove the song from the current TableView
                            getTableView().getItems().remove(metadata);

                            // Refresh the TableView
                            getTableView().refresh();

                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Song Deleted");
                            alert.setHeaderText("Song Removed Successfully");
                            alert.setContentText("The song '" + metadata.getSongName() + "' was removed from the playlist.");
                            alert.showAndWait();
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText("Deletion Failed");
                            alert.setContentText("The song could not be removed from the playlist.");
                            alert.showAndWait();
                        }
                    }
                });
            }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                setGraphic(null); // Hide MenuButton for empty or invalid rows
                return;
            }

            Metadata metadata = getTableView().getItems().get(getIndex());
            int userId = UserSession.getInstance().getUserId();

            // Check if the item should display the MenuButton
            if (metadata == null || !isSongInLibrary(userId, metadata.getBlobName())) {
                setGraphic(null); // Hide the MenuButton if the song is not in the library
            } else {
                setGraphic(optionsMenu); // Show the MenuButton for valid rows

                // Dynamically enable/disable the "Download" option
                String blobName = metadata.getBlobName();
                String localStoragePath = UserSession.getInstance().getLocalStoragePath();

                if (localStoragePath != null && !localStoragePath.isEmpty()) {
                    File file = new File(localStoragePath + File.separator + blobName);
                    downloadOption.setDisable(file.exists()); // Disable if the file already exists
                } else {
                    downloadOption.setDisable(true); // Disable if path is invalid
                }
            }
        }
    });
}

    private void loadUserPlaylists() {
        int userId = UserSession.getInstance().getUserId();
        ObservableList<String> playlists = database.getUserPlaylists(userId);

        // Populate the ListView with playlist names
        playlistListView.setItems(playlists);

        // Add double-click listener to load the selected playlist into the TableView
        playlistListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !playlistListView.getSelectionModel().isEmpty()) {
                String selectedPlaylist = playlistListView.getSelectionModel().getSelectedItem();
                loadPlaylistIntoTable(selectedPlaylist);
            }
        });
    }
    private void loadPlaylistIntoTable(String playlistName) {
        int userId = UserSession.getInstance().getUserId();

        // Fetch songs in the selected playlist
        ObservableList<Metadata> playlistSongs = database.getSongsInPlaylist(userId, playlistName);

        // Update the TableView with the playlist songs
        userLib.setItems(playlistSongs);

        // Update the header label to show the current playlist name
        headerLabel.setText("Playlist: " + playlistName);
    }
    @FXML
    public void handlePlayList_btn(ActionEvent event) {
        if (isPlaylistViewVisible) {
            // Hide the playlist view and reset header label
            headerLabel.setVisible(false);
            playlistListView.setVisible(false);
            isPlaylistViewVisible = false;
        } else {
            // Fetch user playlists from the database
            int userId = UserSession.getInstance().getUserId();
            ObservableList<String> playlists = database.getUserPlaylists(userId);

            // Populate the ListView with playlist names
            playlistListView.setItems(playlists);

            // Show the playlist view and header label
            headerLabel.setText("Playlists");
            headerLabel.setVisible(true);
            playlistListView.setVisible(true);

            // Bring the components to the top of the Z-order
            headerLabel.toFront();
            playlistListView.toFront();

            isPlaylistViewVisible = true;

        }
    }
    // Helper method to check if the song is in the user's library
    private boolean isSongInLibrary(int userId, String blobName) {
        return database.isSongInUserLibrary(userId, blobName);
    }
    private void addButtonToTable() {
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button addButton = new Button("Add to Library");

            {
                addButton.setOnAction(event -> {
                    Metadata metadata = getTableView().getItems().get(getIndex());
                    int userId = UserSession.getInstance().getUserId(); // Fetch user ID from session

                    // Add the song to the user's library

                    database.addToUserSongs(userId, metadata);

                    // Disable the button after adding
                    addButton.setDisable(true);

                    // Refresh the user library
                    loadUserLibrary(userId);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null); // Clear the button if no data
                } else {
                    Metadata metadata = getTableView().getItems().get(getIndex());
                    int userId = UserSession.getInstance().getUserId();
                    // Check if the song is already in the user's library
                    if (database.isSongInUserLibrary(userId, metadata.getBlobName())) {
                        addButton.setDisable(true); // Disable if already in library
                    } else {
                        addButton.setDisable(false); // Enable otherwise
                    }
                    setGraphic(addButton);
                }
            }
        });
    }
    @FXML
    public void onRefresh(ActionEvent actionEvent) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                // Reload metadata into the table
                Platform.runLater(() -> {
                    loadMetadataIntoTable();
                });
                return null;
            }
        };
        new Thread(task).start();
    }
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
    public void initializeMediaPlayer(String filePath, String songName, String artistName) {
        if (filePath == null || filePath.isEmpty()) {
            songTitle.setText("Error: Invalid file path!");
            skipToNextAvailableSong();
            return;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            songTitle.setText("Error: File not found at: " + filePath);
            skipToNextAvailableSong();
            return;
        }

        if (mediaPlayer != null) {
            mediaPlayer.stop(); // Stop the current song if one is already playing
        }

        try {
            Media media = new Media(file.toURI().toString());
            mediaPlayer = new MediaPlayer(media);

            // Prepare media player asynchronously
            mediaPlayer.setOnReady(() -> {
                songTitle.setText(songName + " - " + artistName);
                songTitle.setText("Now playing: " + songName + " by " + artistName);
                mediaPlayer.play();
            });

            // Handle playback errors
            mediaPlayer.setOnError(() -> {
                songTitle.setText("Playback error: " + mediaPlayer.getError().getMessage());
                skipToNextAvailableSong();
            });

        } catch (Exception e) {
            songTitle.setText("Error initializing playback.");
            skipToNextAvailableSong();
        }
    }

    private void addDoubleClickToPlay() {
        userLib.setRowFactory(tv -> {
            TableRow<Metadata> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Metadata metadata = row.getItem();
                    ObservableList<Metadata> sourceList = userLib.getItems();

                    if (isUserLibrary) {
                        // Handle playing songs from the user library
                        currentPlaylist = sourceList.filtered(song -> {
                            String filePath = UserSession.getInstance().getLocalStoragePath() + File.separator + song.getBlobName();
                            File file = new File(filePath);
                            return file.exists(); // Include only downloaded songs
                        });
                    } else {
                        // Handle playing songs from the playlist
                        currentPlaylist = sourceList;
                    }

                    // Update the current index to the clicked song
                    currentIndex = currentPlaylist.indexOf(metadata);
                    playCurrentSong();
                }
            });
            return row;
        });
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
    @FXML
    protected void onPlayButtonClick() {
        if (currentPlaylist.isEmpty()) {
            System.out.println("No songs available to play.");
            return;
        }

        if (mediaPlayer == null || mediaPlayer.getStatus() == MediaPlayer.Status.STOPPED) {
            // Play the current song
            playCurrentSong();
        } else if (mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
            // Resume the song if it's paused
            mediaPlayer.play();
            System.out.println("Resuming playback...");
        } else {
            System.out.println("Already playing...");
        }
    }
    @FXML
    protected void onPauseButtonClick() {
        if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
            System.out.println("Playback paused.");
        } else {
            System.out.println("No song is currently playing.");
        }
    }
    public void setCurrentPlaylist(ObservableList<Metadata> playlist) {
        currentPlaylist = playlist;
        currentIndex = 0; // Reset to the first song
        playCurrentSong(); // Start playing the first song
    }
    @FXML
    protected void onNextButtonClick() {
        if (currentPlaylist.isEmpty()) {
            System.out.println("No songs available in the playlist.");
            return;
        }

        int startIndex = currentIndex; // Save the current index to avoid infinite loops
        do {
            // Move to the next song
            currentIndex = (currentIndex + 1) % currentPlaylist.size();
            Metadata nextSong = currentPlaylist.get(currentIndex);

            if (!isUserLibrary || isSongFileAvailable(nextSong)) {
                playCurrentSong();
                return;
            }
        } while (currentIndex != startIndex); // Stop if we've looped back to the original song

        System.out.println("No valid songs available in the playlist.");
    }
    @FXML
    protected void onPreviousButtonClick() {
        if (currentPlaylist.isEmpty()) {
            System.out.println("No songs available in the playlist.");
            return;
        }

        int startIndex = currentIndex; // Save the current index to avoid infinite loops
        do {
            // Move to the previous song
            currentIndex = (currentIndex - 1 + currentPlaylist.size()) % currentPlaylist.size();
            Metadata previousSong = currentPlaylist.get(currentIndex);

            if (!isUserLibrary || isSongFileAvailable(previousSong)) {
                playCurrentSong();
                return;
            }
        } while (currentIndex != startIndex); // Stop if we've looped back to the original song

        System.out.println("No valid songs available in the playlist.");
    }
    @FXML
    public void onShuffleCLick(ActionEvent actionEvent) {
        if (currentPlaylist.isEmpty()) {
            System.out.println("No songs available to shuffle.");
            return;
        }

        if (isUserLibrary) {
            // Filter out non-downloaded songs in the user library
            currentPlaylist = FXCollections.observableArrayList(
                    currentPlaylist.filtered(this::isSongFileAvailable)
            );
        } else {
            // Make a mutable copy for shuffling
            currentPlaylist = FXCollections.observableArrayList(currentPlaylist);
        }

        // Shuffle the mutable playlist
        FXCollections.shuffle(currentPlaylist);

        // Reset to the first song in the shuffled playlist
        currentIndex = 0;
        playCurrentSong();
    }
    private boolean isSongFileAvailable(Metadata song) {
        String filePath = UserSession.getInstance().getLocalStoragePath() + File.separator + song.getBlobName();
        File file = new File(filePath);
        return file.exists();
    }
    private void playCurrentSong() {
        if (currentPlaylist.isEmpty() || currentIndex < 0 || currentIndex >= currentPlaylist.size()) {
            songTitle.setText("No valid songs to play.");
            return;
        }

        Metadata currentSong = currentPlaylist.get(currentIndex);
        String filePath = UserSession.getInstance().getLocalStoragePath() + File.separator + currentSong.getBlobName();
        File file = new File(filePath);

        if (!file.exists()) {
            songTitle.setText("File not found: " + currentSong.getSongName() + ". Skipping...");
            skipToNextAvailableSong();
            return;
        }

        initializeMediaPlayer(filePath, currentSong.getSongName(), currentSong.getArtist());
    }
    private void skipToNextAvailableSong() {
        int startIndex = currentIndex;
        do {
            currentIndex = (currentIndex + 1) % currentPlaylist.size();
            Metadata nextSong = currentPlaylist.get(currentIndex);

            String filePath = UserSession.getInstance().getLocalStoragePath() + File.separator + nextSong.getBlobName();
            File file = new File(filePath);

            if (!isUserLibrary || file.exists()) {
                playCurrentSong();
                return;
            }
        } while (currentIndex != startIndex);

        System.out.println("No valid songs available to play.");
    }
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
        Stage settingsStage = new Stage();
        settingsStage.setScene(new Scene(root));
        settingsStage.show();

    }
    public void handleLikes_btn(ActionEvent event) {
    }
    @FXML
    public void handleLibrary_btn(ActionEvent event) {
        // Get the user ID from the session
        int userId = UserSession.getInstance().getUserId();

        // Load the user library from the database
        ObservableList<Metadata> library = FXCollections.observableArrayList(database.getUserLibrary(userId));

        // Set the library data into the `userLib` TableView
        userLib.setItems(library);

        // Update the playlist view and header label visibility
        playlistListView.setVisible(false); // Hide the playlist view if it's visible
        headerLabel.setVisible(false); // Hide the header label

        // Optional: Update the UI to indicate that the user is now viewing their library
        headerLabel.setText("User Library");
        headerLabel.setVisible(true); // Show the header label if you want it visible
        System.out.println("Switched to User Library.");
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
    public void handleUpload_btn(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.mp4", "*.wav", "*.aac", "*.flac", "*.ogg", "*.wma", "*.m4a")
        );

        File selectedFile = fileChooser.showOpenDialog(((Node) actionEvent.getSource()).getScene().getWindow());

        if (selectedFile != null) {
            // Progress dialog setup
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

            Task<Void> uploadTask = createUploadTask(selectedFile);
            progressBar.progressProperty().bind(uploadTask.progressProperty());
            statusLabel.textProperty().bind(uploadTask.messageProperty());

            // Handle task success or failure
            uploadTask.setOnSucceeded(event -> {
                progressStage.close();
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "File uploaded successfully!", ButtonType.OK);
                alert.showAndWait();
            });

            uploadTask.setOnFailed(event -> {
                progressStage.close();
                Alert alert = new Alert(Alert.AlertType.ERROR, "File upload failed: " + uploadTask.getException().getMessage(), ButtonType.OK);
                alert.showAndWait();
                uploadTask.getException().printStackTrace(); // Log the exact error
            });

            // Start the upload task
            new Thread(uploadTask).start();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "No file selected!", ButtonType.OK);
            alert.showAndWait();
        }
    }
    private Task<Void> createUploadTask(File file) {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                MusicDB musicDB = new MusicDB();
                BlobClient blobClient = musicDB.getContainerClient().getBlobClient(file.getName());

                // Extract metadata
                Map<String, String> metadata = MetadataExtractor.extractMetadata(file);

                // Upload file to Azure Blob Storage
                long fileSize = Files.size(file.toPath());
                long uploadedBytes = 0;

                updateMessage("Starting upload...");

                try (FileInputStream fileInputStream = new FileInputStream(file);
                     OutputStream blobOutputStream = blobClient.getBlockBlobClient().getBlobOutputStream()) {

                    byte[] buffer = new byte[1024 * 1024];
                    int bytesRead;

                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        blobOutputStream.write(buffer, 0, bytesRead);
                        uploadedBytes += bytesRead;
                        updateProgress(uploadedBytes, fileSize);
                        updateMessage(String.format("Uploading... %.2f%%", (uploadedBytes / (double) fileSize) * 100));
                    }
                }

                // Set metadata after successful upload
                blobClient.setMetadata(metadata);
                System.out.println("Metadata set for blob: " + file.getName());

                updateMessage("Upload complete");
                return null;
            }
        };
    }
    private void saveMetadataToDatabase(Map<String, String> metadata, String blobName, String userId) {
        try (Connection conn = DriverManager.getConnection(DataBase.DB_URL, DataBase.USERNAME, DataBase.PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO UserSongs (user_id, blob_name, title, duration, artist, album, composer, year_recorded) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {

            stmt.setString(1, userId);
            stmt.setString(2, blobName);
            stmt.setString(3, metadata.getOrDefault("title", "Unknown Title"));
            stmt.setString(4, metadata.getOrDefault("duration", "0"));
            stmt.setString(5, metadata.getOrDefault("artist", "Unknown Artist"));
            stmt.setString(6, metadata.getOrDefault("album", "Unknown Album"));
            stmt.setString(7, metadata.getOrDefault("composer", "Unknown Composer"));
            stmt.setString(8, metadata.getOrDefault("year", "0"));

            stmt.executeUpdate();
            System.out.println("Metadata saved to database successfully!");

        } catch (SQLException e) {
            System.err.println("Failed to save metadata to database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    @FXML
    private void handleRefreshUserLibrary(ActionEvent event) {
        // Logic to refresh the user library
        int userId = UserSession.getInstance().getUserId();
        ObservableList<Metadata> library = FXCollections.observableArrayList(database.getUserLibrary(userId));
        userLib.setItems(library);
    }

}