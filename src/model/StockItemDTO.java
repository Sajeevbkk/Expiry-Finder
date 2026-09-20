package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Data Transfer Object representing a combined Stock + Product + Category row
 * for inventory and expiry tracking displays.
 */
public class StockItemDTO {
    private long stockId;
    private long productId;
    private String productName;
    private String barcode;
    private String categoryName;
    private double price;
    private int batchNo;
    private int quantity;
    private LocalDateTime arrivalDate;
    private LocalDateTime expiryDate;

    public StockItemDTO() {
    }

    public StockItemDTO(long stockId, long productId, String productName, String barcode,
                        String categoryName, double price, int batchNo, int quantity,
                        LocalDateTime arrivalDate, LocalDateTime expiryDate) {
        this.stockId = stockId;
        this.productId = productId;
        this.productName = productName;
        this.barcode = barcode;
        this.categoryName = categoryName;
        this.price = price;
        this.batchNo = batchNo;
        this.quantity = quantity;
        this.arrivalDate = arrivalDate;
        this.expiryDate = expiryDate;
    }

    public long getStockId() {
        return stockId;
    }

    public void setStockId(long stockId) {
        this.stockId = stockId;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName != null ? productName : "Unknown Product";
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBarcode() {
        return barcode != null ? barcode : "-";
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getCategoryName() {
        return categoryName != null ? categoryName : "Uncategorized";
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(int batchNo) {
        this.batchNo = batchNo;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(LocalDateTime arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public long getDaysUntilExpiry() {
        if (expiryDate == null) return Long.MAX_VALUE;
        return ChronoUnit.DAYS.between(LocalDate.now(), expiryDate.toLocalDate());
    }

    public String getStatus() {
        if (expiryDate == null) return "UNKNOWN";
        long days = getDaysUntilExpiry();
        if (days < 0) {
            return "EXPIRED";
        } else if (days <= 7) {
            return "CRITICAL (<7d)";
        } else if (days <= 30) {
            return "EXPIRING (<30d)";
        } else {
            return "GOOD";
        }
    }

    public String getArrivalDateFormatted() {
        if (arrivalDate == null) return "-";
        return arrivalDate.toLocalDate().toString();
    }

    public String getExpiryDateFormatted() {
        if (expiryDate == null) return "-";
        return expiryDate.toLocalDate().toString();
    }

    public double getTotalValue() {
        return price * quantity;
    }
}
