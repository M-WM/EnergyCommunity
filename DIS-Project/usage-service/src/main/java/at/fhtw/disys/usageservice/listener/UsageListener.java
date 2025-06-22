package at.fhtw.disys.usageservice.listener;

import at.fhtw.disys.usageservice.dto.EnergyMessage;
import at.fhtw.disys.usageservice.repository.UsageEntity;
import at.fhtw.disys.usageservice.repository.UsageRepository;
import at.fhtw.disys.usageservice.config.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.time.LocalDateTime;

@Service
public class UsageListener {

    private final UsageRepository repository;
    private final RabbitTemplate rabbitTemplate;

    public UsageListener(UsageRepository repository, RabbitTemplate rabbitTemplate) {
        this.repository = repository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = RabbitConfig.USAGE_QUEUE)
    public void handleMessage(EnergyMessage msg) {
        LocalDateTime hour = msg.getDatetime().truncatedTo(ChronoUnit.HOURS);
        UsageEntity usage = repository.findById(hour).orElseGet(() -> {
            var newUsage = new UsageEntity();
            newUsage.setHour(hour);
            return newUsage;
        });

        if ("PRODUCER".equalsIgnoreCase(msg.getType())) {
            usage.setCommunityProduced(usage.getCommunityProduced() + msg.getKwh());
        } else if ("USER".equalsIgnoreCase(msg.getType())) {
            usage.setCommunityUsed(usage.getCommunityUsed() + msg.getKwh());
            double excess = usage.getCommunityUsed() - usage.getCommunityProduced();
            if (excess > 0) {
                usage.setGridUsed(usage.getGridUsed() + excess);
            }
        }

        repository.save(usage);

        // Nachricht an Percentage-Service
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                "percent",
                new UpdateEvent(
                        usage.getHour(),
                        usage.getCommunityProduced(),
                        usage.getCommunityUsed(),
                        usage.getGridUsed()
                )
        );
    }

    public static class UpdateEvent {
        private final LocalDateTime datetime;
        private final double communityProduced;
        private final double communityUsed;
        private final double gridUsed;

        public UpdateEvent(LocalDateTime datetime, double cp, double cu, double grid) {
            this.datetime = datetime;
            this.communityProduced = cp;
            this.communityUsed = cu;
            this.gridUsed = grid;
        }

        public LocalDateTime getDatetime() { return datetime; }
        public double getCommunityProduced() { return communityProduced; }
        public double getCommunityUsed() { return communityUsed; }
        public double getGridUsed() { return gridUsed; }
    }
}