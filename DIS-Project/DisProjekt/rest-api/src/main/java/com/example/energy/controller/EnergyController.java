package com.example.energy.controller;

import com.example.energy.model.CurrentData;
import com.example.energy.model.HistoricalData;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/energy")
public class EnergyController {

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

    @GetMapping("/historical")
    public List<HistoricalData> getHistorical(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        List<HistoricalData> list = new ArrayList<>();
        LocalDateTime cursor = start.withMinute(0).withSecond(0).withNano(0);
        Random rnd = new Random();

        while (!cursor.isAfter(end)) {
            double prod = 100 + rnd.nextDouble() * 50;
            double used = prod - rnd.nextDouble() * 20;
            double grid = Math.max(0, used - prod);
            list.add(new HistoricalData(cursor.toString(), prod, used, grid));
            cursor = cursor.plusHours(1);
        }
        return list;
    }
}
