package at.fhtw.disys.usageservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

// First type parameter is the table, second is the type of the primary key of the table
public interface UsageRepository extends JpaRepository<UsageEntity, LocalDateTime> {
}
