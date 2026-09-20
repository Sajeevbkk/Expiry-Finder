package model;

import java.util.Objects;

public class Product {
    private long id;
    private String name;
    private String barcode;
    private double price;
    private long categoryID;
    private String categoryName;

    public Product() {
    }

    public Product(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public Product(String name, String barcode, double price, long categoryID) {
        this.name = name;
        this.barcode = barcode;
        this.price = price;
        this.categoryID = categoryID;
    }

    public Product(long id, String name, String barcode, double price, long categoryID) {
        this.id = id;
        this.name = name;
        this.barcode = barcode;
        this.price = price;
        this.categoryID = categoryID;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public long getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(long categoryID) {
        this.categoryID = categoryID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id && Double.compare(product.price, price) == 0 && categoryID == product.categoryID && Objects.equals(name, product.name) && Objects.equals(barcode, product.barcode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, barcode, price, categoryID);
    }

    @Override
    public String toString() {
        return name + (barcode != null && !barcode.isEmpty() ? " [" + barcode + "]" : "");
    }
}