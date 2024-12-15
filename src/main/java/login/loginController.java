package login;

import com.example.musicapp.DashBoardController;
import com.example.musicapp.MusicController;
import datab.DataBase;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import service.UserSession;

import java.io.IOException;

public class loginController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Text statusMessage;

    // Instance of the Database class to check credentials
    private DataBase database = new DataBase();
    MusicController mC = new MusicController();

public void handleLogin() {
    String email = usernameField.getText().trim();
    String password = passwordField.getText().trim();

    Task<Boolean> loginTask = new Task<>() {
        private int userId;
        private String fullName;
        private String profileImageUrl;

        @Override
        protected Boolean call() throws Exception {
            database.connectToDatabase();
            if (database.loginUser(email, password)) {
                userId = database.getUserId(email);
                fullName = database.getUserFullName(email);
                profileImageUrl = database.getUserProfileImageUrl(userId);
                return userId > 0 && fullName != null;
            }
            return false;
        }

        protected void succeeded() {
            // Check if the email or password fields are empty
            if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
                // If either field is empty, show a message asking the user to enter both
                statusMessage.setText("Please enter both email and password.");
            } else {
                // Proceed with the login logic if both fields are filled
                if (getValue()) {
                    UserSession session = UserSession.getInstance(userId, email, fullName);
                    statusMessage.setText("Login successful!");

                    try {
                        // Load the dashboard FXML
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/musicresources/view/dashBoard.fxml"));
                        Parent root = loader.load();

                        // Access the DashboardController
                        DashBoardController dashboardController = loader.getController();

                        // Pass the profile image URL to the dashboard controller (remove if no image is needed on dashboard)
                        dashboardController.setUserProfileImage(profileImageUrl);

                        // Create a new stage for the dashboard
                        Stage dashboardStage = new Stage();
                        dashboardStage.setScene(new Scene(root));

                        dashboardStage.setHeight(794);
                        dashboardStage.setResizable(true);

                        // Show the dashboard window
                        dashboardStage.show();

                        // Close the login stage
                        Stage loginStage = (Stage) usernameField.getScene().getWindow();
                        loginStage.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        statusMessage.setText("Failed to load Dashboard.");
                    }
                } else {
                    statusMessage.setText("Invalid username or password.");
                }
            }
        }


        @Override
        protected void failed() {
            statusMessage.setText("An error occurred during login.");
            getException().printStackTrace();
        }
    };

    new Thread(loginTask).start();
}


    // Method to open the registration screen
    public void handleRegister(ActionEvent event) throws Exception {
        register screen = new register();
        screen.start(new Stage());
        // Close the login window
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    public void handleForgotPassword(ActionEvent event) {
        try {
            // Load the forgotPass.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/musicresources/view/forgotPass.fxml")); // Replace with your actual FXML file path
            Parent root = loader.load();

            // Create a new stage for the forgot password screen
            Stage stage = new Stage();
            stage.setTitle("Forgot Password");
            stage.setScene(new Scene(root));
            stage.show();

            // Optionally close the current window
             ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();

        } catch (IOException e) {
            e.printStackTrace(); // Log the error
        }
    }
}
/*
import com.azure.storage.blob.models.BlobItem;
import datab.DataBase;
import datab.MusicDB;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
@@ -30,6 +31,7 @@
        import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import model.Metadata;
import model.MetadataExtractor;
import service.UserSession;
@@ -52,6 +54,20 @@ public class MusicController {
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
    @FXML
    private StackPane searchPane;

    @FXML
    public TableView resultsTable;
    @FXML
    private ListView<String> playlistListView; // Displays playlist names

    @@ -156,6 +172,7 @@ public void initialize() {
        userLibDurationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));
        addDoubleClickToPlay();
        loadUserPlaylists();
        initializeSearchTable();

        // Set default header label to "My Library"
        headerLabel.setText("My Playlists");
        @@ -195,6 +212,113 @@ protected void updateItem(String item, boolean empty) {
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
        private void performSearch() {
            String query = searchBar.getText().trim();
            if (query.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Invalid Search");
                alert.setContentText("Please enter a search query.");
                alert.showAndWait();
                return;
            }

            ObservableList<Metadata> results = searchBlobStorage(query); // Example search function
            resultsTable.setItems(results);

            if (results.isEmpty()) {
                resultsTable.setPlaceholder(new Label("No results found."));
            }

            // Show the table with a fade-in effect
            showTableWithFade();
        }

        private void showTableWithFade() {
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

            @@ -558,7 +682,6 @@ protected void updateItem(Void item, boolean empty) {
            });
        }


        private void loadUserPlaylists() {
            int userId = UserSession.getInstance().getUserId();
            ObservableList<String> playlists = database.getUserPlaylists(userId);
            @@ -1115,4 +1238,6 @@ private void handleRefreshUserLibrary(ActionEvent event) {
            }


            public void performSearch(ActionEvent actionEvent) {
            }
        }
        41 changes: 41 additions & 0 deletions 41
        src/main/java/datab/DataBase.java

        Original file line number	Diff line number	Diff line change
        @@ -166,6 +166,47 @@ public boolean isSongInUserLibrary(int userId, String blobName) {
        }
        return false; // Return false if there's an error or no result
    }
    public ObservableList<Metadata> searchSongInUserLibrary(int userId, String songName, String artist, String albumName) {
        String searchQuery = "SELECT title, blob_name, artist, album " +
                "FROM UserSongs WHERE user_id = ?" +
                (songName != null && !songName.isBlank() ? " AND title LIKE ?" : "") +
                (artist != null && !artist.isBlank() ? " AND artist LIKE ?" : "") +
                (albumName != null && !albumName.isBlank() ? " AND album LIKE ?" : "");

        ObservableList<Metadata> results = FXCollections.observableArrayList();

        try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement stmt = connection.prepareStatement(searchQuery)) {

            int paramIndex = 1;
            stmt.setInt(paramIndex++, userId);

            if (songName != null && !songName.isBlank()) {
                stmt.setString(paramIndex++, "%" + songName + "%");
            }
            if (artist != null && !artist.isBlank()) {
                stmt.setString(paramIndex++, "%" + artist + "%");
            }
            if (albumName != null && !albumName.isBlank()) {
                stmt.setString(paramIndex++, "%" + albumName + "%");
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String title = rs.getString("title");
                String blobName = rs.getString("blob_name");
                String songArtist = rs.getString("artist");
                String songAlbum = rs.getString("album");

                results.add(new Metadata(title, blobName, songArtist, "", songAlbum, ""));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }


    public int getUserId(String email) {
        try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
   30 changes: 26 additions & 4 deletions 30
                     src/main/resources/com/example/musicresources/music-view.fxml
                     Copied!

                     Original file line number	Diff line number	Diff line change
                     @@ -1,6 +1,7 @@
                     <?xml version="1.0" encoding="UTF-8"?>

                     <?import java.lang.*?>
                     <?import javafx.collections.*?>
                     <?import javafx.geometry.*?>
                     <?import javafx.scene.control.*?>
                     <?import javafx.scene.image.*?>
                     @@ -23,7 +24,30 @@
                     </styleClass>
                     </MenuBar>

                     <TextArea fx:id="searchBar" layoutX="550" prefHeight="7.0" prefWidth="300" promptText="Enter Song, Artist, Album, or Genre" AnchorPane.topAnchor="10" />

                     <VBox alignment="TOP_CENTER" layoutX="10.0" layoutY="10.0" prefHeight="200" prefWidth="600" spacing="10" AnchorPane.leftAnchor="22.0" AnchorPane.rightAnchor="-22.0" AnchorPane.topAnchor="10.0">
                     <HBox alignment="TOP_CENTER" layoutX="400" prefHeight="126.0" prefWidth="1096.0" spacing="10">
                     <ComboBox fx:id="sourceComboBox" promptText="Select Source">
                     <items>
                     <FXCollections fx:factory="observableArrayList">
                     <String fx:value="UserLibrary" />
                     <String fx:value="BlobStorage" />
                     </FXCollections>
                     </items>
                     </ComboBox>
                     <TextArea fx:id="searchBar" layoutX="150" prefHeight="81.0" prefWidth="500" promptText="Search by song name, artist, or album" />
                     <Button onAction="#performSearch" text="Search" />

                     <Button fx:id="themeToggleButton" onAction="#onThemeToggleButtonClick" text="Toggle Theme" />
                     </HBox>
                     <TableView fx:id="resultsTable" opacity="0" visible="false">
                     <columns>
                     <TableColumn fx:id="nameResultColumn" text="Song Name" />
                     <TableColumn fx:id="artistResultColumn" text="Artist" />
                     <TableColumn fx:id="albumResultColumn" text="Album" />
                     </columns>
                     </TableView>
                     </VBox>

                     <TableView fx:id="metadataTable" layoutX="674.0" layoutY="110.0" prefHeight="423.0" prefWidth="514.0">
                     <columns>
                     @@ -37,7 +61,7 @@
                     </columns>
                     </TableView>
                     <!-- Label to indicate the current view (initially hidden) -->
                <Label fx:id="headerLabel" layoutX="200.0" layoutY="20.0" text="My Playlists" style="-fx-font-size: 16px;" visible="false" />
                <Label fx:id="headerLabel" layoutX="200.0" layoutY="20.0" style="-fx-font-size: 16px;" text="My Playlists" visible="false" />

                <!-- ListView for displaying playlists (initially hidden) -->
                <ListView fx:id="playlistListView" layoutX="200.0" layoutY="60.0" prefHeight="400.0" prefWidth="200.0" visible="false" />
        @@ -66,8 +90,6 @@
            <Button fx:id="shuffleButton" onAction="#onShuffleCLick" text="Shuffle" />
                </HBox>

                <Button fx:id="themeToggleButton" layoutX="870.0" layoutY="48.0" onAction="#onThemeToggleButtonClick" text="Toggle Theme" />

                <StackPane fx:id="overlayPane" style="-fx-background-color: rgba(0, 0, 0, 0.5);" visible="false">
                <BorderPane fx:id="profilePane" prefHeight="400" prefWidth="300" style="-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1;" translateX="0" translateY="0">
                <top>
                */
