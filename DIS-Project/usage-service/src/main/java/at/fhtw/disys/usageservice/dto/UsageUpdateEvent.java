package at.fhtw.disys.usageservice.dto;

import java.time.LocalDateTime;

/**
 * DTO, das nach jeder Aggregation aus dem Usage-Service
 * an den Current Percentage Service gesendet wird.
 */
public class UsageUpdateEvent {

    private LocalDateTime datetime;
    private double communityProduced;
    private double communityUsed;
    private double gridUsed;

    public UsageUpdateEvent() {
    }

    public UsageUpdateEvent(LocalDateTime datetime,
                            double communityProduced,
                            double communityUsed,
                            double gridUsed) {
        this.datetime = datetime;
        this.communityProduced = communityProduced;
        this.communityUsed = communityUsed;
        this.gridUsed = gridUsed;
    }

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(LocalDateTime datetime) {
        this.datetime = datetime;
    }

    public double getCommunityProduced() {
        return communityProduced;
    }

    public void setCommunityProduced(double communityProduced) {
        this.communityProduced = communityProduced;
    }

    public double getCommunityUsed() {
        return communityUsed;
    }

    public void setCommunityUsed(double communityUsed) {
        this.communityUsed = communityUsed;
    }

    public double getGridUsed() {
        return gridUsed;
    }

    public void setGridUsed(double gridUsed) {
        this.gridUsed = gridUsed;
    }

    @Override
    public String toString() {
        return "UsageUpdateEvent{" +
                "datetime=" + datetime +
                ", communityProduced=" + communityProduced +
                ", communityUsed=" + communityUsed +
                ", gridUsed=" + gridUsed +
                '}';
    }
}