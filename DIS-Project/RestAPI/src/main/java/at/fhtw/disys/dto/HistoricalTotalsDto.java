package at.fhtw.disys.dto;

public class HistoricalTotalsDto {
    private double communityProducedSum;
    private double communityUsedSum;
    private double gridUsedSum;

    public HistoricalTotalsDto() {}

    public HistoricalTotalsDto(double prod, double used, double grid) {
        this.communityProducedSum = prod;
        this.communityUsedSum     = used;
        this.gridUsedSum          = grid;
    }

    public double getCommunityProducedSum() { return communityProducedSum; }
    public void setCommunityProducedSum(double communityProducedSum) {
        this.communityProducedSum = communityProducedSum;
    }

    public double getCommunityUsedSum() { return communityUsedSum; }
    public void setCommunityUsedSum(double communityUsedSum) {
        this.communityUsedSum = communityUsedSum;
    }

    public double getGridUsedSum() { return gridUsedSum; }
    public void setGridUsedSum(double gridUsedSum) {
        this.gridUsedSum = gridUsedSum;
    }
}