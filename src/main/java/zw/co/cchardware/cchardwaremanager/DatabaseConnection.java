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

    public static void increaseStock(
            String productName,
            int quantity,
            double purchasePrice,
            double sellingPrice) {

        String checkSql =
                "SELECT id FROM items WHERE name = ?";

        try (Connection connection = connect()) {

            PreparedStatement checkStatement =
                    connection.prepareStatement(checkSql);

            checkStatement.setString(1, productName);

            ResultSet result =
                    checkStatement.executeQuery();

            if (result.next()) {

                String updateSql = """
                UPDATE items
                SET quantity = quantity + ?
                WHERE name = ?
                """;

                PreparedStatement updateStatement =
                        connection.prepareStatement(updateSql);

                updateStatement.setInt(1, quantity);
                updateStatement.setString(2, productName);

                updateStatement.executeUpdate();

            } else {

                String insertSql = """
                INSERT INTO items
                (name,
                 category,
                 purchase_price,
                 selling_price,
                 quantity)
                VALUES (?, ?, ?, ?, ?)
                """;

                PreparedStatement insertStatement =
                        connection.prepareStatement(insertSql);

                insertStatement.setString(1, productName);
                insertStatement.setString(2, "General");
                insertStatement.setDouble(3, purchasePrice);
                insertStatement.setDouble(4, sellingPrice);
                insertStatement.setInt(5, quantity);

                insertStatement.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static ObservableList<TopSellingProduct> getTopSellingProducts() {

        ObservableList<TopSellingProduct> products =
                FXCollections.observableArrayList();

        String sql = """
            SELECT product_name,
                   SUM(quantity) AS total_sold
            FROM sales
            GROUP BY product_name
            ORDER BY total_sold DESC
            LIMIT 10
            """;

        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(sql)) {

            while (result.next()) {

                products.add(new TopSellingProduct(
                        result.getString("product_name"),
                        result.getInt("total_sold")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    public static ObservableList<TopSellingProduct> getTopSellingProducts(
            int month,
            int year) {

        ObservableList<TopSellingProduct> products =
                FXCollections.observableArrayList();

        String sql = """
            SELECT product_name,
                   SUM(quantity) AS total_sold
            FROM sales
            WHERE strftime('%Y', sale_date) = ?
            AND strftime('%m', sale_date) = ?
            GROUP BY product_name
            ORDER BY total_sold DESC
            LIMIT 10
            """;

        try (Connection connection = connect();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, String.valueOf(year));
            statement.setString(2, String.format("%02d", month));

            ResultSet result = statement.executeQuery();

            while (result.next()) {

                products.add(new TopSellingProduct(

                        result.getString("product_name"),

                        result.getInt("total_sold")

                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }
    public static double getTotalPurchasesValue() {

        String sql = "SELECT SUM(quantity * purchase_price) FROM purchases";

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
    public static double getTotalExpenses() {

        String sql = "SELECT SUM(amount) FROM expenses";

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
    public static double getTotalSalesValue() {

        String sql = "SELECT SUM(total) FROM sales";

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
    public static double getEstimatedProfit() {

        return getTotalSalesValue()
                - getTotalPurchasesValue()
                - getTotalExpenses();
    }
    public static double getTodaySales() {

        String sql = """
            SELECT SUM(total)
            FROM sales
            WHERE sale_date = DATE('now', 'localtime')
            """;

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
    public static double getWeeklySales() {

        String sql = """
            SELECT SUM(total)
            FROM sales
            WHERE sale_date >= DATE('now', '-6 days')
            """;

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
    public static double getMonthlySales() {

        String sql = """
            SELECT SUM(total)
            FROM sales
            WHERE strftime('%Y-%m', sale_date) =
                  strftime('%Y-%m', 'now')
            """;

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
    public static double getMonthlySales(int month, int year) {

        String sql = """
            SELECT SUM(total)
            FROM sales
            WHERE strftime('%Y', sale_date) = ?
            AND strftime('%m', sale_date) = ?
            """;

        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, String.valueOf(year));
            statement.setString(2, String.format("%02d", month));

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                return result.getDouble(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0.0;
    }
    public static double getMonthlyPurchases(int month, int year) {

        String sql = """
            SELECT SUM(quantity * purchase_price)
            FROM purchases
            WHERE strftime('%Y', purchase_date) = ?
            AND strftime('%m', purchase_date) = ?
            """;

        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, String.valueOf(year));
            statement.setString(2, String.format("%02d", month));

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                return result.getDouble(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0.0;
    }
    public static void deletePurchase(int purchaseId) {

        String sql = "DELETE FROM purchases WHERE id = ?";

        try (Connection connection = connect();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, purchaseId);

            statement.executeUpdate();

            System.out.println("Purchase deleted successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void deleteExpense(int expenseId) {

        String sql = "DELETE FROM expenses WHERE id = ?";

        try (Connection connection = connect();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, expenseId);

            statement.executeUpdate();

            System.out.println("Expense deleted successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static double getMonthlyExpenses(int month, int year) {

        String sql = """
            SELECT SUM(amount)
            FROM expenses
            WHERE strftime('%Y', expense_date) = ?
            AND strftime('%m', expense_date) = ?
            """;

        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, String.valueOf(year));
            statement.setString(2, String.format("%02d", month));

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                return result.getDouble(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0.0;
    }
    public static double getYearlySales() {

        String sql = """
            SELECT SUM(total)
            FROM sales
            WHERE strftime('%Y', sale_date) =
                  strftime('%Y', 'now')
            """;

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
    public static double getEstimatedProfit(int month, int year) {

        return getMonthlySales(month, year)
                - getMonthlyPurchases(month, year)
                - getMonthlyExpenses(month, year);
    }

    public static void updatePurchase(Purchase purchase) {

        String sql = """
        UPDATE purchases
        SET product_name = ?,
            quantity = ?,
            purchase_price = ?,
            supplier = ?,
            notes = ?
        WHERE id = ?
        """;

        try (Connection connection = connect();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, purchase.getProductName());
            statement.setInt(2, purchase.getQuantity());
            statement.setDouble(3, purchase.getPurchasePrice());
            statement.setString(4, purchase.getSupplier());
            statement.setString(5, purchase.getNotes());
            statement.setInt(6, purchase.getId());

            statement.executeUpdate();

            System.out.println("Purchase updated successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void updateExpense(Expense expense) {

        String sql = """
        UPDATE expenses
        SET expense_name = ?,
            category = ?,
            amount = ?,
            notes = ?
        WHERE id = ?
        """;

        try (Connection connection = connect();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1,
                    expense.getExpenseName());

            statement.setString(2,
                    expense.getCategory());

            statement.setDouble(3,
                    expense.getAmount());

            statement.setString(4,
                    expense.getNotes());

            statement.setInt(5,
                    expense.getId());

            statement.executeUpdate();

            System.out.println(
                    "Expense updated successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void adjustStock(
            String productName,
            int quantityDifference) {

        String sql = """
        UPDATE items
        SET quantity = quantity + ?
        WHERE name = ?
        """;

        try (Connection connection = connect();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, quantityDifference);
            statement.setString(2, productName);

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}