package zw.co.cchardware.cchardwaremanager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:sqlite:database/inventory.db";

    public static Connection connect() {
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();
            return null;
        }
    }

    public static void createTable() {

        String sql = """
        CREATE TABLE IF NOT EXISTS items (
            id INTEGER PRIMARY KEY,
            name TEXT NOT NULL,
            category TEXT NOT NULL,
            purchase_price REAL NOT NULL,
            selling_price REAL NOT NULL,
            quantity INTEGER NOT NULL
        );
        """;

        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {

            statement.execute(sql);
            System.out.println("Items table ready.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}