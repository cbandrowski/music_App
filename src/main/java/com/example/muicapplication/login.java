package com.example.muicapplication;





import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class login extends Application{

        @Override
        public void start(Stage primaryStage) throws Exception {
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            primaryStage.setTitle("Music App Login");
            // Set a minimum width and height for the window
            primaryStage.setMinWidth(400);
            primaryStage.setMinHeight(300);

            primaryStage.setScene(new Scene(root, 400, 300));
            primaryStage.show();
        }

        public static void main(String[] args) {
            launch(args);
        }

}
