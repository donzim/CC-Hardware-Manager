package zw.co.cchardware.cchardwaremanager;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import javafx.event.ActionEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.ResultSet;
import java.sql.Statement;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;

public class ExpensesController implements Initializable {

    @FXML
    private TextField expenseNameField;

    @FXML
    private TextField categoryField;

    @FXML
    private TextField amountField;

    @FXML
    private TextArea notesArea;

    @FXML
    private TextField searchField;

    @FXML
    private Button recordExpenseButton;

    @FXML
    private Button backButton;

    @FXML
    private TableView<Expense> expenseTable;

    @FXML
    private TableColumn<Expense, String> dateColumn;

    @FXML
    private TableColumn<Expense, String> expenseColumn;

    @FXML
    private TableColumn<Expense, String> categoryColumn;

    @FXML
    private TableColumn<Expense, Double> amountColumn;

    @FXML
    private TableColumn<Expense, String> notesColumn;

    @FXML
    private void recordExpense(ActionEvent event) {

        String expenseName = expenseNameField.getText().trim();
        String category = categoryField.getText().trim();
        String amountText = amountField.getText().trim();
        String notes = notesArea.getText().trim();

        if (expenseName.isEmpty()
                || category.isEmpty()
                || amountText.isEmpty()) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Missing Information");
            alert.setHeaderText(null);
            alert.setContentText(
                    "Please fill in Expense Name, Category and Amount.");
            alert.showAndWait();

            return;
        }

        double amount;

        try {

            amount = Double.parseDouble(amountText);

        } catch (NumberFormatException e) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Amount");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a valid amount.");
            alert.showAndWait();

            return;
        }

        String expenseDate = LocalDate.now().toString();

        String sql = """
            INSERT INTO expenses
            (expense_name, category, amount, notes, expense_date)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt =
                     conn.prepareStatement(sql)) {

            pstmt.setString(1, expenseName);
            pstmt.setString(2, category);
            pstmt.setDouble(3, amount);
            pstmt.setString(4, notes);
            pstmt.setString(5, expenseDate);

            pstmt.executeUpdate();

            loadExpenses();

            expenseNameField.clear();
            categoryField.clear();
            amountField.clear();
            notesArea.clear();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Expense recorded successfully.");
            alert.showAndWait();

        } catch (SQLException e) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to record expense.");
            alert.showAndWait();

            e.printStackTrace();
        }
    }

    private void loadExpenses() {

        expenseList.clear();

        String sql = "SELECT * FROM expenses ORDER BY id DESC";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                Expense expense = new Expense(
                        rs.getInt("id"),
                        rs.getString("expense_name"),
                        rs.getString("category"),
                        rs.getDouble("amount"),
                        rs.getString("notes"),
                        rs.getString("expense_date")
                );

                expenseList.add(expense);
            }

            expenseTable.setItems(expenseList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void searchExpenses() {

        expenseList.clear();

        String keyword = searchField.getText().toLowerCase();

        String sql = """
            SELECT * FROM expenses
            WHERE LOWER(expense_name) LIKE ?
               OR LOWER(category) LIKE ?
            ORDER BY id DESC
            """;

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {

                expenseList.add(new Expense(
                        rs.getInt("id"),
                        rs.getString("expense_name"),
                        rs.getString("category"),
                        rs.getDouble("amount"),
                        rs.getString("notes"),
                        rs.getString("expense_date")
                ));
            }

            expenseTable.setItems(expenseList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void goHome(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("dashboard.fxml"));

        Scene scene = new Scene(loader.load());

        Stage stage = (Stage) ((Node) event.getSource())
                .getScene()
                .getWindow();

        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        dateColumn.setCellValueFactory(
                new PropertyValueFactory<>("expenseDate"));

        expenseColumn.setCellValueFactory(
                new PropertyValueFactory<>("expenseName"));

        categoryColumn.setCellValueFactory(
                new PropertyValueFactory<>("category"));

        amountColumn.setCellValueFactory(
                new PropertyValueFactory<>("amount"));

        notesColumn.setCellValueFactory(
                new PropertyValueFactory<>("notes"));

        loadExpenses();
        searchField.textProperty().addListener(
                (observable, oldValue, newValue) -> searchExpenses());
    }
    private ObservableList<Expense> expenseList =
            FXCollections.observableArrayList();
}