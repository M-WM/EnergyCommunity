package at.fhtw.disys.controller;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.net.http.*;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import com.fasterxml.jackson.databind.*;
import javafx.util.StringConverter;

public class MainController {

    @FXML private Label comPoolLabel;
    @FXML private Label gridPortionLabel;
    @FXML private Button refreshButton;

    @FXML private DatePicker startDatePicker;
    @FXML private Spinner<Integer> startHourSpinner;

    @FXML private DatePicker endDatePicker;
    @FXML private Spinner<Integer> endHourSpinner;

    @FXML private Button showDataButton;

    @FXML private Label comProdTotalLabel;
    @FXML private Label comUsedTotalLabel;
    @FXML private Label gridTotalLabel;

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @FXML
    public void initialize() {
        // Jackson Java-Time support
        mapper.registerModule(new JavaTimeModule());

        // Spinner für Stunden (0–23)
        var startFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0);
        var endFactory   = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0);

        // Anzeige im Format "HH:00"
        StringConverter<Integer> hhmmConverter = new StringConverter<>() {
            @Override
            public String toString(Integer hour) {
                return hour == null ? "" : String.format("%02d:00", hour);
            }
            @Override
            public Integer fromString(String text) {
                // Parsen: nimm alles vor dem „:“
                if (text == null || text.isBlank()) return 0;
                return Integer.parseInt(text.split(":")[0]);
            }
        };

        startHourSpinner.setValueFactory(startFactory);
        startHourSpinner.getValueFactory().setConverter(hhmmConverter);
        endHourSpinner.setValueFactory(endFactory);
        endHourSpinner.getValueFactory().setConverter(hhmmConverter);

        // Default: heutiges Datum und aktuelle volle Stunde
        LocalDate today = LocalDate.now();
        startDatePicker.setValue(today);
        endDatePicker  .setValue(today);
        int currentHour = LocalTime.now().getHour();
        startFactory.setValue(currentHour);
        endFactory.setValue(currentHour);

        // Button-Handler
        refreshButton.setOnAction(e -> fetchCurrent());
        showDataButton.setOnAction(e -> {
            LocalDate d1 = startDatePicker.getValue();
            LocalDate d2 = endDatePicker.getValue();
            int h1 = startHourSpinner.getValue();
            int h2 = endHourSpinner.getValue();

            LocalDateTime start = LocalDateTime.of(d1, LocalTime.of(h1, 0))
                    .truncatedTo(ChronoUnit.HOURS);
            LocalDateTime end   = LocalDateTime.of(d2, LocalTime.of(h2, 0))
                    .truncatedTo(ChronoUnit.HOURS);

            // Validierung: Start <= Ende
            if (start.isAfter(end)) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Ungültige Zeitspanne");
                alert.setHeaderText("Die Startzeit darf nicht nach der Endzeit liegen");
                alert.setContentText(
                        String.format("Start: %s\nEnde: %s", start, end)
                );
                alert.showAndWait();
                return;
            }

            // alles ok → Daten abfragen
            fetchAndSumHistorical(start, end);
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

    private void fetchAndSumHistorical(LocalDateTime start, LocalDateTime end) {
        String url = String.format(
                "http://localhost:8080/energy/historical?start=%s&end=%s",
                start, end);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        client.sendAsync(req, HttpResponse.BodyHandlers.ofString())
                .thenAccept(resp -> {
                    try {
                        // JSON-Baum parsen
                        JsonNode root = mapper.readTree(resp.body());
                        double sumProd = root.get("communityProducedSum").asDouble();
                        double sumUsed = root.get("communityUsedSum").asDouble();
                        double sumGrid = root.get("gridUsedSum").asDouble();

                        // UI-Update (Labels im JavaFX-Thread updaten)
                        Platform.runLater(() -> {
                            comProdTotalLabel.setText(String.format("%.3f kWh", sumProd));
                            comUsedTotalLabel.setText(String.format("%.3f kWh", sumUsed));
                            gridTotalLabel.setText(String.format("%.3f kWh", sumGrid));
                        });
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
    }
}
