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
    - [Run the Application](#run-the-application)  
6. [Folder Structure](#folder-structure)  
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

## Version Control  
- GitHub for repository management  

---

## Setup Instructions

## Clone the Repository  
```bash
git clone <repository-url>
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
  private static final String CONNECTION_STRING = "<Your_Azure_Connection_String>";
  ```  
- Ensure the Container Name is media-files.  

## Run the Application  
- Use your IDE to run the Main class or package the application as a JAR file:  
  ```bash
  java -jar Melodify.jar
  ```

---

## Folder Structure

src/main/java  
- controllers/: JavaFX controllers for managing UI  
- models/: Data models for songs, playlists, and metadata  
- services/: Azure Blob Storage and database interaction logic  

src/main/resources  
- fxml/: FXML files for the UI  
- css/: Stylesheets for themes and design  


 

