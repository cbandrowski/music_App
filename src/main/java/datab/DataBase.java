package datab;

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

                // Create users table
                String createUserTable = "CREATE TABLE IF NOT EXISTS users (" +
                        "id INT(10) NOT NULL PRIMARY KEY AUTO_INCREMENT," +
                        "first_name VARCHAR(200) NOT NULL," +
                        "last_name VARCHAR(200) NOT NULL," +
                        "email VARCHAR(200) NOT NULL UNIQUE," +
                        "password VARCHAR(255) NOT NULL)";
                statement.executeUpdate(createUserTable);

                // Check if the 'local_storage_path' column exists
                String checkColumnQuery = "SELECT COUNT(*) AS cnt " +
                        "FROM INFORMATION_SCHEMA.COLUMNS " +
                        "WHERE TABLE_SCHEMA = '" + DB_NAME + "' " +
                        "AND TABLE_NAME = 'users' " +
                        "AND COLUMN_NAME = 'local_storage_path'";

                try (PreparedStatement checkStmt = conn.prepareStatement(checkColumnQuery);
                     ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt("cnt") == 0) {
                        // Column does not exist, add it
                        String addColumnQuery = "ALTER TABLE users ADD COLUMN local_storage_path VARCHAR(255)";
                        statement.executeUpdate(addColumnQuery);
                    }
                }
                // Create playlists table
                String createPlaylistsTable = "CREATE TABLE IF NOT EXISTS playlists (" +
                        "id INT PRIMARY KEY AUTO_INCREMENT," +
                        "name VARCHAR(100) NOT NULL," +
                        "user_id INT NOT NULL," +
                        "FOREIGN KEY (user_id) REFERENCES users(id))";
                statement.executeUpdate(createPlaylistsTable);
                // Create UserSongs table with ON DELETE CASCADE
                String createUserSongsTable = "CREATE TABLE IF NOT EXISTS UserSongs (" +
                        "id INT PRIMARY KEY AUTO_INCREMENT," +
                        "user_id INT NOT NULL," +
                        "blob_name VARCHAR(255) NOT NULL," +
                        "title VARCHAR(200)," +
                        "duration DECIMAL(10, 2)," +
                        "artist VARCHAR(200)," +
                        "album VARCHAR(200)," +
                        "composer VARCHAR(200)," +
                        "year_recorded INT," +
                        "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE)";
                statement.executeUpdate(createUserSongsTable);

// Create PlaylistSongs table with ON DELETE CASCADE
                String createPlaylistSongsTable = "CREATE TABLE IF NOT EXISTS PlaylistSongs (" +
                        "id INT PRIMARY KEY AUTO_INCREMENT," +
                        "playlist_id INT NOT NULL," +
                        "user_song_id INT NOT NULL," +
                        "FOREIGN KEY (playlist_id) REFERENCES playlists(id) ON DELETE CASCADE," +
                        "FOREIGN KEY (user_song_id) REFERENCES UserSongs(id) ON DELETE CASCADE)";
                statement.executeUpdate(createPlaylistSongsTable);


                // Check if any users are already registered
                try (ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM users")) {
                    if (resultSet.next()) {
                        hasRegisteredUsers = resultSet.getInt(1) > 0;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return hasRegisteredUsers;
    }



    // Method for registering a new user
    public boolean registerUser(String firstName, String lastName, String email, String password, String local_Storage_Path) {
        String insertUserSQL = "INSERT INTO users (first_name, last_name, email, password, local_storage_path) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(insertUserSQL)) {

            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            pstmt.setString(4, password);
            pstmt.setString(5, local_Storage_Path);

            pstmt.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
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

    public String getUserFullName(String email) {
        String getUserSQL = "SELECT first_name, last_name FROM users WHERE email = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(getUserSQL)) {

            pstmt.setString(1, email);

            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                return firstName + " " + lastName;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Return null if no user is found
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


}
