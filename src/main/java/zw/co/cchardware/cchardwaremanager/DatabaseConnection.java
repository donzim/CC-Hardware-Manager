package zw.co.cchardware.cchardwaremanager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

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
}