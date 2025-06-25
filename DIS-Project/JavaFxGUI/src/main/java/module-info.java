module at.fhtw.disys {
    // JavaFX-Basis
    requires javafx.controls;
    requires javafx.fxml;

    // HTTP-Client
    requires java.net.http;

    // Jackson für JSON
    requires com.fasterxml.jackson.datatype.jsr310;
    requires com.fasterxml.jackson.databind;

    // Erlaube FXML-Loader Reflective-Zugriff auf Controller
    opens at.fhtw.disys
            to javafx.graphics, javafx.fxml, com.fasterxml.jackson.databind;
    opens at.fhtw.disys.controller to com.fasterxml.jackson.databind, javafx.fxml, javafx.graphics;
}