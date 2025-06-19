module com.example.gui {
    // JavaFX-Basis
    requires javafx.controls;
    requires javafx.fxml;      // <<< hier wird javafx.fxml gelesen

    // HTTP-Client
    requires java.net.http;

    // Jackson für JSON
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;

    // Erlaube FXML-Loader Reflective-Zugriff auf deinen Controller
    opens com.example.gui to javafx.graphics, javafx.fxml;
}
