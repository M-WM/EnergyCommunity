package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.control.Label;

public class MainController {

    @FXML private Button refreshButton;
    @FXML private Button showDataButton;

    @FXML private Label ComPoolID;
    @FXML private Label GridPorID;
    @FXML private Label ComProdID;
    @FXML private Label ComUsedID;
    @FXML private Label GridUsedID;

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @FXML
    public void initialize() {
        // Live-Daten laden
        refreshButton.setOnAction(e -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/current-hour-data"))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                Data data = mapper.readValue(response.body(), Data.class);

                ComPoolID.setText("Pool: " + data.getTime());
                GridPorID.setText("Wert: " + data.getValue());
            } catch (Exception ex) {
                ComPoolID.setText("Fehler");
                ex.printStackTrace();
            }
        });

        // Zeitraum-Daten laden
        showDataButton.setOnAction(e -> {
            try {
                // 🛑 Vorher prüfen, ob Werte vorhanden sind
                if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
                    ComProdID.setText("Bitte Datum wählen!");
                    ComUsedID.setText("...");
                    GridUsedID.setText("...");
                    return;
                }

                String from = startDatePicker.getValue().toString();
                String to = endDatePicker.getValue().toString();

                String url = "http://localhost:8080/history-data?from=" + from + "&to=" + to;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                Data[] datenArray = mapper.readValue(response.body(), Data[].class);

                int sum = 0;
                for (Data d : datenArray) {
                    sum += d.getValue();
                }
                int avg = datenArray.length > 0 ? sum / datenArray.length : 0;

                ComProdID.setText("Summe: " + sum);
                ComUsedID.setText("Anzahl: " + datenArray.length);
                GridUsedID.setText("Ø Wert: " + avg);

            } catch (Exception ex) {
                ComProdID.setText("Fehler");
                ComUsedID.setText("...");
                GridUsedID.setText("...");
                ex.printStackTrace();
            }
        });

    }
}