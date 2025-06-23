package at.fhtw.disys.dto;

import java.time.LocalDateTime;

public class EnergyUsageMessage {

    private String type = "USER";
    private String association = "COMMUNITY";
    private double kwh;
    private LocalDateTime datetime;

    public EnergyUsageMessage() {}

    public EnergyUsageMessage(double kwh, LocalDateTime datetime) {
        this.kwh     = kwh;
        this.datetime = datetime;
    }

    // Getter / Setter

    public String getType() {
        return type;
    }

    public String getAssociation() {
        return association;
    }

    public double getKwh() {
        return kwh;
    }
    public void setKwh(double kwh) {
        this.kwh = kwh;
    }

    public LocalDateTime getDatetime() {
        return datetime;
    }
    public void setDatetime(LocalDateTime datetime) {
        this.datetime = datetime;
    }
}
