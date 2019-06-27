package com.soniya.sellersapp;

public class ProfileStats {

    private long totalWorth;
    private long soldWorth;
    private int availableInventory;
    private int soldInventory;
    private int avgSellperMonth;
    private long avgSellWorthMonth;

    public long getTotalWorth() {
        return totalWorth;
    }

    public void setTotalWorth(long totalWorth) {
        this.totalWorth = totalWorth;
    }

    public long getSoldWorth() {
        return soldWorth;
    }

    public void setSoldWorth(long soldWorth) {
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

    public long getAvgSellThisMonth() {
        return avgSellWorthMonth;
    }

    public void setAvgSellThisMonth(long avgSellWorthMonth) {
        this.avgSellWorthMonth = avgSellWorthMonth;
    }

    public int getSoldInventory() {
        return soldInventory;
    }

    public void setSoldInventory(int soldInventory) {
        this.soldInventory = soldInventory;
    }

    public ProfileStats(){
        this.totalWorth =0;
        this.soldWorth =0;
        this.availableInventory =0;
        this.avgSellperMonth =0;
        this.avgSellWorthMonth =0;
        this.soldInventory =0;
    }
}
