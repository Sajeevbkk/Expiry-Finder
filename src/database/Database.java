package database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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
        // SQL statement for creating a new table for products
        String sql = "CREATE TABLE IF NOT EXISTS products (\n"
                + "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "    name TEXT NOT NULL,\n"
                + "    barcode TEXT,\n"
                + "    expiry_date TEXT\n"
                + ");";

        try (Connection conn = connect()) {
            if (conn != null) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(sql);
                    System.out.println("Database connection established and table initialized successfully.");
                }
            } else {
                System.err.println("Connection failed. Database not found.");
            }
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }
}
