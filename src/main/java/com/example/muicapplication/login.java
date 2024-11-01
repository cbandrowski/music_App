package com.example.muicapplication;


import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class login {


        public void start(Stage primaryStage) throws Exception {
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            primaryStage.setTitle("Music App Login");
            // Set a minimum width and height for the window

            primaryStage.setScene(new Scene(root, 600, 400));
            primaryStage.show();
        }


}
