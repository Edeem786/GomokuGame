module com.example.gomokuexample {
    requires javafx.controls;
    requires javafx.fxml;
    requires jdk.compiler;


    opens com.example.gomokuexample to javafx.fxml;
    exports com.example.gomokuexample;
}