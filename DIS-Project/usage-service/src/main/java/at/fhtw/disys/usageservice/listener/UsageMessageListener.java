package at.fhtw.disys.usageservice.listener;

import at.fhtw.disys.usageservice.config.RabbitConfig;
import at.fhtw.disys.usageservice.dto.EnergyMessage;
import at.fhtw.disys.usageservice.dto.UsageUpdateEvent;
import at.fhtw.disys.usageservice.repository.UsageEntity;
import at.fhtw.disys.usageservice.repository.UsageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class UsageMessageListener {

    private final UsageRepository repository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public UsageMessageListener(UsageRepository repository, RabbitTemplate rabbitTemplate) {
        this.repository = repository;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Hört auf PRODUCER- und USER-Nachrichten in der Queue,
     * aktualisiert die usage_table pro Stunde und sendet anschließend
     * ein Update-Event an den Current Percentage Service.
     */
    @RabbitListener(queues = "energy.queue")
    @Transactional
    public void handleMessage(String json) {
        try {
            // 1) Nachricht parsen
            EnergyMessage msg = objectMapper.readValue(json, EnergyMessage.class);

            // 2) Stunde extrahieren (Minute/Sekunde/Nano auf 0)
            LocalDateTime hour = msg.getDatetime()
                    .truncatedTo(ChronoUnit.HOURS);

            // 3) Entity laden oder neu anlegen
            UsageEntity usage = repository.findById(hour)
                    .orElseGet(() -> {
                        UsageEntity u = new UsageEntity();
                        u.setHour(hour);
                        u.setCommunityProduced(0.0);
                        u.setCommunityUsed(0.0);
                        u.setGridUsed(0.0);
                        return u;
                    });

            // 4) Werte aktualisieren
            if ("PRODUCER".equalsIgnoreCase(msg.getType())
                    && "COMMUNITY".equalsIgnoreCase(msg.getAssociation())) {
                usage.setCommunityProduced(
                        usage.getCommunityProduced() + msg.getKwh()
                );
            }
            else if ("USER".equalsIgnoreCase(msg.getType())
                    && "COMMUNITY".equalsIgnoreCase(msg.getAssociation())) {

                double newUsage = msg.getKwh();
                double available = usage.getCommunityProduced() - usage.getCommunityUsed();
                double deficit = Math.max(0.0, newUsage - available);

                usage.setCommunityUsed(
                        usage.getCommunityUsed() + newUsage
                );
                usage.setGridUsed(
                        usage.getGridUsed() + deficit
                );
            }

            // 5) Persistieren
            repository.save(usage);

            // 6) Update-Event an Percentage-Service senden
            UsageUpdateEvent evt = new UsageUpdateEvent();
            evt.setDatetime(usage.getHour());
            evt.setCommunityProduced(usage.getCommunityProduced());
            evt.setCommunityUsed(usage.getCommunityUsed());
            evt.setGridUsed(usage.getGridUsed());

            String evtJson = objectMapper.writeValueAsString(evt);
            rabbitTemplate.convertAndSend(
                    RabbitConfig.EXCHANGE,
                    RabbitConfig.UPDATE_ROUTING_KEY,
                    evtJson
            );

        } catch (Exception e) {
            // Fehler protokollieren, aber nicht den gesamten Listener-Container stoppen
            e.printStackTrace();
        }
    }
}