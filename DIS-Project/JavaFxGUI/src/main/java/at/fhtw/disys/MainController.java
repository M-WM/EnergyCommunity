package at.fhtw.disys;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.net.http.*;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.core.type.TypeReference;
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
        mapper.registerModule(new JavaTimeModule());

        // Spinner für Stunden (0–23)
        startHourSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0));
        endHourSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0));

        // Zwei-stellige Anzeige
        StringConverter<Integer> twoDigit = new StringConverter<>() {
            @Override public String toString(Integer v) { return String.format("%02d", v); }
            @Override public Integer fromString(String s) { return Integer.valueOf(s); }
        };
        startHourSpinner.getValueFactory().setConverter(twoDigit);
        endHourSpinner.getValueFactory().setConverter(twoDigit);

        // Defaults
        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now());

        // Button-Handler
        refreshButton.setOnAction(e -> fetchCurrent());
        showDataButton.setOnAction(e -> {
            LocalDateTime start = LocalDateTime.of(
                    startDatePicker.getValue(),
                    LocalTime.of(startHourSpinner.getValue(), 0)
            );
            LocalDateTime end = LocalDateTime.of(
                    endDatePicker.getValue(),
                    LocalTime.of(endHourSpinner.getValue(), 0)
            );
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
                        // 1) JSON in Liste historischer Einträge parsen
                        List<HistoricalEntry> rows = mapper.readValue(
                                resp.body(),
                                new TypeReference<List<HistoricalEntry>>() {});

                        // 2) Summen berechnen
                        double sumProd = rows.stream()
                                .mapToDouble(HistoricalEntry::getCommunityProduced)
                                .sum();
                        double sumUsed = rows.stream()
                                .mapToDouble(HistoricalEntry::getCommunityUsed)
                                .sum();
                        double sumGrid = rows.stream()
                                .mapToDouble(HistoricalEntry::getGridUsed)
                                .sum();

                        // 3) Labels im JavaFX-Thread updaten
                        Platform.runLater(() -> {
                            comProdTotalLabel.setText(
                                    String.format("%.3f kWh", sumProd));
                            comUsedTotalLabel.setText(
                                    String.format("%.3f kWh", sumUsed));
                            gridTotalLabel.setText(
                                    String.format("%.3f kWh", sumGrid));
                        });

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
    }

    private void updateTotals(List<HistoricalEntry> rows) {
        double sumProduced = 0;
        double sumUsed     = 0;
        double sumGrid     = 0;

        for (HistoricalEntry h : rows) {
            sumProduced += h.getCommunityProduced();
            sumUsed     += h.getCommunityUsed();
            sumGrid     += h.getGridUsed();
        }

        // Anzeige aktualisieren (Labels oder Textfelder)
        comProdTotalLabel.setText(String.format("%.3f kWh", sumProduced));
        comUsedTotalLabel.setText(String.format("%.3f kWh", sumUsed));
        gridTotalLabel.setText(String.format("%.3f kWh", sumGrid));
    }

    // Hilfsklasse für Jackson-Mapping
    public static class HistoricalEntry {
        private String hour;                  // neu: String oder LocalDateTime
        private double communityProduced;
        private double communityUsed;
        private double gridUsed;

        // Standard-Konstruktor
        public HistoricalEntry() {}

        // Getter/Setter für hour
        public String getHour() {
            return hour;
        }
        public void setHour(String hour) {
            this.hour = hour;
        }

        // bestehende Getter/Setter
        public double getCommunityProduced() { return communityProduced; }
        public void setCommunityProduced(double p) { this.communityProduced = p; }

        public double getCommunityUsed() { return communityUsed; }
        public void setCommunityUsed(double u) { this.communityUsed = u; }

        public double getGridUsed() { return gridUsed; }
        public void setGridUsed(double g) { this.gridUsed = g; }
    }
}
