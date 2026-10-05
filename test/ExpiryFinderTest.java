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

        Product product = new Product("Test Organic Milk", 4.25, cat.getId());

        // Insert product
        assertTrue("Insert product should succeed", Database.insertProduct(product));
        assertTrue("Product ID should be generated", product.getId() > 0);

        // Lookup by ID
        Product byId = Database.getProductById(product.getId());
        assertNotNull("Product should be found by ID", byId);
        assertEquals(product.getName(), byId.getName());
        assertEquals(4.25, byId.getPrice(), 0.001);

        // Update product
        byId.setName("Test Organic Whole Milk 1L");
        byId.setPrice(4.50);
        assertTrue("Update product should succeed", Database.updateProduct(byId));

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
        Product product = new Product("Greek Yogurt 500g", 3.20, cat.getId());
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

        // Verify date formatting for user display (dd-MM-yyyy)
        assertTrue("Arrival date should be formatted as dd-MM-yyyy",
                expiredDTO.getArrivalDateFormatted().matches("^\\d{2}-\\d{2}-\\d{4}$"));
        assertTrue("Expiry date should be formatted as dd-MM-yyyy",
                expiredDTO.getExpiryDateFormatted().matches("^\\d{2}-\\d{2}-\\d{4}$"));

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

        // Test user format: dd-MM-yyyy
        LocalDateTime dtUser = Stock.parseDateTime("15-10-2026");
        assertNotNull(dtUser);
        assertEquals(2026, dtUser.getYear());
        assertEquals(10, dtUser.getMonthValue());
        assertEquals(15, dtUser.getDayOfMonth());

        // Test user format with single digit day/month: d-M-yyyy
        LocalDateTime dtUserSingle = Stock.parseDateTime("5-9-2026");
        assertNotNull(dtUserSingle);
        assertEquals(2026, dtUserSingle.getYear());
        assertEquals(9, dtUserSingle.getMonthValue());
        assertEquals(5, dtUserSingle.getDayOfMonth());

        // Test user format with slashes: dd/MM/yyyy
        LocalDateTime dtSlashes = Stock.parseDateTime("25/12/2026");
        assertNotNull(dtSlashes);
        assertEquals(2026, dtSlashes.getYear());
        assertEquals(12, dtSlashes.getMonthValue());
        assertEquals(25, dtSlashes.getDayOfMonth());

        // Test user format with dots: dd.MM.yyyy
        LocalDateTime dtDots = Stock.parseDateTime("15.10.2026");
        assertNotNull(dtDots);
        assertEquals(2026, dtDots.getYear());
        assertEquals(10, dtDots.getMonthValue());
        assertEquals(15, dtDots.getDayOfMonth());

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

    @Test
    public void testDateFormattingAndDatabaseStorageWithoutTime() throws Exception {
        Category cat = Database.findOrCreateCategory("Test Date Department");
        Product prod = new Product("Date Test Item", 99.0, cat.getId());
        assertTrue(Database.insertProduct(prod));

        // Create stock with user format: dd-MM-yyyy
        Stock stock = new Stock(prod.getId(), 888, 10, "15-08-2026", "20-11-2026");
        assertTrue(stock.save());
        assertTrue(stock.getId() > 0);

        // 1. Verify that dates stored in the database have NO time component (exact YYYY-MM-DD)
        try (java.sql.Connection conn = Database.connect();
             java.sql.PreparedStatement pstmt = conn.prepareStatement("SELECT arrival_date, expiry_date FROM stock WHERE id = ?")) {
            pstmt.setLong(1, stock.getId());
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                assertTrue(rs.next());
                String rawArrival = rs.getString("arrival_date");
                String rawExpiry = rs.getString("expiry_date");

                assertEquals("Raw arrival_date in DB must be YYYY-MM-DD without time", "2026-08-15", rawArrival);
                assertEquals("Raw expiry_date in DB must be YYYY-MM-DD without time", "2026-11-20", rawExpiry);

                assertFalse("arrival_date in DB must not contain time indicator 'T'", rawArrival.contains("T"));
                assertFalse("arrival_date in DB must not contain spaces or time", rawArrival.contains(" "));
                assertFalse("arrival_date in DB must not contain colons", rawArrival.contains(":"));

                assertFalse("expiry_date in DB must not contain time indicator 'T'", rawExpiry.contains("T"));
                assertFalse("expiry_date in DB must not contain spaces or time", rawExpiry.contains(" "));
                assertFalse("expiry_date in DB must not contain colons", rawExpiry.contains(":"));

                assertEquals(10, rawArrival.length());
                assertEquals(10, rawExpiry.length());
            }
        }

        // 2. Verify that dates displayed to user are formatted as dd-MM-yyyy
        List<StockItemDTO> items = Database.searchStockItems("Date Test Item", null, "ALL");
        assertFalse(items.isEmpty());
        StockItemDTO dto = items.get(0);

        assertEquals("Arrival date shown to user must be dd-MM-yyyy", "15-08-2026", dto.getArrivalDateFormatted());
        assertEquals("Expiry date shown to user must be dd-MM-yyyy", "20-11-2026", dto.getExpiryDateFormatted());

        // Clean up
        stock.delete();
        Database.deleteProduct(prod.getId());
        Database.deleteCategory(cat.getId());
    }
}
