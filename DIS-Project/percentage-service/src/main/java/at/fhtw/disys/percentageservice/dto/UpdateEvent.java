package at.fhtw.disys.percentageservice.dto;

import java.time.LocalDateTime;

public class UpdateEvent {
    private LocalDateTime datetime;
    private double communityProduced;
    private double communityUsed;
    private double gridUsed;

    public UpdateEvent() {}

    // Getter / Setter

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
}