package zw.co.cchardware.cchardwaremanager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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

        String itemsTable = """
        CREATE TABLE IF NOT EXISTS items (
            id INTEGER PRIMARY KEY,
            name TEXT NOT NULL,
            category TEXT NOT NULL,
            purchase_price REAL NOT NULL,
            selling_price REAL NOT NULL,
            quantity INTEGER NOT NULL
        );
        """;

        String salesTable = """
    CREATE TABLE IF NOT EXISTS sales (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        product_name TEXT NOT NULL,
        quantity INTEGER NOT NULL,
        unit_price REAL NOT NULL,
        total REAL NOT NULL,
        sale_date TEXT NOT NULL
    );
    """;
        String purchaseTable = """
        CREATE TABLE IF NOT EXISTS purchases (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
        product_name TEXT NOT NULL,
        quantity INTEGER NOT NULL,
        purchase_price REAL NOT NULL,
        supplier TEXT,
        notes TEXT,
        purchase_date TEXT NOT NULL
);
        """;

        String expenseTable = """
CREATE TABLE IF NOT EXISTS expenses (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    expense_name TEXT NOT NULL,
    category TEXT NOT NULL,
    amount REAL NOT NULL,
    notes TEXT,
    expense_date TEXT NOT NULL
);
""";

        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {

            statement.execute(itemsTable);
            statement.execute(salesTable);
            statement.execute(purchaseTable);
            statement.execute(expenseTable);

            System.out.println("Items table ready.");
            System.out.println("Sales table ready.");
            System.out.println("Purchases table ready.");
            System.out.println("Expenses table ready.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertItem(Item item) {

        String sql = """
            INSERT INTO items
            (name, category, purchase_price, selling_price, quantity)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection connection = connect();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, item.getName());
            statement.setString(2, item.getCategory());
            statement.setDouble(3, item.getPurchasePrice());
            statement.setDouble(4, item.getSellingPrice());
            statement.setInt(5, item.getQuantity());

            statement.executeUpdate();

            System.out.println("Item saved successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Item> getAllItems() {

        List<Item> items = new ArrayList<>();

        String sql = "SELECT * FROM items";

        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                Item item = new Item(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("category"),
                        resultSet.getDouble("purchase_price"),
                        resultSet.getDouble("selling_price"),
                        resultSet.getInt("quantity")
                );

                items.add(item);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    public static void updateItem(Item item) {

        String sql = """
            UPDATE items
            SET name = ?,
                category = ?,
                purchase_price = ?,
                selling_price = ?,
                quantity = ?
            WHERE id = ?
            """;

        try (Connection connection = connect();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, item.getName());
            statement.setString(2, item.getCategory());
            statement.setDouble(3, item.getPurchasePrice());
            statement.setDouble(4, item.getSellingPrice());
            statement.setInt(5, item.getQuantity());
            statement.setInt(6, item.getId());

            statement.executeUpdate();

            System.out.println("Item updated successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteItem(int id) {

        String sql = "DELETE FROM items WHERE id = ?";

        try (Connection connection = connect();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            statement.executeUpdate();

            System.out.println("Item deleted successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int getTotalProducts() {

        String sql = "SELECT COUNT(*) FROM items";

        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(sql)) {

            if (result.next()) {
                return result.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static int getTotalStockUnits() {

        String sql = "SELECT SUM(quantity) FROM items";

        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(sql)) {

            if (result.next()) {
                return result.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static double getInventoryCostValue() {

        String sql = "SELECT SUM(purchase_price * quantity) FROM items";

        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(sql)) {

            if (result.next()) {
                return result.getDouble(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0.0;
    }

    public static double getPotentialSalesValue() {

        String sql = "SELECT SUM(selling_price * quantity) FROM items";

        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(sql)) {

            if (result.next()) {
                return result.getDouble(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0.0;
    }

    public static int getLowStockCount() {

        String sql = "SELECT COUNT(*) FROM items WHERE quantity <= 10";

        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(sql)) {

            if (result.next()) {
                return result.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static ObservableList<Item> getLowStockItems() {

        ObservableList<Item> lowStockItems = FXCollections.observableArrayList();

        String sql = "SELECT * FROM items WHERE quantity <= 10 ORDER BY quantity ASC";

        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(sql)) {

            while (result.next()) {

                Item item = new Item(
                        result.getInt("id"),
                        result.getString("name"),
                        result.getString("category"),
                        result.getDouble("purchase_price"),
                        result.getDouble("selling_price"),
                        result.getInt("quantity")
                );

                lowStockItems.add(item);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lowStockItems;
    }

    public static void increaseStock(String productName, int quantity) {

        String sql = """
            UPDATE items
            SET quantity = quantity + ?
            WHERE name = ?
            """;

        try (Connection connection = connect();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, quantity);
            statement.setString(2, productName);

            int rowsUpdated = statement.executeUpdate();


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}