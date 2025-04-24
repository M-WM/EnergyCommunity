package technikum.RestAPI;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class DataController {

    @GetMapping("/current-hour-data")
    public Data getCurrentHourData() {
        return new Data("13:00", 120);
    }

    @GetMapping("/history-data")
    public List<Data> getHistoryData(@RequestParam String from, @RequestParam String to) {
        return List.of(
                new Data("12:00", 100),
                new Data("13:00", 120)
        );
    }
}
