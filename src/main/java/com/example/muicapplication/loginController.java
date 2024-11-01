package com.example.muicapplication;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class loginController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Text statusMessage;

    @FXML
    private void handleLogin() throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Add your login logic here (e.g., validation against a database)
        if (username.equals("user") && password.equals("pass")) { // Replace with your logic
            statusMessage.setText("Login successful!");
            System.out.println("login successful"); //for debugging purposes

            //loads to the dashboard
            MusicApplication start = new MusicApplication();
            start.start(new Stage());

        } else {
            statusMessage.setText("Invalid username or password.");
        }
    }

    //will take you to register
    public void handleRegister() throws Exception {
        register screen = new register();
        screen.start(new Stage());

    }
}
