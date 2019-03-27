package com.assignment.whatstheweather.Models;

public class SoilTypes {
    private String soilType;
    private int soilId;

    public SoilTypes() {
    }

    public SoilTypes(String soilType) {
        this.soilType = soilType;
    }

    public SoilTypes(int soilId, String soilType) {
        this.soilType = soilType;
        this.soilId = soilId;
    }

    public String getSoilType() {
        return soilType;
    }

    public void setSoilType(String soilType) {
        this.soilType = soilType;
    }

    public int getSoilId() {
        return soilId;
    }

    public void setSoilId(int soilId) {
        this.soilId = soilId;
    }
}
