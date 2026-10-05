package database;

import model.Category;
import model.DashboardStats;
import model.Product;
import model.Stock;
import model.StockItemDTO;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Database {
    private static String host = "localhost";
    private static int port = 3306;
    private static String dbName = "expiry_finder";
    private static String user = "root";
    private static String password = "";
    private static String extraParams = "useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&createDatabaseIfNotExist=true";

    static {
        loadConfiguration();
    }

    public static void loadConfiguration() {
        Properties props = new Properties();

        // 1. Try loading from working directory file: db.properties
        File file = new File("db.properties");
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                props.load(fis);
            } catch (IOException e) {
                System.err.println("Notice: Could not load db.properties from file system: " + e.getMessage());
            }
        } else {
            // 2. Try loading from classpath
            try (InputStream is = Database.class.getResourceAsStream("/db.properties")) {
                if (is != null) {
                    props.load(is);
                }
            } catch (IOException e) {
                System.err.println("Notice: Could not load db.properties from classpath: " + e.getMessage());
            }
        }

        // Apply properties or system properties / environment overrides
        host = getPropOrEnv(props, "db.host", "DB_HOST", "localhost");
        String portStr = getPropOrEnv(props, "db.port", "DB_PORT", "3306");
        try {
            port = Integer.parseInt(portStr.trim());
        } catch (NumberFormatException ignored) {
            port = 3306;
        }
        dbName = getPropOrEnv(props, "db.name", "DB_NAME", "expiry_finder");
        user = getPropOrEnv(props, "db.user", "DB_USER", "root");
        password = getPropOrEnv(props, "db.password", "DB_PASSWORD", "");
        extraParams = getPropOrEnv(props, "db.params", "DB_PARAMS",
                "useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&createDatabaseIfNotExist=true");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
        }
    }

    private static String getPropOrEnv(Properties props, String propKey, String envKey, String defVal) {
        String val = System.getProperty(propKey);
        if (val != null && !val.trim().isEmpty()) return val.trim();
        val = System.getenv(envKey);
        if (val != null && !val.trim().isEmpty()) return val.trim();
        val = props.getProperty(propKey);
        if (val != null) return val.trim();
        return defVal;
    }

    private static volatile Process localServerProcess = null;

    public static boolean isConnected() {
        try (Connection conn = connect()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    private static boolean isHostLocal(String h) {
        return "localhost".equalsIgnoreCase(h) || "127.0.0.1".equals(h) || "::1".equals(h);
    }

    private static boolean isPortReachable(String h, int p) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(h, p), 800);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static synchronized boolean tryStartLocalServer() {
        if (!isHostLocal(host)) {
            return false;
        }
        if (isPortReachable(host, port)) {
            return true;
        }

        File mysqldExe = findMysqldExecutable();
        if (mysqldExe == null || !mysqldExe.exists()) {
            return false;
        }

        File dataDir = findMysqlDataDir();
        if (dataDir == null || !dataDir.exists()) {
            return false;
        }

        try {
            System.out.println("Starting local MySQL server using " + mysqldExe.getAbsolutePath() + " and data directory " + dataDir.getAbsolutePath() + "...");
            ProcessBuilder pb = new ProcessBuilder(
                    mysqldExe.getAbsolutePath(),
                    "--console",
                    "--datadir=" + dataDir.getAbsolutePath(),
                    "--port=" + port
            );
            pb.redirectErrorStream(true);
            localServerProcess = pb.start();

            // Register shutdown hook to cleanly terminate the background mysqld when JVM exits
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (localServerProcess != null && localServerProcess.isAlive()) {
                    localServerProcess.destroy();
                }
            }));

            // Wait up to 6 seconds for MySQL server to start accepting connections
            for (int i = 0; i < 30; i++) {
                if (isPortReachable(host, port)) {
                    System.out.println("Local MySQL server started successfully on port " + port + ".");
                    return true;
                }
                Thread.sleep(200);
            }
        } catch (Exception ex) {
            System.err.println("Notice: Could not automatically start local MySQL server: " + ex.getMessage());
        }

        return isPortReachable(host, port);
    }

    private static File findMysqldExecutable() {
        String[] candidatePaths = {
                "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysqld.exe",
                "C:\\Program Files\\MySQL\\MySQL Server 8.4\\bin\\mysqld.exe",
                "C:\\Program Files\\MySQL\\MySQL Server 8.1\\bin\\mysqld.exe",
                "C:\\Program Files\\MySQL\\MySQL Server 8.2\\bin\\mysqld.exe",
                "C:\\Program Files\\MySQL\\MySQL Server 8.3\\bin\\mysqld.exe",
                "C:\\Program Files (x86)\\MySQL\\MySQL Server 8.0\\bin\\mysqld.exe",
                "C:\\xampp\\mysql\\bin\\mysqld.exe"
        };
        for (String p : candidatePaths) {
            File f = new File(p);
            if (f.exists() && f.canExecute()) {
                return f;
            }
        }

        String mysqlHome = System.getenv("MYSQL_HOME");
        if (mysqlHome != null) {
            File f = new File(mysqlHome, "bin/mysqld.exe");
            if (f.exists()) return f;
        }

        return null;
    }

    private static File findMysqlDataDir() {
        String[] candidateDirs = {
                "mysql_data",
                "../mysql_data",
                "C:\\Users\\sajee\\Desktop\\Expiry Finder (MySQL)\\mysql_data"
        };
        for (String d : candidateDirs) {
            File dir = new File(d);
            if (dir.exists() && dir.isDirectory()) {
                return dir;
            }
        }
        return null;
    }

    public static Connection connect() {
        // Attempt starting local MySQL if running on localhost and not currently listening
        if (isHostLocal(host) && !isPortReachable(host, port)) {
            tryStartLocalServer();
        }

        Connection conn = null;
        String url = "jdbc:mysql://" + host + ":" + port + "/" + dbName + "?" + extraParams;
        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            // If database not found, attempt creating database schema first
            if (e.getErrorCode() == 1049 || (e.getMessage() != null && e.getMessage().toLowerCase().contains("unknown database"))) {
                tryCreateDatabase();
                try {
                    conn = DriverManager.getConnection(url, user, password);
                } catch (SQLException retryEx) {
                    System.err.println("Error connecting to database after creation attempt: " + retryEx.getMessage());
                }
            } else {
                System.err.println("Error connecting to MySQL database (" + url + "): " + e.getMessage());
            }
        }
        return conn;
    }

    public static void tryCreateDatabase() {
        String serverUrl = "jdbc:mysql://" + host + ":" + port + "/?" + extraParams;
        try (Connection rootConn = DriverManager.getConnection(serverUrl, user, password)) {
            if (rootConn != null) {
                try (Statement stmt = rootConn.createStatement()) {
                    stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS `" + dbName + "` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
                    System.out.println("MySQL Database `" + dbName + "` ensured/created successfully.");
                }
            }
        } catch (SQLException ex) {
            System.err.println("Failed to automatically create database `" + dbName + "`: " + ex.getMessage());
        }
    }

    public static void initializeDatabase() {
        if (isHostLocal(host) && !isPortReachable(host, port)) {
            tryStartLocalServer();
        }
        // Ensure database exists on server
        tryCreateDatabase();

        String categoriesSql = "CREATE TABLE IF NOT EXISTS categories (" +
                "    id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "    name VARCHAR(255) NOT NULL UNIQUE" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";

        String productsSql = "CREATE TABLE IF NOT EXISTS products (" +
                "    id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "    name VARCHAR(255) NOT NULL," +
                "    price DOUBLE NOT NULL," +
                "    category_id BIGINT," +
                "    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";

        String stockSql = "CREATE TABLE IF NOT EXISTS stock (" +
                "    id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "    product_id BIGINT NOT NULL," +
                "    quantity INT NOT NULL," +
                "    batchno INT NOT NULL," +
                "    arrival_date VARCHAR(10) NOT NULL," +
                "    expiry_date VARCHAR(10) NOT NULL," +
                "    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";

        try (Connection conn = connect()) {
            if (conn != null) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(categoriesSql);
                    stmt.execute(productsSql);
                    stmt.execute(stockSql);

                    // Ensure any existing legacy timestamps with time in stock table are cleaned up to date-only
                    stmt.execute("UPDATE stock SET arrival_date = SUBSTR(arrival_date, 1, 10) WHERE LENGTH(arrival_date) > 10;");
                    stmt.execute("UPDATE stock SET expiry_date = SUBSTR(expiry_date, 1, 10) WHERE LENGTH(expiry_date) > 10;");

                    // Seed default categories if table is empty
                    seedDefaultCategoriesIfEmpty(conn);
                }

                System.out.println("Database connection established and tables initialized successfully.");
            } else {
                System.err.println("Connection failed. MySQL database not reachable.");
            }
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

    private static void seedDefaultCategoriesIfEmpty(Connection conn) {
        if (conn == null) return;
        String countSql = "SELECT COUNT(*) FROM categories";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(countSql)) {
            if (rs.next() && rs.getInt(1) == 0) {
                String[] defaultCategories = {
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
                    for (String cat : defaultCategories) {
                        pstmt.setString(1, cat);
                        pstmt.executeUpdate();
                    }
                }
                System.out.println("Default categories seeded successfully.");
            }
        } catch (SQLException e) {
            System.err.println("Error checking/seeding categories: " + e.getMessage());
        }
    }

    // ==========================================
    // --- Category Operations ---
    // ==========================================

    public static boolean insertCategory(Category category) {
        if (category == null || category.getName() == null || category.getName().trim().isEmpty()) {
            return false;
        }
        String sql = "INSERT INTO categories (name) VALUES (?)";
        try (Connection conn = connect()) {
            if (conn == null) return false;
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, category.getName().trim());
                int affected = pstmt.executeUpdate();
                if (affected > 0) {
                    try (ResultSet keys = pstmt.getGeneratedKeys()) {
                        if (keys.next()) {
                            category.setId(keys.getLong(1));
                        }
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting category: " + e.getMessage());
        }
        return false;
    }

    public static boolean updateCategory(Category category) {
        if (category == null) return false;
        String sql = "UPDATE categories SET name = ? WHERE id = ?";
        try (Connection conn = connect()) {
            if (conn == null) return false;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, category.getName());
                pstmt.setLong(2, category.getId());
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error updating category: " + e.getMessage());
        }
        return false;
    }

    public static boolean deleteCategory(long id) {
        String sql = "DELETE FROM categories WHERE id = ?";
        try (Connection conn = connect()) {
            if (conn == null) return false;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setLong(1, id);
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting category: " + e.getMessage());
        }
        return false;
    }

    public static Category getCategoryById(long id) {
        String sql = "SELECT id, name FROM categories WHERE id = ?";
        try (Connection conn = connect()) {
            if (conn == null) return null;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setLong(1, id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return new Category(rs.getLong("id"), rs.getString("name"));
                    }
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
        try (Connection conn = connect()) {
            if (conn == null) return list;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    list.add(new Category(rs.getLong("id"), rs.getString("name")));
                }
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
        try (Connection conn = connect()) {
            if (conn == null) return null;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return new Category(rs.getLong("id"), rs.getString("name"));
                    }
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
        if (product == null) return false;
        String sql = "INSERT INTO products (name, price, category_id) VALUES (?, ?, ?)";
        try (Connection conn = connect()) {
            if (conn == null) return false;
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, product.getName());
                pstmt.setDouble(2, product.getPrice());
                if (product.getCategoryID() > 0) {
                    pstmt.setLong(3, product.getCategoryID());
                } else {
                    pstmt.setNull(3, Types.BIGINT);
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
            }
        } catch (SQLException e) {
            System.err.println("Error inserting product: " + e.getMessage());
        }
        return false;
    }

    public static boolean updateProduct(Product product) {
        if (product == null) return false;
        String sql = "UPDATE products SET name = ?, price = ?, category_id = ? WHERE id = ?";
        try (Connection conn = connect()) {
            if (conn == null) return false;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, product.getName());
                pstmt.setDouble(2, product.getPrice());
                if (product.getCategoryID() > 0) {
                    pstmt.setLong(3, product.getCategoryID());
                } else {
                    pstmt.setNull(3, Types.BIGINT);
                }
                pstmt.setLong(4, product.getId());
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error updating product: " + e.getMessage());
        }
        return false;
    }

    public static boolean deleteProduct(long id) {
        String deleteStockSql = "DELETE FROM stock WHERE product_id = ?";
        String deleteProductSql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = connect()) {
            if (conn == null) return false;
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
        String sql = "SELECT p.id, p.name, p.price, p.category_id, c.name AS category_name " +
                "FROM products p LEFT JOIN categories c ON p.category_id = c.id WHERE p.id = ?";
        try (Connection conn = connect()) {
            if (conn == null) return null;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setLong(1, id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        Product p = new Product(rs.getLong("id"), rs.getString("name"),
                                rs.getDouble("price"), rs.getLong("category_id"));
                        p.setCategoryName(rs.getString("category_name"));
                        return p;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching product by ID: " + e.getMessage());
        }
        return null;
    }

    public static List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.id, p.name, p.price, p.category_id, c.name AS category_name " +
                "FROM products p LEFT JOIN categories c ON p.category_id = c.id ORDER BY p.name ASC";
        try (Connection conn = connect()) {
            if (conn == null) return list;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    Product p = new Product(rs.getLong("id"), rs.getString("name"),
                            rs.getDouble("price"), rs.getLong("category_id"));
                    p.setCategoryName(rs.getString("category_name"));
                    list.add(p);
                }
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
        if (stock == null) return false;
        String sql = "INSERT INTO stock (product_id, quantity, batchno, arrival_date, expiry_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = connect()) {
            if (conn == null) return false;
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                String arrival = stock.getArrivalDateString();
                if (arrival != null && arrival.length() > 10) {
                    arrival = arrival.substring(0, 10);
                }
                String expiry = stock.getExpiryDateString();
                if (expiry != null && expiry.length() > 10) {
                    expiry = expiry.substring(0, 10);
                }
                if (expiry == null) {
                    System.err.println("Cannot insert stock: Expiry date is required");
                    return false;
                }

                pstmt.setLong(1, stock.getProductID());
                pstmt.setInt(2, stock.getQuantity());
                pstmt.setInt(3, stock.getBatchNo());
                pstmt.setString(4, arrival != null ? arrival : LocalDate.now().toString());
                pstmt.setString(5, expiry);

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            stock.setId(generatedKeys.getLong(1));
                        }
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting stock: " + e.getMessage());
        }
        return false;
    }

    public static boolean updateStock(Stock stock) {
        if (stock == null) return false;
        String sql = "UPDATE stock SET product_id = ?, quantity = ?, batchno = ?, arrival_date = ?, expiry_date = ? WHERE id = ?";
        try (Connection conn = connect()) {
            if (conn == null) return false;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                String arrival = stock.getArrivalDateString();
                if (arrival != null && arrival.length() > 10) {
                    arrival = arrival.substring(0, 10);
                }
                String expiry = stock.getExpiryDateString();
                if (expiry != null && expiry.length() > 10) {
                    expiry = expiry.substring(0, 10);
                }

                pstmt.setLong(1, stock.getProductID());
                pstmt.setInt(2, stock.getQuantity());
                pstmt.setInt(3, stock.getBatchNo());
                pstmt.setString(4, arrival);
                pstmt.setString(5, expiry);
                pstmt.setLong(6, stock.getId());

                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error updating stock: " + e.getMessage());
        }
        return false;
    }

    public static boolean saveStock(Stock stock) {
        if (stock == null) return false;
        if (stock.getId() > 0) {
            return updateStock(stock);
        } else {
            return insertStock(stock);
        }
    }

    public static Stock getStockById(long id) {
        String sql = "SELECT id, product_id, quantity, batchno, arrival_date, expiry_date FROM stock WHERE id = ?";
        try (Connection conn = connect()) {
            if (conn == null) return null;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setLong(1, id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return Stock.fromResultSet(rs);
                    }
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
        try (Connection conn = connect()) {
            if (conn == null) return list;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setLong(1, productId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        list.add(Stock.fromResultSet(rs));
                    }
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
        try (Connection conn = connect()) {
            if (conn == null) return list;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    list.add(Stock.fromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading all stocks: " + e.getMessage());
        }
        return list;
    }

    public static boolean deleteStock(long id) {
        String sql = "DELETE FROM stock WHERE id = ?";
        try (Connection conn = connect()) {
            if (conn == null) return false;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setLong(1, id);
                return pstmt.executeUpdate() > 0;
            }
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
        try (Connection conn = connect()) {
            if (conn == null) return false;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, newQuantity);
                pstmt.setLong(2, stockId);
                return pstmt.executeUpdate() > 0;
            }
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
                "SELECT s.id AS stock_id, s.product_id, p.name AS product_name, " +
                "c.name AS category_name, p.price, s.batchno, s.quantity, " +
                "s.arrival_date, s.expiry_date " +
                "FROM stock s " +
                "JOIN products p ON s.product_id = p.id " +
                "LEFT JOIN categories c ON p.category_id = c.id " +
                "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (LOWER(p.name) LIKE ? OR CAST(s.batchno AS CHAR) LIKE ?) ");
            String term = "%" + keyword.trim().toLowerCase() + "%";
            params.add(term);
            params.add(term);
        }

        if (categoryId != null && categoryId > 0) {
            sql.append("AND p.category_id = ? ");
            params.add(categoryId);
        }

        sql.append("ORDER BY s.expiry_date ASC");

        try (Connection conn = connect()) {
            if (conn == null) return list;
            try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    pstmt.setObject(i + 1, params.get(i));
                }

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        long stockId = rs.getLong("stock_id");
                        long productId = rs.getLong("product_id");
                        String productName = rs.getString("product_name");
                        String categoryName = rs.getString("category_name");
                        double price = rs.getDouble("price");
                        int batchNo = rs.getInt("batchno");
                        int quantity = rs.getInt("quantity");
                        LocalDateTime arrivalDate = Stock.parseDateTime(rs.getString("arrival_date"));
                        LocalDateTime expiryDate = Stock.parseDateTime(rs.getString("expiry_date"));

                        StockItemDTO dto = new StockItemDTO(stockId, productId, productName,
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
        try (Connection conn = connect()) {
            if (conn != null) {
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(prodCountSql)) {
                    if (rs.next()) {
                        stats.setTotalProducts(rs.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error counting products: " + e.getMessage());
        }

        return stats;
    }

    // Configuration getters
    public static String getHost() { return host; }
    public static int getPort() { return port; }
    public static String getDbName() { return dbName; }
    public static String getUser() { return user; }
}
