package at.fhtw.disys.percentageservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.Optional;

// First type parameter is the table, second is the type of the primary key of the table
public interface CurrentPercentageRepository extends JpaRepository<CurrentPercentage, Long> {
    Optional<CurrentPercentage> findByHour(LocalDateTime hour);
}