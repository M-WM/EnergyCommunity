package at.fhtw.disys.dto;

import java.time.LocalDateTime;

public class EnergyMessage {
    private String type = "PRODUCER";
    private String association = "COMMUNITY";
    private double kwh;
    private LocalDateTime datetime;

    public EnergyMessage() {}

    public EnergyMessage(double kwh, LocalDateTime datetime) {
        this.kwh = kwh;
        this.datetime = datetime;
    }

    public String getType() { return type; }
    public String getAssociation() { return association; }
    public double getKwh() { return kwh; }
    public void setKwh(double kwh) { this.kwh = kwh; }
    public LocalDateTime getDatetime() { return datetime; }
    public void setDatetime(LocalDateTime datetime) { this.datetime = datetime; }
}