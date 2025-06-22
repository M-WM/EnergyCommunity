package at.fhtw.disys.usageservice.dto;

import java.time.LocalDateTime;

public class EnergyMessage {
    private String type;         // PRODUCER | USER
    private String association;  // COMMUNITY
    private double kwh;
    private LocalDateTime datetime;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAssociation() {
        return association;
    }

    public void setAssociation(String association) {
        this.association = association;
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