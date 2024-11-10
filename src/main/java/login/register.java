package login;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class register {

    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/musicresources/registeration.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Music App Registration");
        // Set a minimum width and height for the window
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(300);

        primaryStage.setScene(new Scene(root, 400, 300));
        primaryStage.show();
    }
}
