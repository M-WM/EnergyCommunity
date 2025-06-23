package at.fhtw.disys.energyuser;

import at.fhtw.disys.config.RabbitConfig;
import at.fhtw.disys.dto.EnergyUsageMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.Random;

@Service
public class EnergyUserService {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper  objectMapper;
    private final Random        random = new Random();

    public EnergyUserService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper    = new ObjectMapper()
                .registerModule(new JavaTimeModule());
    }

    /**
     * Sende alle 1–5 Sekunden eine USER-Nachricht.
     * Die Verbrauchsmenge variiert je nach Tageszeit:
     * – Morgens (6–9h) und Abends (17–20h) höher,
     * sonst niedriger.
     */
    @Scheduled(fixedDelayString =
            "#{T(java.util.concurrent.ThreadLocalRandom).current().nextInt(1000,5000)}")
    public void sendUsage() {
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();

        // Grundverbrauch zwischen 0.0005 und 0.001 kWh pro Minute
        double base = 0.0005 + random.nextDouble() * 0.0005;

        // Peak-Faktor morgens und abends
        if (hour >= 6 && hour < 9 || hour >= 17 && hour < 21) {
            base *= 2.5;
        }

        EnergyUsageMessage msg = new EnergyUsageMessage(base, now);

        try {
            String json = objectMapper.writeValueAsString(msg);
            rabbitTemplate.convertAndSend(
                    RabbitConfig.EXCHANGE_NAME,
                    RabbitConfig.ROUTING_KEY,
                    json
            );
            System.out.println("Sent USER: " + json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
