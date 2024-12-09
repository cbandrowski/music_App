### Melodify Application


## Table of Contents

1. [Project Overview](#project-overview)  
2. [Project Goals](#project-goals)  
3. [Key Features](#key-features)  
4. [Technologies Used](#technologies-used)  
    - [Frontend](#frontend)  
    - [Backend](#backend)  
    - [Version Control](#version-control)  
5. [Setup Instructions](#setup-instructions)  
    - [Clone the Repository](#clone-the-repository)  
    - [Build the Project](#build-the-project)  
    - [Configure Azure Blob Storage](#configure-azure-blob-storage)  
6. [Folder Structure](#folder-structure)
7. [Regristration](#regristration)
8. [Login](#login)
9. [Forgot Password](#forgot-password)
10. [Dashboard](#dashboard)
11. [Music Controller](#music-controller)
---

## Project Overview

The Melodify Application is a feature-rich music platform offering users a seamless experience to browse, play, and download music. Built using JavaFX for the frontend and a robust backend, the application provides an intuitive interface styled with a clean blue-themed design. The application integrates cloud services for music storage and user data management, ensuring scalability and reliability.

---

## Project Goals

- Develop a user-friendly interface for music browsing and library management.  
- Provide a robust backend to handle music playback and downloads.  
- Integrate Azure Blob Storage for storing and retrieving music files.  
- Use Microsoft Azure for user data, playlists, and metadata storage.  
- Ensure cohesive styling using Material Design 3 for a modern blue-themed interface.  

---

## Key Features

1. Music Library  
   A dedicated page for browsing and managing music.  

2. Download Feature  
   Allows users to download selected tracks to their local devices.  

3. Playlist Management  
   Create, edit, and manage playlists, with options to add or remove tracks.  

4. Dynamic UI Styling  
   Switch between light and dark themes with a toggle button.  

5. Azure Integration  
   - Blob Storage: Uploads and downloads music files with progress tracking.  
   - Database: Handles user playlists, music metadata, and profiles.  

6. Search Functionality  
   Comprehensive search for songs, albums, and artists using database and Azure Blob Storage metadata.  

7. Playback Features  
   Shuffle, play, pause, skip tracks, and view song details.  

---

## Technologies Used

## Frontend  
- JavaFX with FXML for UI development  
- Material Design 3 for styling  
- CSS for theme consistency  

## Backend  
- Java for core application logic  
- Azure Blob Storage for media file management  
- Azure SQL Database for user and metadata storage


#### **1. SQL Database**

The SQL backend is implemented through the `DataBase` class, which uses MySQL to store and manage relational data such as user accounts, playlists, and song metadata.

---

##### **Class: DataBase**

**Key Responsibilities**:
- Connect to the MySQL database hosted on Azure.
- Manage user authentication, registration, and password changes.
- Handle CRUD operations for playlists and song metadata.

**Features**:
1. **Database Initialization**
   - Creates the required database (`MusicApplication`) and tables (`users`, `UserSongs`, `playlists`, and `PlaylistSongs`) if they do not exist.
   - Dynamically checks and adds columns for schema updates.

2. **User Management**
   - Supports registration, login, and password updates.
   - Allows updating and retrieving user profile images and local storage paths.

3. **Song and Playlist Management**
   - Adds, retrieves, and deletes songs from user libraries.
   - Manages playlists and their associated songs.


#### **2. Azure Blob Storage**

The Azure Blob Storage subsystem is implemented through the `MusicDB` class. It enables the application to store, retrieve, and manage media files in Azure's scalable storage service.

---

##### **Class: MusicDB**

**Key Responsibilities**:
- Upload and download music files to and from Azure Blob Storage.
- Retrieve and display metadata from stored blobs.
- Handle progress tracking during file downloads.

**Features**:
1. **File Upload**
   - Allows users to upload music files to Azure Blob Storage with unique blob names.

2. **File Retrieval**
   - Downloads music files locally with progress tracking.
   - Checks for the existence of specific blobs.

3. **Metadata Management**
   - Extracts and lists metadata (e.g., song title, artist, album, duration, genre) for stored files.



#### **Comparison of Subsystems**

| Feature                 | SQL Database                         | Azure Blob Storage               |
|-------------------------|---------------------------------------|----------------------------------|
| **Purpose**             | Stores relational data (e.g., users, playlists) | Stores and retrieves media files |
| **Technology**          | MySQL                                | Azure Blob Storage               |
| **Key Classes**         | `DataBase`                           | `MusicDB`                        |
| **Data Managed**        | User accounts, playlists, metadata   | Music files, metadata            |
| **Strengths**           | Fast relational queries              | Scalable, efficient media storage|

---


## Version Control  
- GitHub for repository management  

---

## Setup Instructions

## Clone the Repository  
```bash
git clone <[repository-url](https://github.com/Mian56/music_Application.git)>
```

## Build the Project  
- Ensure Java 17+ and Maven are installed.  
- Open the project in an IDE such as IntelliJ IDEA or Eclipse.  
- Run the Maven build using:  
  ```bash
  mvn clean install
  ```

## Configure Azure Blob Storage  
- Set up your Azure Blob Storage account.  
- Update the connection string in MusicController:  
  ```java
  private static final String CONNECTION_STRING = "<DefaultEndpointsProtocol=https;AccountName=musicappdb;AccountKey=/TxkG8DnJ6NGWCEnv/82FiqesEi04JLZ/s6qd5Ox78qGJuxETnxCrpVs6C42jsmTzNUQ65iZ5cLn+AStfJBFbw==;EndpointSuffix=core.windows.net>";
  ```  
- Ensure the Container Name is media-files.  

---

## Folder Structure

src/main/java  
- controllers/: JavaFX controllers for managing UI  
- models/: Data models for songs, playlists, and metadata  
- services/: Azure Blob Storage and database interaction logic  

src/main/resources  
- fxml/: FXML files for the UI  
- css/: Stylesheets for themes and design

## Regristration
 
#### **Class Overview**

`RegistrationController` is responsible for managing the user registration workflow, including form validation, interaction with the database, and local storage selection.

---

#### **Key Features**

1. **User Input Fields**
   - First Name
   - Last Name
   - Email
   - Password and Confirm Password

2. **Form Validation**
   - Ensures all fields are filled.
   - Verifies email format using regex.
   - Validates that passwords match.

3. **Storage Location Selection**
   - Uses a `DirectoryChooser` to let users select a local storage path.
   - Saves the selected location in the database.

4. **Database Integration**
   - Checks for duplicate registrations.
   - Saves user data upon successful validation.

5. **Navigation**
   - Provides an option to navigate back to the login page.

---

#### **Methods**

- **`initialize()`**
  - Initializes the database connection for registration.

- **`handleRegister()`**
  - Validates user inputs and attempts to register the user.
  - Displays appropriate status messages for success, duplicate entries, or validation failures.

- **`handleBackToLogin()`**
  - Loads the login page FXML and redirects the user to the login interface.

- **`pickStorageLocation(ActionEvent actionEvent)`**
  - Opens a `DirectoryChooser` to allow users to select a local storage path.
  - Updates the user session and saves the path to the database.

---

#### **Usage**

- Ensure the database is properly set up, and the `DataBase` class is implemented for handling user registration.
- Ensure the `UserSession` singleton is configured to manage user-specific data.

---


#### **Notes**

- This class relies on the `DataBase` and `UserSession` classes for backend functionality.
- Ensure `login.fxml` is present and properly linked for seamless navigation.

## Login


#### **Class Overview**

The `login` class extends `Application` and is the main class responsible for launching the JavaFX application. It handles:

1. Loading the `login.fxml` file for the user interface.
2. Preloading theme-related resources for better performance.
3. Displaying the login screen as the primary stage.

---

#### **Key Features**

1. **FXML Integration**
   - Dynamically loads the `login.fxml` file for the login page.
   - Ensures a clean and responsive user interface.

2. **Preloading Resources**
   - Preloads stylesheets (`dark-theme.css`, `light-theme.css`) in a separate thread for efficiency.
   - Provides feedback in the console about successful or failed resource loading.

3. **Scene Management**
   - Sets the application window dimensions (795x500) for consistency.
   - Titles the application as "Music App Login."

---

#### **Methods**

- **`start(Stage primaryStage)`**
  - Initializes the primary stage with the login interface and sets the application title.
  - Loads the `login.fxml` from the appropriate path.

- **`preloadResources()`**
  - Preloads stylesheets for the application's light and dark themes.
  - Uses multithreading to minimize delays during resource loading.

- **`main(String[] args)`**
  - Launches the JavaFX application.

---

#### **Usage**

1. **Running the Application**
   - Use the `main` method to start the application.
   - Ensure all resource files (e.g., `login.fxml`, `dark-theme.css`, `light-theme.css`) are correctly placed and accessible in the `resources` directory.

2. **Customizing Styles**
   - Add or update the stylesheets (`dark-theme.css`, `light-theme.css`) to reflect your application's desired themes.



#### **Notes**

- Ensure the file paths for the `login.fxml` and stylesheets are correct relative to the `resources` directory.
- Customize the dimensions and title of the application window as required.
- Preloading resources improves runtime performance and is particularly useful for applications with multiple themes or styles.

## Forgot Password

#### **Class Overview**

The `ForgotPassController` handles:
1. Capturing user input for the email address and new password.
2. Validating input fields.
3. Updating the user's password in the database via the `DataBase` class.
4. Navigating back to the login page after a successful reset or on user action.

---

#### **Key Features**

1. **Password Reset**
   - Validates the user's email and new password input.
   - Updates the password in the database securely.

2. **Error Handling**
   - Provides user feedback for incomplete fields or invalid email addresses.
   - Displays success or failure messages based on the database update result.

3. **Navigation**
   - Allows users to return to the login page after completing or canceling the password reset process.

---

#### **Methods**

- **`initialize()`**
  - Ensures all FXML components are properly loaded and injected.

- **`handleResetPasswordAction(ActionEvent event)`**
  - Validates email and password fields.
  - Calls `DataBase.changePassword()` to update the user's password.
  - Displays success or failure messages using `statusLabel`.

- **`handleBackToLoginAction(ActionEvent event)`**
  - Navigates back to the login screen by loading the `login.fxml` file.
  - Updates the current stage with the login scene.

---

#### **Usage**

1. **Resetting Password**
   - The user enters their registered email and a new password.
   - On clicking the reset button, the system validates the input and updates the password in the database.

2. **Navigating to Login**
   - The user can return to the login screen by clicking the "Back to Login" button.



#### **FXML Requirements**

- **Fields**
  - `emailField`: Input for the user's email.
  - `newPasswordField`: Input for the new password.
  - `statusLabel`: Label for displaying status messages.

- **FXML File**  
  Ensure `forgotpass.fxml` is correctly set up with matching `fx:id` attributes for the fields and buttons.

---

#### **Notes**

- The `DataBase` class must implement the `changePassword()` method to handle the database logic securely.
- Customize the `statusLabel` messages for better user communication.
- Ensure the file path `/com/example/musicresources/login.fxml` is accurate and the FXML file is present in the resources directory.

## Dashboard

#### **Class Overview**

The `DashBoardController` facilitates:
1. A dynamic image slideshow with smooth fade transitions.
2. Navigation to the music player view and settings.
3. Toggle functionality for displaying the "About" pane.
4. Profile image customization and display.

---

#### **Key Features**

1. **Dynamic Slideshow**
   - Displays a series of images (`imageView1`, `imageView2`, `imageView3`) with fade animations.
   - Automatically cycles through images using a sequential animation.

2. **Navigation**
   - Provides buttons to navigate to the music player (`MusicController`) or settings page.

3. **Profile Customization**
   - Allows the user's profile image to be set dynamically and displayed on the dashboard.

4. **About Pane Toggle**
   - Displays or hides an "About" section with one button click.

5. **Settings Integration**
   - Opens a dedicated settings page with a fixed-size window.

---

#### **Methods**

- **`initialize()`**
  - Sets up the initial slideshow images and starts the fade animation.

- **`playFadeAnimation()`**
  - Manages the fade transitions for the slideshow images.
  - Cycles through images in a loop.

- **`createFadeTransition(ImageView imageView, double from, double to)`**
  - Helper method for creating a fade animation for an image.

- **`handleHome_btn(ActionEvent event)`**
  - Navigates to the `MusicController` view.
  - Passes the user’s profile image URL to the music view.

- **`handleAbout_btn(ActionEvent event)`**
  - Toggles the visibility of the "About" pane.

- **`handleSettings(ActionEvent event)`**
  - Opens the settings page in a fixed-size window.

- **`setUserProfileImage(String profileImageUrl)`**
  - Sets and displays the user's profile image on the dashboard.

---

#### **Usage**

1. **Slideshow**
   - The dashboard displays a series of images that fade in and out dynamically.
   - Customize the images by updating the paths in the `images` array.

2. **Profile Image**
   - Use `setUserProfileImage(String profileImageUrl)` to dynamically load and display the user's profile image.

3. **Navigation**
   - Navigate to the music player or settings using dedicated buttons.

4. **Settings**
   - Opens a fixed-size settings page for user configuration.



#### **FXML Requirements**

- **Fields**
  - `imageView1`, `imageView2`, `imageView3`: Image views for the slideshow.
  - `aboutPane`: Pane for the "About" section.
  - `welcomeText`: Text displayed when "About" is hidden.
  - `profileImageView`: Image view for the user's profile picture.

- **FXML File**
  - Ensure `dashboard.fxml` includes the above fields with matching `fx:id` attributes.

---

#### **Notes**

- Update the image paths in the `images` array to match your application's resource structure.
- Ensure the FXML files for the dashboard, music view, and settings are in the correct paths.
- Customize the window dimensions and styles for a cohesive user experience.

  ## Music Controller
  
#### **Class Overview**

`MusicController` is responsible for managing the user interface, audio playback, user library, and playlists. It integrates with Azure Blob Storage for media storage and retrieval, ensuring seamless user interaction.

---

#### **Key Features**

1. **User Library Management**
   - Displays songs with metadata (e.g., name, artist, album, duration).
   - Allows adding songs to the library or playlists.
   - Validates downloaded songs and updates their status.

2. **Playlist Management**
   - Create, edit, and delete playlists.
   - Add or remove songs from playlists.
   - Navigate between playlists and the user library.

3. **Music Playback**
   - Play, pause, stop, and navigate tracks (next/previous).
   - Shuffle songs in the playlist.
   - Displays current song details (name, artist).

4. **File Management**
   - Upload music files to Azure Blob Storage with metadata extraction.
   - Download songs with progress tracking.
   - Extract metadata (e.g., artist, duration, album).

5. **UI/UX Enhancements**
   - Sidebar with sliding animation.
   - Search bar with dynamic results table.
   - Animated album cover transitions.
   - Dark mode and light mode toggle.

6. **Search Functionality**
   - Search for songs in the user library or Azure Blob Storage by name, artist, or album.

---

#### **Methods**

- **`initialize()`**
  - Sets up the UI, validates downloaded songs, and loads user data.
  - Configures event handlers for playlists, search, and playback.

- **`handleUpload_btn(ActionEvent actionEvent)`**
  - Allows the user to upload songs to Azure Blob Storage with a progress tracker.

- **`loadUserLibrary(int userId)`**
  - Retrieves the user's library from the database and updates the TableView.

- **`addDoubleClickToPlay()`**
  - Adds a double-click event to the user library table for starting playback.

- **`playCurrentSong()`**
  - Plays the currently selected song in the playlist or library.

- **`onThemeToggleButtonClick()`**
  - Toggles between dark mode and light mode for the application.

- **`handleLogOutAction(ActionEvent event)`**
  - Navigates the user back to the login screen.

- **`validateDownloadedSongs()`**
  - Verifies the existence of downloaded files and updates their status in the library.

- **`createUploadTask(File file)`**
  - Uploads a selected music file to Azure Blob Storage and extracts metadata.

- **`handleSearch_btn(ActionEvent event)`**
  - Executes a search query and displays results in a table.

---

#### **Usage**

1. **Set Up Azure Blob Storage**
   - Replace the placeholder `CONNECTION_STRING` with valid Azure credentials.

2. **Database Configuration**
   - Ensure the database schema is configured and connection details are updated in the `DataBase` class.

3. **Run the Application**
   - Start the JavaFX application to access the library, playlists, and playback features.

4. **Interacting with Features**
   - **Library:** Add, play, and download songs.
   - **Playlists:** Create and manage playlists.
   - **Search:** Search for songs by metadata.

---

#### **Notes**

- This class interacts with `MusicDB`, `DataBase`, and `UserSession` for backend operations.
- Ensure the Azure SDK and database dependencies are included in the project.







 

