package database;

import model.Category;
import model.DashboardStats;
import model.Product;
import model.Stock;
import model.StockItemDTO;

import java.io.File;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Database {
    // Database URL points to the db folder and product.db file
    private static final String URL = "jdbc:sqlite:db/product.db";

    public static Connection connect() {
        Connection conn = null;
        try {
            // Ensure the db folder exists
            File dbDir = new File("db");
            if (!dbDir.exists()) {
                boolean created = dbDir.mkdirs();
                if (created) {
                    System.out.println("Created database directory: " + dbDir.getAbsolutePath());
                }
            }

            // Create a connection to the database (SQLite will create product.db if missing)
            conn = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
        }
        return conn;
    }

    public static void initializeDatabase() {
        String categoriesSql = "CREATE TABLE IF NOT EXISTS categories (" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    name TEXT NOT NULL UNIQUE" +
                ");";

        String productsSql = "CREATE TABLE IF NOT EXISTS products (" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    name TEXT NOT NULL," +
                "    barcode TEXT," +
                "    price REAL NOT NULL," +
                "    category_id INTEGER," +
                "    FOREIGN KEY (category_id) REFERENCES categories(id)" +
                ");";

        String stockSql = "CREATE TABLE IF NOT EXISTS stock (" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    product_id INTEGER," +
                "    quantity INTEGER NOT NULL," +
                "    batchno INTEGER NOT NULL," +
                "    arrival_date TEXT NOT NULL CHECK (" +
                "        arrival_date GLOB '[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]*'" +
                "    )," +
                "    expiry_date TEXT NOT NULL CHECK (" +
                "        expiry_date GLOB '[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]*'" +
                "    )," +
                "    FOREIGN KEY (product_id) REFERENCES products(id)" +
                ");";

        try (Connection conn = connect()) {
            if (conn != null) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(categoriesSql);
                    stmt.execute(productsSql);
                    stmt.execute(stockSql);
                }

                // Check if barcode column exists in products (schema migration safety)
                try (ResultSet rs = conn.getMetaData().getColumns(null, null, "products", "barcode")) {
                    if (!rs.next()) {
                        try (Statement alterStmt = conn.createStatement()) {
                            alterStmt.execute("ALTER TABLE products ADD COLUMN barcode TEXT;");
                        }
                    }
                }

                // Seed default categories if table is empty
                seedDefaultCategories(conn);

                System.out.println("Database connection established and tables initialized successfully.");
            } else {
                System.err.println("Connection failed. Database not found.");
            }
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

    private static void seedDefaultCategories(Connection conn) {
        String countSql = "SELECT COUNT(*) FROM categories";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(countSql)) {
            if (rs.next() && rs.getInt(1) == 0) {
                String[] defaults = {
                        "Dairy & Eggs",
                        "Bakery & Bread",
                        "Beverages & Drinks",
                        "Snacks & Sweets",
                        "Canned & Packaged Foods",
                        "Fresh Produce & Fruits",
                        "Meat & Seafood",
                        "Personal Care & Medicine"
                };
                String insertSql = "INSERT INTO categories (name) VALUES (?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    for (String cat : defaults) {
                        pstmt.setString(1, cat);
                        pstmt.addBatch();
                    }
                    pstmt.executeBatch();
                }
            }
        } catch (SQLException e) {
            System.err.println("Note: Default categories check/seed: " + e.getMessage());
        }
    }

    // ==========================================
    // --- Category Operations ---
    // ==========================================

    public static boolean insertCategory(Category category) {
        String sql = "INSERT INTO categories (name) VALUES (?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, category.getName());
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        category.setId(keys.getLong(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error inserting category: " + e.getMessage());
        }
        return false;
    }

    public static boolean updateCategory(Category category) {
        String sql = "UPDATE categories SET name = ? WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, category.getName());
            pstmt.setLong(2, category.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating category: " + e.getMessage());
        }
        return false;
    }

    public static boolean deleteCategory(long id) {
        String sql = "DELETE FROM categories WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting category: " + e.getMessage());
        }
        return false;
    }

    public static Category getCategoryById(long id) {
        String sql = "SELECT id, name FROM categories WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Category(rs.getLong("id"), rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching category: " + e.getMessage());
        }
        return null;
    }

    public static List<Category> getAllCategories() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT id, name FROM categories ORDER BY name ASC";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Category(rs.getLong("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching categories: " + e.getMessage());
        }
        return list;
    }

    public static Category findOrCreateCategory(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        name = name.trim();
        String sql = "SELECT id, name FROM categories WHERE LOWER(name) = LOWER(?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Category(rs.getLong("id"), rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding category: " + e.getMessage());
        }

        Category created = new Category(name);
        if (insertCategory(created)) {
            return created;
        }
        return null;
    }

    // ==========================================
    // --- Product Operations ---
    // ==========================================

    public static boolean insertProduct(Product product) {
        String sql = "INSERT INTO products (name, barcode, price, category_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getBarcode());
            pstmt.setDouble(3, product.getPrice());
            if (product.getCategoryID() > 0) {
                pstmt.setLong(4, product.getCategoryID());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }

            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        product.setId(keys.getLong(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error inserting product: " + e.getMessage());
        }
        return false;
    }

    public static boolean updateProduct(Product product) {
        String sql = "UPDATE products SET name = ?, barcode = ?, price = ?, category_id = ? WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getBarcode());
            pstmt.setDouble(3, product.getPrice());
            if (product.getCategoryID() > 0) {
                pstmt.setLong(4, product.getCategoryID());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }
            pstmt.setLong(5, product.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating product: " + e.getMessage());
        }
        return false;
    }

    public static boolean deleteProduct(long id) {
        // First delete associated stock records
        String deleteStockSql = "DELETE FROM stock WHERE product_id = ?";
        String deleteProductSql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = connect()) {
            conn.setAutoCommit(false);
            try (PreparedStatement sPstmt = conn.prepareStatement(deleteStockSql);
                 PreparedStatement pPstmt = conn.prepareStatement(deleteProductSql)) {
                sPstmt.setLong(1, id);
                sPstmt.executeUpdate();

                pPstmt.setLong(1, id);
                int affected = pPstmt.executeUpdate();
                conn.commit();
                return affected > 0;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting product and stock: " + e.getMessage());
        }
        return false;
    }

    public static Product getProductById(long id) {
        String sql = "SELECT p.id, p.name, p.barcode, p.price, p.category_id, c.name AS category_name " +
                "FROM products p LEFT JOIN categories c ON p.category_id = c.id WHERE p.id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Product p = new Product(rs.getLong("id"), rs.getString("name"),
                            rs.getString("barcode"), rs.getDouble("price"), rs.getLong("category_id"));
                    p.setCategoryName(rs.getString("category_name"));
                    return p;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching product by ID: " + e.getMessage());
        }
        return null;
    }

    public static Product getProductByBarcode(String barcode) {
        if (barcode == null || barcode.trim().isEmpty()) {
            return null;
        }
        String sql = "SELECT p.id, p.name, p.barcode, p.price, p.category_id, c.name AS category_name " +
                "FROM products p LEFT JOIN categories c ON p.category_id = c.id WHERE TRIM(p.barcode) = TRIM(?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, barcode.trim());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Product p = new Product(rs.getLong("id"), rs.getString("name"),
                            rs.getString("barcode"), rs.getDouble("price"), rs.getLong("category_id"));
                    p.setCategoryName(rs.getString("category_name"));
                    return p;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching product by barcode: " + e.getMessage());
        }
        return null;
    }

    public static List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.id, p.name, p.barcode, p.price, p.category_id, c.name AS category_name " +
                "FROM products p LEFT JOIN categories c ON p.category_id = c.id ORDER BY p.name ASC";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Product p = new Product(rs.getLong("id"), rs.getString("name"),
                        rs.getString("barcode"), rs.getDouble("price"), rs.getLong("category_id"));
                p.setCategoryName(rs.getString("category_name"));
                list.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all products: " + e.getMessage());
        }
        return list;
    }

    // ==========================================
    // --- Stock Operations ---
    // ==========================================

    public static boolean insertStock(Stock stock) {
        String sql = "INSERT INTO stock (product_id, quantity, batchno, arrival_date, expiry_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setLong(1, stock.getProductID());
            pstmt.setInt(2, stock.getQuantity());
            pstmt.setInt(3, stock.getBatchNo());
            pstmt.setString(4, stock.getArrivalDateString());
            pstmt.setString(5, stock.getExpiryDateString());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        stock.setId(generatedKeys.getLong(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error inserting stock: " + e.getMessage());
        }
        return false;
    }

    public static boolean updateStock(Stock stock) {
        String sql = "UPDATE stock SET product_id = ?, quantity = ?, batchno = ?, arrival_date = ?, expiry_date = ? WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, stock.getProductID());
            pstmt.setInt(2, stock.getQuantity());
            pstmt.setInt(3, stock.getBatchNo());
            pstmt.setString(4, stock.getArrivalDateString());
            pstmt.setString(5, stock.getExpiryDateString());
            pstmt.setLong(6, stock.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating stock: " + e.getMessage());
        }
        return false;
    }

    public static boolean saveStock(Stock stock) {
        if (stock.getId() > 0) {
            return updateStock(stock);
        } else {
            return insertStock(stock);
        }
    }

    public static Stock getStockById(long id) {
        String sql = "SELECT id, product_id, quantity, batchno, arrival_date, expiry_date FROM stock WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Stock.fromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading stock by ID: " + e.getMessage());
        }
        return null;
    }

    public static List<Stock> getStocksByProductId(long productId) {
        List<Stock> list = new ArrayList<>();
        String sql = "SELECT id, product_id, quantity, batchno, arrival_date, expiry_date FROM stock WHERE product_id = ? ORDER BY expiry_date ASC";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, productId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(Stock.fromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading stocks by product ID: " + e.getMessage());
        }
        return list;
    }

    public static List<Stock> getAllStocks() {
        List<Stock> list = new ArrayList<>();
        String sql = "SELECT id, product_id, quantity, batchno, arrival_date, expiry_date FROM stock ORDER BY id ASC";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(Stock.fromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error loading all stocks: " + e.getMessage());
        }
        return list;
    }

    public static boolean deleteStock(long id) {
        String sql = "DELETE FROM stock WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting stock: " + e.getMessage());
        }
        return false;
    }

    public static boolean adjustStockQuantity(long stockId, int newQuantity) {
        if (newQuantity <= 0) {
            return deleteStock(stockId);
        }
        String sql = "UPDATE stock SET quantity = ? WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newQuantity);
            pstmt.setLong(2, stockId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adjusting stock quantity: " + e.getMessage());
        }
        return false;
    }

    // ==========================================
    // --- Unified StockItemDTO & Search Queries ---
    // ==========================================

    public static List<StockItemDTO> getAllStockItems() {
        return searchStockItems(null, null, null);
    }

    public static List<StockItemDTO> searchStockItems(String keyword, Long categoryId, String statusFilter) {
        List<StockItemDTO> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT s.id AS stock_id, s.product_id, p.name AS product_name, p.barcode, " +
                "c.name AS category_name, p.price, s.batchno, s.quantity, " +
                "s.arrival_date, s.expiry_date " +
                "FROM stock s " +
                "JOIN products p ON s.product_id = p.id " +
                "LEFT JOIN categories c ON p.category_id = c.id " +
                "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (LOWER(p.name) LIKE ? OR LOWER(p.barcode) LIKE ? OR CAST(s.batchno AS TEXT) LIKE ?) ");
            String term = "%" + keyword.trim().toLowerCase() + "%";
            params.add(term);
            params.add(term);
            params.add(term);
        }

        if (categoryId != null && categoryId > 0) {
            sql.append("AND p.category_id = ? ");
            params.add(categoryId);
        }

        sql.append("ORDER BY s.expiry_date ASC");

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    long stockId = rs.getLong("stock_id");
                    long productId = rs.getLong("product_id");
                    String productName = rs.getString("product_name");
                    String barcode = rs.getString("barcode");
                    String categoryName = rs.getString("category_name");
                    double price = rs.getDouble("price");
                    int batchNo = rs.getInt("batchno");
                    int quantity = rs.getInt("quantity");
                    LocalDateTime arrivalDate = Stock.parseDateTime(rs.getString("arrival_date"));
                    LocalDateTime expiryDate = Stock.parseDateTime(rs.getString("expiry_date"));

                    StockItemDTO dto = new StockItemDTO(stockId, productId, productName, barcode,
                            categoryName, price, batchNo, quantity, arrivalDate, expiryDate);

                    if (statusFilter != null && !statusFilter.equalsIgnoreCase("ALL")) {
                        long days = dto.getDaysUntilExpiry();
                        if (statusFilter.equalsIgnoreCase("EXPIRED") && days >= 0) continue;
                        if (statusFilter.equalsIgnoreCase("7DAYS") && (days < 0 || days > 7)) continue;
                        if (statusFilter.equalsIgnoreCase("30DAYS") && (days < 0 || days > 30)) continue;
                        if (statusFilter.equalsIgnoreCase("GOOD") && days <= 30) continue;
                    }

                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching stock items: " + e.getMessage());
        }
        return list;
    }

    public static DashboardStats getDashboardStats() {
        DashboardStats stats = new DashboardStats();
        List<StockItemDTO> all = getAllStockItems();

        int totalUnits = 0;
        int expired = 0;
        int within7 = 0;
        int within30 = 0;
        double totalValue = 0;

        for (StockItemDTO item : all) {
            totalUnits += item.getQuantity();
            totalValue += item.getTotalValue();
            long days = item.getDaysUntilExpiry();
            if (days < 0) {
                expired++;
            } else if (days <= 7) {
                within7++;
            } else if (days <= 30) {
                within30++;
            }
        }

        stats.setTotalStockUnits(totalUnits);
        stats.setExpiredBatches(expired);
        stats.setExpiringWithin7Days(within7);
        stats.setExpiringWithin30Days(within30);
        stats.setTotalInventoryValue(totalValue);

        // Count distinct products
        String prodCountSql = "SELECT COUNT(*) FROM products";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(prodCountSql)) {
            if (rs.next()) {
                stats.setTotalProducts(rs.getInt(1));
            }
        } catch (SQLException e) {
            System.err.println("Error counting products: " + e.getMessage());
        }

        return stats;
    }
}
