package com.example.gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.net.http.*;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.core.type.TypeReference;

public class MainController {

    @FXML private Label comPoolLabel;
    @FXML private Label gridPortionLabel;
    @FXML private Button refreshButton;

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Button showDataButton;

    @FXML private Label histProdLabel;
    @FXML private Label histUsedLabel;
    @FXML private Label histGridLabel;

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @FXML
    public void initialize() {
        // Default-Daten für DatePicker
        startDatePicker.setValue(java.time.LocalDate.now());
        endDatePicker.setValue(java.time.LocalDate.now());

        // Button-Handler
        refreshButton.setOnAction(e -> fetchCurrent());
        showDataButton.setOnAction(e -> {
            LocalDateTime start = startDatePicker.getValue().atStartOfDay();
            LocalDateTime end   = endDatePicker.getValue().atStartOfDay();
            fetchHistorical(start, end);
        });
    }

    private void fetchCurrent() {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/energy/current"))
                .build();

        client.sendAsync(req, HttpResponse.BodyHandlers.ofString())
                .thenAccept(resp -> {
                    try {
                        JsonNode node = mapper.readTree(resp.body());
                        double c = node.get("communityDepleted").asDouble();
                        double g = node.get("gridPortion").asDouble();
                        // UI-Update im JavaFX-Thread
                        javafx.application.Platform.runLater(() -> {
                            comPoolLabel.setText(String.format("%.2f%%", c));
                            gridPortionLabel.setText(String.format("%.2f%%", g));
                        });
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
    }

    private void fetchHistorical(LocalDateTime start, LocalDateTime end) {
        String url = String.format(
                "http://localhost:8080/energy/historical?start=%s&end=%s",
                start, end);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        client.sendAsync(req, HttpResponse.BodyHandlers.ofString())
                .thenAccept(resp -> {
                    try {
                        // Wir simulieren nur einen Eintrag: nimm den ersten
                        List<HistoricalEntry> list = mapper.readValue(
                                resp.body(),
                                new TypeReference<List<HistoricalEntry>>(){});
                        if (!list.isEmpty()) {
                            HistoricalEntry h = list.get(0);
                            javafx.application.Platform.runLater(() -> {
                                histProdLabel.setText(String.format("%.2f", h.getCommunityProduced()));
                                histUsedLabel.setText(String.format("%.2f", h.getCommunityUsed()));
                                histGridLabel.setText(String.format("%.2f", h.getGridUsed()));
                            });
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
    }

    // Hilfsklasse für Jackson-Mapping
    public static class HistoricalEntry {
        private double communityProduced;
        private double communityUsed;
        private double gridUsed;
        public double getCommunityProduced() { return communityProduced; }
        public double getCommunityUsed()    { return communityUsed; }
        public double getGridUsed()         { return gridUsed; }
    }
}
