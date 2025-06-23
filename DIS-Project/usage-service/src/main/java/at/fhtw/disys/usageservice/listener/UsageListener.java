package at.fhtw.disys.usageservice.listener;

import at.fhtw.disys.usageservice.dto.EnergyMessage;
import at.fhtw.disys.usageservice.repository.UsageEntity;
import at.fhtw.disys.usageservice.repository.UsageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UsageListener {

    private final UsageRepository repository;
    private final ObjectMapper objectMapper;

    public UsageListener(UsageRepository repository) {
        this.repository = repository;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @RabbitListener(queues = "energy.queue")
    public void handleMessage(String json) {
        try {
            EnergyMessage message = objectMapper.readValue(json, EnergyMessage.class);
            LocalDateTime hour = message.getDatetime().withMinute(0).withSecond(0).withNano(0);

            UsageEntity usage = repository.findById(hour)
                    .orElseGet(() -> {
                        UsageEntity u = new UsageEntity();
                        u.setHour(hour);
                        u.setCommunityProduced(0.0);
                        u.setCommunityUsed(0.0);
                        u.setGridUsed(0.0);
                        return u;
                    });

            if ("PRODUCER".equals(message.getType()) && "COMMUNITY".equals(message.getAssociation())) {
                usage.setCommunityProduced(usage.getCommunityProduced() + message.getKwh());
            } else if ("USER".equals(message.getType()) && "COMMUNITY".equals(message.getAssociation())) {
                double remainingCommunityEnergy = usage.getCommunityProduced() - usage.getCommunityUsed();
                double deficit = Math.max(0, message.getKwh() - remainingCommunityEnergy);

                usage.setCommunityUsed(usage.getCommunityUsed() + message.getKwh());
                usage.setGridUsed(usage.getGridUsed() + deficit);
            }

            System.out.printf("Saving: hour=%s, produced=%.4f, used=%.4f, grid=%.4f%n",
                    hour, usage.getCommunityProduced(), usage.getCommunityUsed(), usage.getGridUsed());

            repository.save(usage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}