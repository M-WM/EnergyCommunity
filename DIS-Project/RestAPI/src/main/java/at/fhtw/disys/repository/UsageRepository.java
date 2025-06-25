package at.fhtw.disys.repository;

import at.fhtw.disys.entity.Usage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface UsageRepository
        extends JpaRepository<Usage, LocalDateTime> {

    List<Usage> findAllByHourBetweenOrderByHour(
            LocalDateTime start, LocalDateTime end);
}
