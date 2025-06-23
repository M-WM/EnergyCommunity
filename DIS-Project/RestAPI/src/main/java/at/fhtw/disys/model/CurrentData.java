package at.fhtw.disys.model;

public class CurrentData {
    private String hour;
    private double communityDepleted; // in %
    private double gridPortion;       // in %

    public CurrentData() {}

    public CurrentData(String hour, double communityDepleted, double gridPortion) {
        this.hour = hour;
        this.communityDepleted = communityDepleted;
        this.gridPortion = gridPortion;
    }

    public String getHour() {
        return hour;
    }
    public void setHour(String hour) {
        this.hour = hour;
    }
    public double getCommunityDepleted() {
        return communityDepleted;
    }
    public void setCommunityDepleted(double communityDepleted) {
        this.communityDepleted = communityDepleted;
    }
    public double getGridPortion() {
        return gridPortion;
    }
    public void setGridPortion(double gridPortion) {
        this.gridPortion = gridPortion;
    }
}
