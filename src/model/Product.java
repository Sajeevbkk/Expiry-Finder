package model;

import java.time.LocalDate;

public class Product {
    private int id;
    private String name;
    private String barcode;
    private LocalDate expiryDate;

    public Product() {
    }

    public Product(int id, String name, String barcode, LocalDate expiryDate) {
        this.id = id;
        this.name = name;
        this.barcode = barcode;
        this.expiryDate = expiryDate;
    }

    public Product(String name, String barcode, LocalDate expiryDate) {
        this.name = name;
        this.barcode = barcode;
        this.expiryDate = expiryDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }
}