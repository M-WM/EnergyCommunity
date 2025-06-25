package at.fhtw.disys.controller;

import at.fhtw.disys.entity.CurrentPercentage;
import at.fhtw.disys.entity.Usage;
import at.fhtw.disys.repository.CurrentPercentageRepository;
import at.fhtw.disys.repository.UsageRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RestController
@RequestMapping("/energy")
public class EnergyController {

    private final UsageRepository usageRepo;
    private final CurrentPercentageRepository percRepo;

    public EnergyController(UsageRepository usageRepo,
                            CurrentPercentageRepository percRepo) {
        this.usageRepo = usageRepo;
        this.percRepo  = percRepo;
    }

    @GetMapping("/current")
    public CurrentPercentage getCurrent() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);

        return percRepo.findByHour(now)
                .orElseGet(() -> {
                    CurrentPercentage cp = new CurrentPercentage();
                    cp.setHour(now);
                    cp.setCommunityDepleted(0);
                    cp.setGridPortion(0);
                    return cp;
                });
    }

    /** Historische Daten nach Stunden-Filter */
    @GetMapping("/historical")
    public List<Usage> getHistorical(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime end) {

        LocalDateTime from = start.truncatedTo(ChronoUnit.HOURS);
        LocalDateTime to   = end.truncatedTo(ChronoUnit.HOURS);

        return usageRepo.findAllByHourBetweenOrderByHour(from, to);
    }
}
