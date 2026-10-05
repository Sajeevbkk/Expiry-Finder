# Expiry Finder

A simple Java application to help shops manage product expiry dates efficiently.  
⚠️ This is a school project and **not production‑ready**.

## 📋 Features

- Track product expiry dates
- Simple and intuitive CLI interface
- Database-backed storage
- Search and filter products
- Generate expiry reports

## 🛠️ Technology Stack

- **Java 17** - Core language
- **Maven** - Build and dependency management
- **JDBC** - Database connectivity
- **MySQL 8.0+** - Relational data persistence
- **Swing** - Modern desktop user interface

## 📋 Prerequisites

- Java JDK 17 or higher
- Maven 3.6 or higher
- MySQL Server 8.0+ (or MariaDB / XAMPP / WampServer)
- Git (optional, for cloning)

## ⚙️ Database Configuration

Configuration is located in `db.properties` (in project root):

```properties
db.host=localhost
db.port=3306
db.name=expiry_finder
db.user=root
db.password=
```

> **Note:** The application automatically creates the `expiry_finder` database and required tables (`categories`, `products`, `stock`) on first run if your user has database creation privileges.
> Alternatively, you can import `sql/setup_mysql.sql` using MySQL Workbench, phpMyAdmin, or MySQL CLI:
> ```bash
> mysql -u root -p < sql/setup_mysql.sql
> ```

## 🚀 Getting Started

### 1. Clone the Repository
```bash
git clone https://github.com/Sajeevbkk/Expiry-Finder.git
cd Expiry-Finder
```

### 2. Build the Project
```bash
mvn clean compile
```

### 3. Run Tests
```bash
mvn test
```

### 4. Run the Application
```bash
mvn exec:java
```

## 📝 Project Structure

```
Expiry-Finder/
├── src/
│   ├── main/
│   │   └── java/
│   └── test/
├── pom.xml
├── README.md
└── LICENSE
```

## 🤝 Contributing

Contributions are welcome! Feel free to fork this repository and submit pull requests.

## 📄 License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for details.

---

> Have a Nice Day 😊
