package com.soniya.sellersapp;

public class ProfileStats {

    private double totalWorth;
    private double soldWorth;
    private int availableInventory;
    private int soldInventory;
    private int avgSellperMonth;
    private double avgSellWorthMonth;
    private String worthUnit;

    public double getTotalWorth() {
        return totalWorth;
    }

    public void setTotalWorth(double totalWorth) {
        this.totalWorth = totalWorth;
    }

    public double getSoldWorth() {
        return soldWorth;
    }

    public void setSoldWorth(double soldWorth) {
        this.soldWorth = soldWorth;
    }

    public int getAvailableInventory() {
        return availableInventory;
    }

    public void setAvailableInventory(int availableInventory) {
        this.availableInventory = availableInventory;
    }

    public int getAvgSellperMonth() {
        return avgSellperMonth;
    }

    public void setAvgSellperMonth(int avgSellperMonth) {
        this.avgSellperMonth = avgSellperMonth;
    }

    public double getAvgSellThisMonth() {
        return avgSellWorthMonth;
    }

    public void setAvgSellThisMonth(double avgSellWorthMonth) {
        this.avgSellWorthMonth = avgSellWorthMonth;
    }

    public int getSoldInventory() {
        return soldInventory;
    }

    public void setSoldInventory(int soldInventory) {
        this.soldInventory = soldInventory;
    }

    public double getAvgSellWorthMonth() {
        return avgSellWorthMonth;
    }

    public void setAvgSellWorthMonth(double avgSellWorthMonth) {
        this.avgSellWorthMonth = avgSellWorthMonth;
    }

    public String getWorthUnit() {
        return worthUnit;
    }

    public void setWorthUnit(String worthUnit) {
        this.worthUnit = worthUnit;
    }

    public ProfileStats(){
        this.totalWorth =0;
        this.soldWorth =0;
        this.availableInventory =0;
        this.avgSellperMonth =0;
        this.avgSellWorthMonth =0;
        this.soldInventory =0;
        this.worthUnit="";
    }
}
