module com.example.muicapplication {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.muicapplication to javafx.fxml;
    exports com.example.muicapplication;
}