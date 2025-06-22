package org.example.energyproducer;

import org.example.config.RabbitConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ProducerService {

    private final RabbitTemplate rabbit;

    public ProducerService(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }

    @Scheduled(fixedDelayString =
            "#{T(java.util.concurrent.ThreadLocalRandom).current().nextInt(2000,5000)}")
    public void sendProduction() {
        double sunlightFactor = ThreadLocalRandom.current().nextDouble();
        // Faktor zwischen 0.0 (keine Sonne) und 1.0 (volle Sonne)

        double minKwh = 0.001;
        double maxKwh = 0.005;
        double kwh = minKwh + (maxKwh - minKwh) * sunlightFactor;

        EnergyMessage msg = new EnergyMessage(
                "PRODUCER",
                "COMMUNITY",
                kwh,
                LocalDateTime.now()
        );
        rabbit.convertAndSend(
                RabbitConfig.EXCHANGE,
                "usage",
                msg
        );
    }

    public static record EnergyMessage(
            String type,
            String association,
            double kwh,
            LocalDateTime datetime
    ) { }
}
