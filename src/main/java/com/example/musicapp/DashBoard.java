package com.example.musicapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DashBoard extends Application {

    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/musicresources/dashBoard.fxml"));
            Scene scene = new Scene(loader.load(), 1200, 800);
            stage.setTitle("Melodify");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();  // This will print the exact error details.
        }

    }
    public static void main(String[] args) {
        launch();
    }
}
