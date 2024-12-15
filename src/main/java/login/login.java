package login;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;

public class login  { //


    public void start(Stage primaryStage) throws Exception {
        // Use the absolute path to load the FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/musicresources/view/login.fxml"));
        Parent root = loader.load();
        preloadResources();
        primaryStage.setTitle("Music App Login");
        primaryStage.setScene(new Scene(root, 795, 500));
        primaryStage.show();
    }
    public void preloadResources() {
        List<String> stylesheets = List.of(
                "/com/example/musicresources/css/dark-theme.css",
                "/com/example/musicresources/css/light-theme.css"
        );
        for (String stylesheet : stylesheets) {
            new Thread(() -> {
                try {
                    getClass().getResource(stylesheet).toExternalForm();
                    System.out.println("Preloaded stylesheet: " + stylesheet);
                } catch (Exception e) {
                    System.err.println("Failed to preload stylesheet: " + stylesheet);
                }
            }).start();
        }
    }

//    public static void main(String[] args) {
//        launch(args);
//
//    }
}
