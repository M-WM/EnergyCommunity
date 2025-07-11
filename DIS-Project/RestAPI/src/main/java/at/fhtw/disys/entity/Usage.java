package at.fhtw.disys.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usage_table")
public class Usage {

    @Id
    @Column(name = "hour", nullable = false)
    private LocalDateTime hour;

    @Column(name = "community_produced", nullable = false)
    private double communityProduced;

    @Column(name = "community_used", nullable = false)
    private double communityUsed;

    @Column(name = "grid_used", nullable = false)
    private double gridUsed;

    // Standard-Konstruktor
    public Usage() {}

    // Getter/Setter
    public LocalDateTime getHour() { return hour; }
    public void setHour(LocalDateTime hour) { this.hour = hour; }

    public double getCommunityProduced() { return communityProduced; }
    public void setCommunityProduced(double communityProduced) {
        this.communityProduced = communityProduced;
    }

    public double getCommunityUsed() { return communityUsed; }
    public void setCommunityUsed(double communityUsed) {
        this.communityUsed = communityUsed;
    }

    public double getGridUsed() { return gridUsed; }
    public void setGridUsed(double gridUsed) {
        this.gridUsed = gridUsed;
    }
}