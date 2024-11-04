module com.example.muicapplication {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;


    opens com.example.muicapplication to javafx.fxml;
    exports com.example.muicapplication;
}