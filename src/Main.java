import database.Database;
import gui.App;

public class Main {
    public static void main(String[] args) {
        // Initialize SQLite database and tables
        Database.initializeDatabase();
        
        new App();
    }
}