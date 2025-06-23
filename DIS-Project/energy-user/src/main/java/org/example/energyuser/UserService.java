package org.example.energyuser;

import org.example.energyuser.config.RabbitConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class UserService {

    private final RabbitTemplate rabbit;

    public UserService(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }

    @Scheduled(fixedDelayString =
            "#{T(java.util.concurrent.ThreadLocalRandom).current().nextInt(1000,5000)}")
    public void sendUsage() {
        // Peakzeiten: 6–9 und 17–20 Uhr
        int hour = LocalTime.now().getHour();
        double base = (hour >= 6 && hour < 9) || (hour >= 17 && hour < 20)
                ? 0.003 : 0.001;
        double kwh = base + ThreadLocalRandom.current().nextDouble() * base;

        EnergyMessage msg = new EnergyMessage(
                "USER",
                "COMMUNITY",
                kwh,
                LocalDateTime.now()
        );
        rabbit.convertAndSend(
                RabbitConfig.EXCHANGE,
                "usage",
                msg
        );
        System.out.println("Sent: kwH: " + kwh + " time: " + LocalDateTime.now());
    }

    public static record EnergyMessage(
            String type,
            String association,
            double kwh,
            LocalDateTime datetime
    ) {}
}
