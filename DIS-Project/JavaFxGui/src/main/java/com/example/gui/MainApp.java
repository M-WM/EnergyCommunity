package com.example.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.net.http.*;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;

public class MainApp extends Application {
    private Label lblCommunity, lblGrid;
    private TableView<HistoricalRow> table;

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void start(Stage stage) {
        lblCommunity = new Label("Community: –");
        lblGrid      = new Label("Grid: –");

        Button btnRefresh = new Button("Refresh");
        btnRefresh.setOnAction(e -> fetchCurrent());

        DatePicker dpStart = new DatePicker(LocalDate.now());
        DatePicker dpEnd   = new DatePicker(LocalDate.now());
        Button btnHist     = new Button("Show Data");
        btnHist.setOnAction(e -> {
            LocalDateTime s = dpStart.getValue().atStartOfDay();
            LocalDateTime t = dpEnd.getValue().atStartOfDay();
            fetchHistorical(s, t);
        });

        table = new TableView<>();
        TableColumn<HistoricalRow,String> colHour = new TableColumn<>("Hour");
        colHour.setCellValueFactory(d -> d.getValue().hourProperty());
        TableColumn<HistoricalRow,Number> colProd = new TableColumn<>("Prod");
        colProd.setCellValueFactory(d -> d.getValue().prodProperty());
        TableColumn<HistoricalRow,Number> colUsed = new TableColumn<>("Used");
        colUsed.setCellValueFactory(d -> d.getValue().usedProperty());
        TableColumn<HistoricalRow,Number> colGrid = new TableColumn<>("Grid");
        colGrid.setCellValueFactory(d -> d.getValue().gridProperty());
        table.getColumns().addAll(colHour, colProd, colUsed, colGrid);

        HBox top = new HBox(10, lblCommunity, lblGrid, btnRefresh);
        HBox hist = new HBox(10,
            new Label("Start"), dpStart,
            new Label("End"),   dpEnd,
            btnHist
        );
        VBox root = new VBox(15, top, hist, table);
        root.setPadding(new Insets(15));

        stage.setScene(new Scene(root, 600, 400));
        stage.setTitle("Energy Monitor");
        stage.show();
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
                      lblCommunity.setText("Community: " + c + "%");
                      lblGrid.setText("Grid: " + g + "%");
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
                      List<HistoricalRow> rows = mapper.readValue(
                        resp.body(),
                        new TypeReference<List<HistoricalRow>>() {});
                      table.getItems().setAll(rows);
                  } catch (Exception ex) {
                      ex.printStackTrace();
                  }
              });
    }

    public static void main(String[] args) {
        launch();
    }
}
