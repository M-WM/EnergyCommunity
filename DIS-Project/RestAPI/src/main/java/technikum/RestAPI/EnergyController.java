package technikum.RestAPI;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/energy")
public class EnergyController {

    // === Current ===
    @GetMapping("/current")
    public CurrentData getCurrent() {
        // Beispiel-Werte
        return new CurrentData(78.54, 7.23);
    }

    // === Historical ===
    @GetMapping("/historical")
    public List<HistoricalEntry> getHistorical(
            @RequestParam("start")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        // Einfach nur einen Eintrag zurückliefern
        return List.of(
                new HistoricalEntry(143.024, 130.101, 14.75)
        );
    }

    // DTO für /energy/current
    public static class CurrentData {
        private double communityDepleted;
        private double gridPortion;
        public CurrentData() {}
        public CurrentData(double communityDepleted, double gridPortion) {
            this.communityDepleted = communityDepleted;
            this.gridPortion      = gridPortion;
        }
        public double getCommunityDepleted() { return communityDepleted; }
        public void   setCommunityDepleted(double v) { communityDepleted = v; }
        public double getGridPortion()      { return gridPortion; }
        public void   setGridPortion(double v)      { gridPortion = v; }
    }

    // DTO für /energy/historical
    public static class HistoricalEntry {
        private double communityProduced;
        private double communityUsed;
        private double gridUsed;
        public HistoricalEntry() {}
        public HistoricalEntry(double p, double u, double g) {
            this.communityProduced = p;
            this.communityUsed     = u;
            this.gridUsed          = g;
        }
        public double getCommunityProduced() { return communityProduced; }
        public void   setCommunityProduced(double v) { communityProduced = v; }
        public double getCommunityUsed()     { return communityUsed; }
        public void   setCommunityUsed(double v)     { communityUsed = v; }
        public double getGridUsed()          { return gridUsed; }
        public void   setGridUsed(double v)          { gridUsed = v; }
    }
}