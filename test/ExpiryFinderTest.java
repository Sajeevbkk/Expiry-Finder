import database.Database;
import model.Category;
import model.DashboardStats;
import model.Product;
import model.Stock;
import model.StockItemDTO;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

public class ExpiryFinderTest {

    @BeforeClass
    public static void setup() {
        Database.initializeDatabase();
    }

    @Test
    public void testCategoriesOperations() {
        // 1. Verify default categories exist
        List<Category> categories = Database.getAllCategories();
        assertNotNull("Categories should not be null", categories);
        assertTrue("Should have seeded default categories", categories.size() >= 5);

        // 2. Insert new category
        Category testCat = new Category("Test Department " + System.currentTimeMillis());
        assertTrue("Insert category should succeed", Database.insertCategory(testCat));
        assertTrue("Category ID should be set", testCat.getId() > 0);

        // 3. Find category by name
        Category found = Database.findOrCreateCategory(testCat.getName());
        assertNotNull("Should find created category", found);
        assertEquals("Category name should match", testCat.getName(), found.getName());

        // 4. Update category
        testCat.setName("Updated Department " + System.currentTimeMillis());
        assertTrue("Update category should succeed", Database.updateCategory(testCat));

        // 5. Delete category
        assertTrue("Delete category should succeed", Database.deleteCategory(testCat.getId()));
    }

    @Test
    public void testProductOperations() {
        // Find or create test category
        Category cat = Database.findOrCreateCategory("Test Category");
        assertNotNull(cat);

        String barcode = "TEST_EAN_" + System.currentTimeMillis();
        Product product = new Product("Test Organic Milk", barcode, 4.25, cat.getId());

        // Insert product
        assertTrue("Insert product should succeed", Database.insertProduct(product));
        assertTrue("Product ID should be generated", product.getId() > 0);

        // Lookup by barcode
        Product byBarcode = Database.getProductByBarcode(barcode);
        assertNotNull("Product should be found by barcode", byBarcode);
        assertEquals(product.getName(), byBarcode.getName());
        assertEquals(4.25, byBarcode.getPrice(), 0.001);

        // Update product
        byBarcode.setName("Test Organic Whole Milk 1L");
        byBarcode.setPrice(4.50);
        assertTrue("Update product should succeed", Database.updateProduct(byBarcode));

        Product updated = Database.getProductById(product.getId());
        assertEquals("Test Organic Whole Milk 1L", updated.getName());
        assertEquals(4.50, updated.getPrice(), 0.001);

        // Clean up
        assertTrue("Delete product should succeed", Database.deleteProduct(product.getId()));
        assertNull("Product should no longer exist", Database.getProductById(product.getId()));
    }

    @Test
    public void testStockAndExpiryCalculations() {
        Category cat = Database.findOrCreateCategory("Dairy & Eggs");
        Product product = new Product("Greek Yogurt 500g", "BC_" + System.currentTimeMillis(), 3.20, cat.getId());
        assertTrue(Database.insertProduct(product));

        LocalDate today = LocalDate.now();

        // 1. Expired Stock Batch (-5 days)
        Stock expiredStock = new Stock(product.getId(), 201, 10,
                today.minusDays(20).toString(), today.minusDays(5).toString());
        assertTrue(expiredStock.save());
        assertTrue(expiredStock.getId() > 0);

        // 2. Critical Stock Batch (+3 days)
        Stock criticalStock = new Stock(product.getId(), 202, 15,
                today.minusDays(10).toString(), today.plusDays(3).toString());
        assertTrue(criticalStock.save());

        // 3. Expiring Soon Stock Batch (+20 days)
        Stock warningStock = new Stock(product.getId(), 203, 25,
                today.toString(), today.plusDays(20).toString());
        assertTrue(warningStock.save());

        // 4. Safe Stock Batch (+60 days)
        Stock safeStock = new Stock(product.getId(), 204, 30,
                today.toString(), today.plusDays(60).toString());
        assertTrue(safeStock.save());

        // Verify loaded stocks and status classification via DTO
        List<StockItemDTO> items = Database.searchStockItems("Greek Yogurt", null, "ALL");
        assertTrue("Should have 4 batches for Greek Yogurt", items.size() >= 4);

        StockItemDTO expiredDTO = items.stream().filter(i -> i.getBatchNo() == 201).findFirst().orElse(null);
        assertNotNull(expiredDTO);
        assertEquals("EXPIRED", expiredDTO.getStatus());
        assertTrue(expiredDTO.getDaysUntilExpiry() < 0);

        StockItemDTO criticalDTO = items.stream().filter(i -> i.getBatchNo() == 202).findFirst().orElse(null);
        assertNotNull(criticalDTO);
        assertEquals("CRITICAL (<7d)", criticalDTO.getStatus());
        assertTrue(criticalDTO.getDaysUntilExpiry() <= 7 && criticalDTO.getDaysUntilExpiry() >= 0);

        StockItemDTO warningDTO = items.stream().filter(i -> i.getBatchNo() == 203).findFirst().orElse(null);
        assertNotNull(warningDTO);
        assertEquals("EXPIRING (<30d)", warningDTO.getStatus());

        StockItemDTO safeDTO = items.stream().filter(i -> i.getBatchNo() == 204).findFirst().orElse(null);
        assertNotNull(safeDTO);
        assertEquals("GOOD", safeDTO.getStatus());

        // Test adjustStockQuantity
        assertTrue(Database.adjustStockQuantity(criticalStock.getId(), 12));
        Stock reloadedCritical = Stock.load(criticalStock.getId());
        assertEquals(12, reloadedCritical.getQuantity());

        // Test dashboard stats
        DashboardStats stats = Database.getDashboardStats();
        assertTrue("Expired batches should be at least 1", stats.getExpiredBatches() >= 1);
        assertTrue("Critical batches should be at least 1", stats.getExpiringWithin7Days() >= 1);
        assertTrue("Warning batches should be at least 1", stats.getExpiringWithin30Days() >= 1);
        assertTrue("Total units should be positive", stats.getTotalStockUnits() > 0);
        assertTrue("Total value should be positive", stats.getTotalInventoryValue() > 0);

        // Clean up
        expiredStock.delete();
        criticalStock.delete();
        warningStock.delete();
        safeStock.delete();
        Database.deleteProduct(product.getId());
    }

    @Test
    public void testDateParsingAndFormatting() {
        LocalDateTime dt1 = Stock.parseDateTime("2026-10-15");
        assertNotNull(dt1);
        assertEquals(2026, dt1.getYear());
        assertEquals(10, dt1.getMonthValue());
        assertEquals(15, dt1.getDayOfMonth());
        assertEquals(0, dt1.getHour());

        LocalDateTime dt2 = Stock.parseDateTime("2026-12-31 23:59:59");
        assertNotNull(dt2);
        assertEquals(23, dt2.getHour());
        assertEquals(59, dt2.getMinute());

        LocalDateTime dt3 = Stock.parseDateTime("2027-01-01T12:30:00");
        assertNotNull(dt3);
        assertEquals(2027, dt3.getYear());
        assertEquals(12, dt3.getHour());

        assertNull(Stock.parseDateTime(null));
        assertNull(Stock.parseDateTime(""));
        assertNull(Stock.parseDateTime("invalid-date-format"));
    }
}
