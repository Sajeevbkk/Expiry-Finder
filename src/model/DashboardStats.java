package model;

public class DashboardStats {
    private int totalProducts;
    private int totalStockUnits;
    private int expiredBatches;
    private int expiringWithin7Days;
    private int expiringWithin30Days;
    private double totalInventoryValue;

    public DashboardStats() {
    }

    public DashboardStats(int totalProducts, int totalStockUnits, int expiredBatches,
                          int expiringWithin7Days, int expiringWithin30Days, double totalInventoryValue) {
        this.totalProducts = totalProducts;
        this.totalStockUnits = totalStockUnits;
        this.expiredBatches = expiredBatches;
        this.expiringWithin7Days = expiringWithin7Days;
        this.expiringWithin30Days = expiringWithin30Days;
        this.totalInventoryValue = totalInventoryValue;
    }

    public int getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(int totalProducts) {
        this.totalProducts = totalProducts;
    }

    public int getTotalStockUnits() {
        return totalStockUnits;
    }

    public void setTotalStockUnits(int totalStockUnits) {
        this.totalStockUnits = totalStockUnits;
    }

    public int getExpiredBatches() {
        return expiredBatches;
    }

    public void setExpiredBatches(int expiredBatches) {
        this.expiredBatches = expiredBatches;
    }

    public int getExpiringWithin7Days() {
        return expiringWithin7Days;
    }

    public void setExpiringWithin7Days(int expiringWithin7Days) {
        this.expiringWithin7Days = expiringWithin7Days;
    }

    public int getExpiringWithin30Days() {
        return expiringWithin30Days;
    }

    public void setExpiringWithin30Days(int expiringWithin30Days) {
        this.expiringWithin30Days = expiringWithin30Days;
    }

    public double getTotalInventoryValue() {
        return totalInventoryValue;
    }

    public void setTotalInventoryValue(double totalInventoryValue) {
        this.totalInventoryValue = totalInventoryValue;
    }
}
