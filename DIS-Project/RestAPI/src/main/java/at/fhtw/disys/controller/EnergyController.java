package at.fhtw.disys.controller;

import at.fhtw.disys.model.HistoricalData;
import at.fhtw.disys.usageservice.repository.UsageEntity;
import at.fhtw.disys.usageservice.repository.UsageRepository;
import at.fhtw.disys.model.CurrentData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/energy")
public class EnergyController {

    @Autowired
    private UsageRepository repo;

    @GetMapping("/current")
    public CurrentData getCurrent() {
        String nowHour = LocalDateTime.now()
                                .withMinute(0)
                                .withSecond(0)
                                .withNano(0)
                                .toString();
        double communityDepleted = 78.54;
        double gridPortion     = 7.23;
        return new CurrentData(nowHour, communityDepleted, gridPortion);
    }

    /** Historische Daten nach Stunden-Filter */
    @GetMapping("/historical")
    public List<HistoricalData> getHistorical(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        // Runde Eingaben auf die jeweilige volle Stunde
        LocalDateTime from = start.truncatedTo(ChronoUnit.HOURS);
        LocalDateTime to   = end.truncatedTo(ChronoUnit.HOURS);

        System.out.println("Historical from=" + from + " to=" + to);

        List<UsageEntity> usages = repo.findAllByHourBetweenOrderByHour(from, to);

        System.out.println("Found " + usages.size() + " records");

        return usages.stream()
                .map(u -> new HistoricalData(
                        u.getHour().toString(),
                        u.getCommunityProduced(),
                        u.getCommunityUsed(),
                        u.getGridUsed()
                ))
                .collect(Collectors.toList());
    }

    // --- DTO-Klassen ---

    public static class CurrentDto {
        public LocalDateTime hour;
        public double communityDepleted;
        public double gridPortion;
        public CurrentDto(LocalDateTime h, double d, double g) {
            this.hour = h;
            this.communityDepleted = d;
            this.gridPortion = g;
        }
    }
}
