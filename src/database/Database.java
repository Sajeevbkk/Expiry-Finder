package database;

import model.Stock;

import java.io.File;
import java.sql.*;
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
                "    name TEXT NOT NULL" +
                ");";

        String productsSql = "CREATE TABLE IF NOT EXISTS products (" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    name TEXT NOT NULL," +
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
                    System.out.println("Database connection established and table initialized successfully.");
                }
            } else {
                System.err.println("Connection failed. Database not found.");
            }
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

    // --- Stock Operations ---

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
}
