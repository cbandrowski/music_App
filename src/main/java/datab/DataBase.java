package datab;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Metadata;
import service.UserSession;

import java.sql.*;

public class DataBase {
    // Database connection constants
    final static String DB_NAME = "MusicApplication";

    //iqbama311server.mysql.database.azure.com
    final static String SQL_SERVER_URL = "jdbc:mysql://bandrowskicsc311server.mysql.database.azure.com";
    public final static String DB_URL = SQL_SERVER_URL + "/" + DB_NAME;
    public final static String USERNAME = "bandrowskiadmin";
    public final static String PASSWORD = "Farmingdale24";

    // Method to connect to the database and initialize tables if not already created
    public boolean connectToDatabase() {
        boolean hasRegisteredUsers = false;

        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false; // Exit if driver is not found
        }

        try {
            // First, connect to MySQL server and create the database if not created
            try (Connection conn = DriverManager.getConnection(SQL_SERVER_URL, USERNAME, PASSWORD);
                 Statement statement = conn.createStatement()) {
                statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            }

            // Connect to the specific database
            try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                 Statement statement = conn.createStatement()) {


                // Check if the 'profile_image_url' column exists
                String checkColumnQuery = "SELECT COUNT(*) AS cnt " +
                        "FROM INFORMATION_SCHEMA.COLUMNS " +
                        "WHERE TABLE_SCHEMA = '" + DB_NAME + "' " +
                        "AND TABLE_NAME = 'users' " +
                        "AND COLUMN_NAME = 'profile_image_url'";

                try (PreparedStatement checkStmt = conn.prepareStatement(checkColumnQuery);
                     ResultSet rs = checkStmt.executeQuery()) {

                    if (rs.next() && rs.getInt("cnt") == 0) {
                        // Column doesn't exist, so we add it
                        String alterTableQuery = "ALTER TABLE users ADD COLUMN profile_image_url VARCHAR(255)";
                        statement.executeUpdate(alterTableQuery);
                    }
                }

                // Create users table
                String createUserTable = "CREATE TABLE IF NOT EXISTS users (" +
                        "id INT(10) NOT NULL PRIMARY KEY AUTO_INCREMENT," +
                        "first_name VARCHAR(200) NOT NULL," +
                        "last_name VARCHAR(200) NOT NULL," +
                        "email VARCHAR(200) NOT NULL UNIQUE," +
                        "password VARCHAR(255) NOT NULL," +
                        "profile_image_url VARCHAR(255))";  // Correctly added the profile_image_url column

                statement.executeUpdate(createUserTable);

                // Check if the 'local_storage_path' column exists and add if not
                String checkLocalStorageColumnQuery = "SELECT COUNT(*) AS cnt " +
                        "FROM INFORMATION_SCHEMA.COLUMNS " +
                        "WHERE TABLE_SCHEMA = '" + DB_NAME + "' " +
                        "AND TABLE_NAME = 'users' " +
                        "AND COLUMN_NAME = 'local_storage_path'";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkLocalStorageColumnQuery);
                     ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt("cnt") == 0) {
                        String addLocalStorageColumnQuery = "ALTER TABLE users ADD COLUMN local_storage_path VARCHAR(255)";
                        statement.executeUpdate(addLocalStorageColumnQuery);
                    }
                }

                String createPlaylistsTable = "CREATE TABLE IF NOT EXISTS playlists (" +
                        "playlist_id INT PRIMARY KEY AUTO_INCREMENT," +
                        "name VARCHAR(100) NOT NULL," +
                        "user_id INT NOT NULL," +
                        "FOREIGN KEY (user_id) REFERENCES users(id))";
                statement.executeUpdate(createPlaylistsTable);

                // Create UserSongs table
                String createUserSongsTable = "CREATE TABLE IF NOT EXISTS UserSongs (" +
                        "id INT PRIMARY KEY AUTO_INCREMENT," +
                        "user_id INT NOT NULL," +
                        "blob_name VARCHAR(255) NOT NULL," +
                        "title VARCHAR(200)," +
                        "duration VARCHAR(200)," +
                        "artist VARCHAR(200)," +
                        "album VARCHAR(200)," +
                        "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE)";
                statement.executeUpdate(createUserSongsTable);

                // Check and add 'genre' column to UserSongs if not exists
                String checkGenreColumnQuery = "SELECT COUNT(*) AS cnt " +
                        "FROM INFORMATION_SCHEMA.COLUMNS " +
                        "WHERE TABLE_SCHEMA = '" + DB_NAME + "' " +
                        "AND TABLE_NAME = 'UserSongs' " +
                        "AND COLUMN_NAME = 'genre'";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkGenreColumnQuery);
                     ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt("cnt") == 0) {
                        String addGenreColumnQuery = "ALTER TABLE UserSongs ADD COLUMN genre VARCHAR(200)";
                        statement.executeUpdate(addGenreColumnQuery);
                    }
                }

                String createPlaylistSongsTable = "CREATE TABLE IF NOT EXISTS PlaylistSongs (" +
                        "id INT PRIMARY KEY AUTO_INCREMENT," +
                        "playlist_id INT NOT NULL," +
                        "user_song_id INT NOT NULL," +
                        "FOREIGN KEY (playlist_id) REFERENCES playlists(playlist_id) ON DELETE CASCADE," +
                        "FOREIGN KEY (user_song_id) REFERENCES UserSongs(id) ON DELETE CASCADE)";
                statement.executeUpdate(createPlaylistSongsTable);

                // Check if any users are already registered
                try (ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM users")) {
                    if (resultSet.next()) {
                        hasRegisteredUsers = resultSet.getInt(1) > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return hasRegisteredUsers;
    }

    // Method to check if a song exists in the UserSongs table
    public boolean checkIfSongExists(String blobName) {
        try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT COUNT(*) FROM UserSongs WHERE blob_name = ?")) {
            stmt.setString(1, blobName);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void addToUserSongs(int userId, Metadata metadata) {
        try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement stmt = connection.prepareStatement(
                     "INSERT INTO UserSongs (user_id, blob_name, title, duration, artist, album) VALUES (?, ?, ?, ?, ?, ?)")) {

            stmt.setInt(1, userId);
            stmt.setString(2, metadata.getBlobName());
            stmt.setString(3, metadata.getSongName());
            stmt.setString(4, metadata.getDuration()); // Pass duration as String
            stmt.setString(5, metadata.getArtist());
            stmt.setString(6, metadata.getAlbum());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isSongInUserLibrary(int userId, String blobName) {
        try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT COUNT(*) FROM UserSongs WHERE user_id = ? AND blob_name = ?")) {

            stmt.setInt(1, userId);
            stmt.setString(2, blobName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Return true if the song exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Return false if there's an error or no result
    }
    public ObservableList<Metadata> searchSongInUserLibrary(int userId, String query) {
        // Construct the SQL query to search multiple fields using OR
        String searchQuery = "SELECT title, blob_name, artist, album " +
                "FROM UserSongs WHERE user_id = ? AND " +
                "(LOWER(title) LIKE ? OR LOWER(artist) LIKE ? OR LOWER(album) LIKE ?)";

        ObservableList<Metadata> results = FXCollections.observableArrayList();

        try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement stmt = connection.prepareStatement(searchQuery)) {

            // Set query parameters
            stmt.setInt(1, userId);
            String normalizedQuery = "%" + query.trim().toLowerCase() + "%"; // Normalize query for case-insensitive matching
            stmt.setString(2, normalizedQuery); // Match against title
            stmt.setString(3, normalizedQuery); // Match against artist
            stmt.setString(4, normalizedQuery); // Match against album

            // Execute the query
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                // Retrieve and map the result set into Metadata objects
                String title = rs.getString("title");
                String blobName = rs.getString("blob_name");
                String songArtist = rs.getString("artist");
                String songAlbum = rs.getString("album");

                results.add(new Metadata(title, blobName, songArtist, "", songAlbum, ""));
            }
        } catch (SQLException e) {
            // Print exception stack trace for debugging
            e.printStackTrace();
        }

        return results; // Return the results list
    }

//    public ObservableList<Metadata> searchSongInUserLibrary(int userId, String songName, String artist, String albumName) {
//        String searchQuery = "SELECT title, blob_name, artist, album " +
//                "FROM UserSongs WHERE user_id = ?" +
//                (songName != null && !songName.isBlank() ? " AND title LIKE ?" : "") +
//                (artist != null && !artist.isBlank() ? " AND artist LIKE ?" : "") +
//                (albumName != null && !albumName.isBlank() ? " AND album LIKE ?" : "");
//
//        ObservableList<Metadata> results = FXCollections.observableArrayList();
//
//        try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
//
//             PreparedStatement stmt = connection.prepareStatement(searchQuery)) {
//
//            int paramIndex = 1;
//            stmt.setInt(paramIndex++, userId);
//
//            if (songName != null && !songName.isBlank()) {
//                stmt.setString(paramIndex++, "%" + songName + "%");
//            }
//            if (artist != null && !artist.isBlank()) {
//                stmt.setString(paramIndex++, "%" + artist + "%");
//            }
//            if (albumName != null && !albumName.isBlank()) {
//                stmt.setString(paramIndex++, "%" + albumName + "%");
//            }
//
//            ResultSet rs = stmt.executeQuery();
//            while (rs.next()) {
//                String title = rs.getString("title");
//                String blobName = rs.getString("blob_name");
//                String songArtist = rs.getString("artist");
//                String songAlbum = rs.getString("album");
//
//                results.add(new Metadata(title, blobName, songArtist, "", songAlbum, ""));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return results;
//    }


    public int getUserId(String email) {
        try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement stmt = connection.prepareStatement("SELECT id FROM Users WHERE email = ?")) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // Return 0 if not found
    }

    public String getUserFullName(String email) {
        try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT CONCAT(first_name, ' ', last_name) AS full_name FROM Users WHERE email = ?")) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("full_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if not found
    }

    public ObservableList<Metadata> getUserLibrary(int userId) {
        ObservableList<Metadata> library = FXCollections.observableArrayList();
        String query = "SELECT title, blob_name, artist, duration, album, genre FROM UserSongs WHERE user_id = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                // Fetch fields from the database
                String title = rs.getString("title");
                String blobName = rs.getString("blob_name");
                String duration = rs.getString("duration");
                String artist = rs.getString("artist");
                String album = rs.getString("album");
                String genre = rs.getString("genre");

                // Debugging: Print each field
                System.out.println("Fetched Record - Title: " + title +
                        ", Blob Name: " + blobName +
                        ", Duration: " + duration +
                        ", Artist: " + artist +
                        ", Album: " + album +
                        ", Genre: " + genre);

                // Add the Metadata object to the list
                library.add(new Metadata(title, blobName, artist, duration, album, genre));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return library;
    }
    public boolean createPlaylist(int userId, String playlistName) {
        String query = "INSERT INTO playlists (name, user_id) VALUES (?, ?)";
        try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, playlistName); // Set the playlist name
            stmt.setInt(2, userId); // Set the user ID
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // Return true if the playlist was created successfully

        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false if there was an error
        }
    }
    public boolean addSongToPlaylist(Metadata metadata, String playlistName, int userId) {
        String getPlaylistIdQuery = "SELECT playlist_id FROM playlists WHERE name = ? AND user_id = ?";
        String getSongIdQuery = "SELECT id FROM UserSongs WHERE blob_name = ?";
        String insertQuery = "INSERT INTO PlaylistSongs (playlist_id, user_song_id, user_id) VALUES (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement playlistStmt = connection.prepareStatement(getPlaylistIdQuery);
             PreparedStatement songStmt = connection.prepareStatement(getSongIdQuery);
             PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {

            // Get the playlist ID
            playlistStmt.setString(1, playlistName);
            playlistStmt.setInt(2, userId);
            ResultSet playlistResult = playlistStmt.executeQuery();
            if (!playlistResult.next()) {
                System.err.println("Playlist not found: " + playlistName);
                return false; // Playlist does not exist for the user
            }
            int playlistId = playlistResult.getInt("playlist_id");

            // Get the song ID
            songStmt.setString(1, metadata.getBlobName());
            ResultSet songResult = songStmt.executeQuery();
            if (!songResult.next()) {
                System.err.println("Song not found: " + metadata.getBlobName());
                return false; // Song does not exist
            }
            int songId = songResult.getInt("id");

            // Add the song to the playlist
            insertStmt.setInt(1, playlistId);
            insertStmt.setInt(2, songId);
            insertStmt.setInt(3, userId); // Include the user ID
            int rowsAffected = insertStmt.executeUpdate();
            return rowsAffected > 0; // Return true if the song was added successfully

        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false if there was an error
        }
    }
    public ObservableList<Metadata> getSongsInPlaylist(int userId, String playlistName) {
        ObservableList<Metadata> songs = FXCollections.observableArrayList();
        String query = "SELECT us.title, us.blob_name, us.duration, us.artist, us.album, us.genre " +
                "FROM UserSongs us " +
                "JOIN PlaylistSongs ps ON us.id = ps.user_song_id " +
                "JOIN playlists p ON ps.playlist_id = p.playlist_id " +
                "WHERE p.user_id = ? AND p.name = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, userId);
            stmt.setString(2, playlistName);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String title = rs.getString("title");
                String blobName = rs.getString("blob_name");
                String duration = rs.getString("duration");
                String artist = rs.getString("artist");
                String album = rs.getString("album");
                String genre = rs.getString("genre");

                songs.add(new Metadata(title, blobName, artist, duration, album, genre));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return songs;
    }

    public ObservableList<String> getUserPlaylists(int userId) {
        ObservableList<String> playlists = FXCollections.observableArrayList();
        String query = "SELECT name FROM playlists WHERE user_id = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                playlists.add(rs.getString("name")); // Add playlist names to the list
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return playlists;
    }
    public boolean deleteSongFromPlaylist(int userId, String blobName) {
        String deleteQuery = "DELETE FROM PlaylistSongs " +
                "WHERE user_id = ? " +
                "AND user_song_id = (SELECT id FROM UserSongs WHERE blob_name = ? LIMIT 1);";

        try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement stmt = connection.prepareStatement(deleteQuery)) {

            stmt.setInt(1, userId); // Set the user ID directly
            stmt.setString(2, blobName); // Set the blob name for the song

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // Return true if deletion was successful
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false if deletion fails
        }
    }
//1. Relationships Summary
//    From your output:
//
//    playlists.user_id → users.id
//    Each playlist belongs to a user.
//    PlaylistSongs.playlist_id → playlists.playlist_id
//    Each entry in PlaylistSongs is associated with a playlist.
//            PlaylistSongs.user_song_id → UserSongs.id
//    Each entry in PlaylistSongs references a specific song from UserSongs.
//    UserSongs.user_id → users.id
//    Each song in UserSongs is associated with a user.

    // Method for registering a new user
    public boolean registerUser(String firstName, String lastName, String email, String password, String local_Storage_Path) {
        String insertUserSQL = "INSERT INTO users (first_name, last_name, email, password, local_storage_path) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

             PreparedStatement pstmt = conn.prepareStatement(insertUserSQL)) {

            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            pstmt.setString(4, password);
            pstmt.setString(5, local_Storage_Path);

            pstmt.executeUpdate();
            return true;

        }
        catch (SQLIntegrityConstraintViolationException e) {
            // Handle duplicate email or other unique constraint violations
            System.err.println("Error: Duplicate email - " + e.getMessage());
            return false;

        } catch (SQLException e) {
            // Log and handle other SQL exceptions
            System.err.println("SQL Error: " + e.getMessage());
            return false;
        }
    }

    // Method for validating login credentials
    public boolean loginUser(String email, String password) {
        String selectUserSQL = "SELECT * FROM users WHERE email = ? AND password = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(selectUserSQL)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);

            ResultSet resultSet = pstmt.executeQuery();
            return resultSet.next(); // Returns true if a user with the given email and password exists

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to change the password for a user
    public boolean changePassword(String email, String newPassword) {
        String updatePasswordSQL = "UPDATE users SET password = ? WHERE email = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(updatePasswordSQL)) {

            // Set the new password and email for the query
            pstmt.setString(1, newPassword);
            pstmt.setString(2, email);

            // Execute the update
            int rowsUpdated = pstmt.executeUpdate();

            // If rowsUpdated is greater than 0, then the password was updated successfully
            return rowsUpdated > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean saveStorageLocation(String email, String storagePath) {
        String updateQuery = "UPDATE users SET local_storage_path = ? WHERE email = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = conn.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, storagePath);
            preparedStatement.setString(2, email);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0; // Return true if the update was successful
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public String getUserStoragePath(String email) {
        try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement stmt = connection.prepareStatement("SELECT local_storage_path FROM Users WHERE email = ?")) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("local_storage_path"); // Fetch the file path
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if not found or an error occurs
    }


    //method to save profile image
    // Method to update the profile image URL in the database
    public void updateProfileImageInDatabase(String imageUrl) {
        // Assuming you have a connection to the database
        int userId = UserSession.getUserId();
        String updateQuery = "UPDATE users SET profile_image_url = ? WHERE id = ?";  // Replace ? with the user's ID

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            stmt.setString(1, imageUrl);  // Set the image URL
            stmt.setInt(2, userId);  // Assuming you have the user's ID in a variable (replace userId with the actual variable)

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Profile image updated successfully!");
            } else {
                System.out.println("Failed to update profile image.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //get the profile image
    public String getUserProfileImageUrl(int userId) {
        String query = "SELECT profile_image_url FROM users WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("profile_image_url");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if no profile image is found
    }


}
