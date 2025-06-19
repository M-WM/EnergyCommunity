package com.example.energy.model;

public class HistoricalData {
    private String hour;
    private double communityProduced;
    private double communityUsed;
    private double gridUsed;

    public HistoricalData() {}

    public HistoricalData(String hour, double communityProduced, double communityUsed, double gridUsed) {
        this.hour = hour;
        this.communityProduced = communityProduced;
        this.communityUsed = communityUsed;
        this.gridUsed = gridUsed;
    }

    public String getHour() {
        return hour;
    }
    public void setHour(String hour) {
        this.hour = hour;
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
