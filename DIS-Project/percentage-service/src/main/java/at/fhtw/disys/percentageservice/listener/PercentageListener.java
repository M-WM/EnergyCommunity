package at.fhtw.disys.percentageservice.listener;

import at.fhtw.disys.percentageservice.config.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import at.fhtw.disys.percentageservice.dto.UpdateEvent;
import at.fhtw.disys.percentageservice.repository.PercentageEntity;
import at.fhtw.disys.percentageservice.repository.PercentageRepository;

import java.time.temporal.ChronoUnit;

@Service
public class PercentageListener {

    private final PercentageRepository repo;

    public PercentageListener(PercentageRepository repo) {
        this.repo = repo;
    }

    @RabbitListener(queues = RabbitConfig.PERCENT_QUEUE)
    public void onUpdate(UpdateEvent msg) {
        // Stunde extrahieren
        var hour = msg.getDatetime()
                .truncatedTo(ChronoUnit.HOURS);

        // Prozentsätze berechnen
        double produced = msg.getCommunityProduced();
        double used     = msg.getCommunityUsed();
        double grid     = msg.getGridUsed();

        double communityDepleted = produced > 0
                ? Math.min(100.0, used / produced * 100.0)
                : 0.0;
        double gridPortion = (produced + grid) > 0
                ? (grid / (produced + grid)) * 100.0
                : 0.0;

        // Entity erzeugen oder updaten
        PercentageEntity cp = repo.findByHour(hour)
                .orElseGet(() -> {
                    var e = new PercentageEntity();
                    e.setHour(hour);
                    return e;
                });
        cp.setCommunityDepleted(communityDepleted);
        cp.setGridPortion(gridPortion);

        // Speichern
        repo.save(cp);
    }
}