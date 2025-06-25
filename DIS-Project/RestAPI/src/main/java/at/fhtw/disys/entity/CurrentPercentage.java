package at.fhtw.disys.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "percentage_table")
public class CurrentPercentage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hour", nullable = false)
    private LocalDateTime hour;

    @Column(name = "community_depleted", nullable = false)
    private double communityDepleted;

    @Column(name = "grid_portion", nullable = false)
    private double gridPortion;

    // Standard-Konstruktor
    public CurrentPercentage() {}

    // Getter/Setter

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public LocalDateTime getHour() { return hour; }
    public void setHour(LocalDateTime hour) { this.hour = hour; }

    public double getCommunityDepleted() { return communityDepleted; }
    public void setCommunityDepleted(double communityDepleted) {
        this.communityDepleted = communityDepleted;
    }

    public double getGridPortion() { return gridPortion; }
    public void setGridPortion(double gridPortion) {
        this.gridPortion = gridPortion;
    }
}
