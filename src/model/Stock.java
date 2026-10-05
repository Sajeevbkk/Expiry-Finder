package model;

import database.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;

public class Stock {
    private long id;
    private long productID;
    private int batchNo;
    private int quantity;
    private LocalDateTime arrivalDate;
    private LocalDateTime expiryDate;

    public Stock() {
    }

    public Stock(long productID, int batchNo,
                 int quantity, LocalDateTime arrivalDate,
                 LocalDateTime expiryDate) {
        this.productID = productID;
        this.batchNo = batchNo;
        this.quantity = quantity;
        this.arrivalDate = arrivalDate;
        this.expiryDate = expiryDate;
    }

    public Stock(long id, long productID, int batchNo,
                 int quantity, LocalDateTime arrivalDate,
                 LocalDateTime expiryDate) {
        this.id = id;
        this.productID = productID;
        this.batchNo = batchNo;
        this.quantity = quantity;
        this.arrivalDate = arrivalDate;
        this.expiryDate = expiryDate;
    }

    public Stock(long productID, int batchNo,
                 int quantity, LocalDateTime arrivalDate,
                 LocalDate expiryDate) {
        this(productID, batchNo, quantity, arrivalDate, expiryDate != null ? expiryDate.atStartOfDay() : null);
    }

    public Stock(long id, long productID, int batchNo,
                 int quantity, LocalDateTime arrivalDate,
                 LocalDate expiryDate) {
        this(id, productID, batchNo, quantity, arrivalDate, expiryDate != null ? expiryDate.atStartOfDay() : null);
    }

    public Stock(long productID, int batchNo,
                 int quantity, String arrivalDateStr,
                 String expiryDateStr) {
        this(productID, batchNo, quantity, parseDateTime(arrivalDateStr), parseDateTime(expiryDateStr));
    }

    public Stock(long id, long productID, int batchNo,
                 int quantity, String arrivalDateStr,
                 String expiryDateStr) {
        this(id, productID, batchNo, quantity, parseDateTime(arrivalDateStr), parseDateTime(expiryDateStr));
    }

    // --- Date/Time Parsing and Formatting Helpers ---

    public static final DateTimeFormatter DB_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter USER_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    /**
     * Parses a date or date-time string into a LocalDateTime.
     * Supports:
     * - User date: "dd-MM-yyyy" or "d-M-yyyy", with hyphens or slashes
     * - ISO date: "yyyy-MM-dd" or "yyyy-M-d", with hyphens or slashes
     * - ISO date-time: "yyyy-MM-ddTHH:mm[:ss[.SSS]]"
     * - Space-separated date-time: "yyyy-MM-dd HH:mm[:ss]"
     */
    public static LocalDateTime parseDateTime(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        dateStr = dateStr.trim();
        try {
            // User format: dd-MM-yyyy (e.g. 30-09-2026 or 5-9-2026)
            if (dateStr.matches("^\\d{1,2}-\\d{1,2}-\\d{4}$")) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("d-M-yyyy");
                return LocalDate.parse(dateStr, dtf).atStartOfDay();
            }
            // User format with slashes: dd/MM/yyyy (e.g. 30/09/2026)
            if (dateStr.matches("^\\d{1,2}/\\d{1,2}/\\d{4}$")) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("d/M/yyyy");
                return LocalDate.parse(dateStr, dtf).atStartOfDay();
            }
            // User format with dots: dd.MM.yyyy (e.g. 30.09.2026 or 5.9.2026)
            if (dateStr.matches("^\\d{1,2}\\.\\d{1,2}\\.\\d{4}$")) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("d.M.yyyy");
                return LocalDate.parse(dateStr, dtf).atStartOfDay();
            }
            // ISO date format: yyyy-MM-dd
            if (dateStr.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$")) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-M-d");
                return LocalDate.parse(dateStr, dtf).atStartOfDay();
            }
            // ISO date format with slashes: yyyy/MM/dd
            if (dateStr.matches("^\\d{4}/\\d{1,2}/\\d{1,2}$")) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/M/d");
                return LocalDate.parse(dateStr, dtf).atStartOfDay();
            }

            // ISO date-time or space-separated date-time
            if (dateStr.contains(" ") && !dateStr.contains("T")) {
                dateStr = dateStr.replace(" ", "T");
            }
            return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException e) {
            System.err.println("Failed to parse date/time string: '" + dateStr + "' - " + e.getMessage());
            return null;
        }
    }

    /**
     * Formats a date for database storage without time (yyyy-MM-dd).
     */
    public static String formatDateForDb(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.toLocalDate().format(DB_DATE_FORMATTER);
    }

    public static String formatDateForDb(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(DB_DATE_FORMATTER);
    }

    /**
     * Formats a date for user display in dd-MM-yyyy format.
     */
    public static String formatDateForUser(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.toLocalDate().format(USER_DATE_FORMATTER);
    }

    public static String formatDateForUser(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(USER_DATE_FORMATTER);
    }

    /**
     * Legacy helper: formats without time for database storage (yyyy-MM-dd).
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return formatDateForDb(dateTime);
    }

    /**
     * Returns the arrival date formatted for database storage without time (yyyy-MM-dd).
     */
    public String getArrivalDateString() {
        if (arrivalDate == null) {
            arrivalDate = LocalDateTime.now();
        }
        return formatDateForDb(arrivalDate);
    }

    /**
     * Returns the expiry date formatted for database storage without time (yyyy-MM-dd).
     */
    public String getExpiryDateString() {
        return formatDateForDb(expiryDate);
    }

    /**
     * Returns the arrival date formatted for user display (dd-MM-yyyy).
     */
    public String getArrivalDateFormatted() {
        return formatDateForUser(arrivalDate);
    }

    /**
     * Returns the expiry date formatted for user display (dd-MM-yyyy).
     */
    public String getExpiryDateFormatted() {
        return formatDateForUser(expiryDate);
    }

    public String getArrivalDateFormatted(String pattern) {
        if (arrivalDate == null) return "";
        return arrivalDate.format(DateTimeFormatter.ofPattern(pattern));
    }

    public String getExpiryDateFormatted(String pattern) {
        if (expiryDate == null) return "";
        return expiryDate.format(DateTimeFormatter.ofPattern(pattern));
    }

    // --- Database Persistence Methods ---

    public boolean save() {
        return Database.saveStock(this);
    }

    public boolean delete() {
        return Database.deleteStock(this.id);
    }

    public static Stock load(long id) {
        return Database.getStockById(id);
    }

    public static List<Stock> loadAll() {
        return Database.getAllStocks();
    }

    public static List<Stock> loadByProductId(long productId) {
        return Database.getStocksByProductId(productId);
    }

    public static Stock fromResultSet(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        long productId = rs.getLong("product_id");
        int quantity = rs.getInt("quantity");
        int batchNo = rs.getInt("batchno");
        String arrivalStr = rs.getString("arrival_date");
        String expiryStr = rs.getString("expiry_date");

        LocalDateTime arrivalDate = parseDateTime(arrivalStr);
        LocalDateTime expiryDate = parseDateTime(expiryStr);

        return new Stock(id, productId, batchNo, quantity, arrivalDate, expiryDate);
    }

    // --- Getters and Setters ---

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getProductID() {
        return productID;
    }

    public void setProductID(long productID) {
        this.productID = productID;
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

    public LocalDate getArrivalLocalDate() {
        return arrivalDate != null ? arrivalDate.toLocalDate() : null;
    }

    public void setArrivalDate(LocalDateTime arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public void setArrivalDate(LocalDate arrivalDate) {
        this.arrivalDate = arrivalDate != null ? arrivalDate.atStartOfDay() : null;
    }

    public void setArrivalDate(String arrivalDateStr) {
        this.arrivalDate = parseDateTime(arrivalDateStr);
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public LocalDate getExpiryLocalDate() {
        return expiryDate != null ? expiryDate.toLocalDate() : null;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate != null ? expiryDate.atStartOfDay() : null;
    }

    public void setExpiryDate(String expiryDateStr) {
        this.expiryDate = parseDateTime(expiryDateStr);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stock stock = (Stock) o;
        return id == stock.id && productID == stock.productID && batchNo == stock.batchNo && quantity == stock.quantity && Objects.equals(arrivalDate, stock.arrivalDate) && Objects.equals(expiryDate, stock.expiryDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, productID, batchNo, quantity, arrivalDate, expiryDate);
    }

    @Override
    public String toString() {
        return "Stock{" +
                "id=" + id +
                ", productID=" + productID +
                ", batchNo=" + batchNo +
                ", quantity=" + quantity +
                ", arrivalDate=" + arrivalDate +
                ", expiryDate=" + expiryDate +
                '}';
    }
}
