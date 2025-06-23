package at.fhtw.disys.service;

import at.fhtw.disys.dto.EnergyMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class EnergyProducerService {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    private final Random random = new Random();

    public EnergyProducerService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Scheduled(fixedDelayString = "#{T(java.util.concurrent.ThreadLocalRandom).current().nextInt(1000,5000)}")
    public void sendEnergyMessage() {
        double minKwh = 0.001;
        double maxKwh = 0.005;
        double kwh = minKwh + (maxKwh - minKwh) * random.nextDouble();
        EnergyMessage message = new EnergyMessage(kwh, LocalDateTime.now());
        try {
            String json = objectMapper.writeValueAsString(message);
            rabbitTemplate.convertAndSend("energy.exchange", "energy.routing", json);
            System.out.println("Sent: " + json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}