package com.example.gui;

import javafx.beans.property.*;

public class HistoricalRow {
    private final StringProperty hour = new SimpleStringProperty();
    private final DoubleProperty prod = new SimpleDoubleProperty();
    private final DoubleProperty used = new SimpleDoubleProperty();
    private final DoubleProperty grid = new SimpleDoubleProperty();

    public HistoricalRow() {}

    public String getHour() {
        return hour.get();
    }
    public void setHour(String value) {
        hour.set(value);
    }
    public StringProperty hourProperty() {
        return hour;
    }

    public double getProd() {
        return prod.get();
    }
    public void setProd(double value) {
        prod.set(value);
    }
    public DoubleProperty prodProperty() {
        return prod;
    }

    public double getUsed() {
        return used.get();
    }
    public void setUsed(double value) {
        used.set(value);
    }
    public DoubleProperty usedProperty() {
        return used;
    }

    public double getGrid() {
        return grid.get();
    }
    public void setGrid(double value) {
        grid.set(value);
    }
    public DoubleProperty gridProperty() {
        return grid;
    }
}
