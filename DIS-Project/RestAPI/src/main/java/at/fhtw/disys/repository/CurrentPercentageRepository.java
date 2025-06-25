package at.fhtw.disys.repository;

import at.fhtw.disys.entity.CurrentPercentage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.Optional;

public interface CurrentPercentageRepository
        extends JpaRepository<at.fhtw.disys.entity.CurrentPercentage, Long> {
    Optional<CurrentPercentage> findByHour(LocalDateTime hour);
}