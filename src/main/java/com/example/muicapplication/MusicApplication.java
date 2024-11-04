package com.example.muicapplication;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MusicApplication extends Application {

    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MusicApplication.class.getResource("music-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);

        // Set default to light theme
        scene.getStylesheets().add(MusicApplication.class.getResource("light-theme.css").toExternalForm());

        stage.setTitle("Music App");
        stage.setScene(scene);
        stage.show();
    }

 public static void main(String[] args) {
       launch();
   }
}
