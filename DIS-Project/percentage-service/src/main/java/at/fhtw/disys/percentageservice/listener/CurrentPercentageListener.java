package at.fhtw.disys.percentageservice.listener;

import at.fhtw.disys.percentageservice.config.RabbitConfig;
import at.fhtw.disys.percentageservice.repository.CurrentPercentage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import at.fhtw.disys.percentageservice.dto.UsageUpdateEvent;
import at.fhtw.disys.percentageservice.repository.CurrentPercentageRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class CurrentPercentageListener {

    private final CurrentPercentageRepository repository;
    private final ObjectMapper objectMapper;

    public CurrentPercentageListener(CurrentPercentageRepository repository) {
        this.repository   = repository;
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
    }

    /**
     * Hört auf Update-Events vom Usage-Service in der percentage.queue,
     * berechnet die Prozentsätze und speichert sie in percentage_table.
     */
    @RabbitListener(queues = RabbitConfig.PERCENTAGE_QUEUE)
    public void handleUpdate(String json) {
        try {
            // 1) Nachricht deserialisieren
            UsageUpdateEvent evt = objectMapper.readValue(json, UsageUpdateEvent.class);

            // 2) Stunde extrahieren (Minute/Sekunde auf 0)
            LocalDateTime hour = evt.getDatetime()
                    .truncatedTo(ChronoUnit.HOURS);

            // 3) Prozentsätze berechnen
            double produced = evt.getCommunityProduced();
            double used     = evt.getCommunityUsed();
            double grid     = evt.getGridUsed();

            double communityDepleted = produced > 0
                    ? Math.min(100.0, (used / produced) * 100.0)
                    : 0.0;

            double gridPortion = (produced + grid) > 0
                    ? (grid / (produced + grid)) * 100.0
                    : 0.0;

            // 4) Entity laden oder neu erzeugen
            CurrentPercentage cp = repository.findByHour(hour)
                    .orElseGet(() -> {
                        CurrentPercentage c = new CurrentPercentage();
                        c.setHour(hour);
                        return c;
                    });

            // 5) Werte setzen und speichern
            cp.setCommunityDepleted(communityDepleted);
            cp.setGridPortion(gridPortion);
            repository.save(cp);

        } catch (Exception e) {
            // Fehler protokollieren, Listener-Container bleibt erhalten
            e.printStackTrace();
        }
    }
}