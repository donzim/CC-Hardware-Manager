package zw.co.cchardware.cchardwaremanager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import java.sql.SQLException;
import java.time.LocalDate;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.control.TextField;

import java.io.IOException;
public class SalesController implements Initializable{

    @FXML
    private ComboBox<String> productComboBox;

    @FXML
    private TableView<CartItem> cartTable;

    @FXML
    private TableColumn<CartItem, String> cartProductColumn;

    @FXML
    private TableColumn<CartItem, Integer> cartQuantityColumn;

    @FXML
    private TableColumn<CartItem, Double> cartPriceColumn;

    @FXML
    private TableColumn<CartItem, Double> cartTotalColumn;

    private ObservableList<CartItem> cartItems =
            FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        loadProducts();
        cartProductColumn.setCellValueFactory(
                new PropertyValueFactory<>("productName"));

        cartQuantityColumn.setCellValueFactory(
                new PropertyValueFactory<>("quantity"));

        cartPriceColumn.setCellValueFactory(
                new PropertyValueFactory<>("unitPrice"));

        cartTotalColumn.setCellValueFactory(
                new PropertyValueFactory<>("total"));

        cartTable.setItems(cartItems);
        productComboBox.setOnAction(e -> loadPrice());
        quantityField.textProperty().addListener((observable, oldValue, newValue) -> calculateTotal());

    }

    @FXML
    private Label priceLabel;

    @FXML
    private TextField quantityField;

    @FXML
    private Label totalLabel;

    private double currentPrice = 0.0;

    @FXML
    private void goHome(ActionEvent event) throws IOException {

        FXMLLoader loader =
                new FXMLLoader(getClass().getResource("dashboard.fxml"));

        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setScene(new Scene(root));
        stage.setTitle("Dashboard");
        stage.show();
    }

    private void loadProducts() {

        String sql = "SELECT name FROM items ORDER BY name";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                productComboBox.getItems().add(rs.getString("name"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPrice() {

        String sql = "SELECT selling_price FROM items WHERE name = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, productComboBox.getValue());

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {

                currentPrice = rs.getDouble("selling_price");

                priceLabel.setText(String.format("$%.2f", currentPrice));

            }

        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    private void calculateTotal() {

        try {

            int quantity = Integer.parseInt(quantityField.getText());

            double total = quantity * currentPrice;

            totalLabel.setText(String.format("$%.2f", total));

        } catch (NumberFormatException e) {

            totalLabel.setText("$0.00");

        }

    }

    private boolean hasEnoughStock(String product, int quantity) {

        String sql =
                "SELECT quantity FROM items WHERE name = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, product);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {

                int stock = rs.getInt("quantity");

                return stock >= quantity;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private void deductStock(String product, int quantity) {

        String sql =
                "UPDATE items SET quantity = quantity - ? WHERE name = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, quantity);
            pstmt.setString(2, product);

            pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void recordSale(String product, int quantity, double unitPrice) {

        System.out.println("recordSale() called");
        String sql = """
        INSERT INTO sales
        (product_name, quantity, unit_price, total, sale_date)
        VALUES (?, ?, ?, ?, ?)
        """;

        double total = unitPrice * quantity;

        String saleDate = LocalDate.now().toString();

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, product);
            pstmt.setInt(2, quantity);
            pstmt.setDouble(3, unitPrice);
            pstmt.setDouble(4, total);
            pstmt.setString(5, saleDate);

            pstmt.executeUpdate();
            System.out.println("Sale inserted into database.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void completeSale() {

        if (cartItems.isEmpty()) {

            System.out.println("Cart is empty.");

            return;
        }

        for (CartItem item : cartItems) {

            if (!hasEnoughStock(
                    item.getProductName(),
                    item.getQuantity())) {

                System.out.println(
                        "Not enough stock for "
                                + item.getProductName());

                return;
            }
        }

        for (CartItem item : cartItems) {

            deductStock(
                    item.getProductName(),
                    item.getQuantity());

            recordSale(
                    item.getProductName(),
                    item.getQuantity(),
                    item.getUnitPrice());
        }

        cartItems.clear();

        calculateGrandTotal();

        System.out.println(
                "Sale completed successfully.");
    }

    @FXML
    private void addItem() {

        if (productComboBox.getValue() == null) {
            System.out.println("Select a product.");
            return;
        }

        if (quantityField.getText().isEmpty()) {
            System.out.println("Enter quantity.");
            return;
        }

        int quantity =
                Integer.parseInt(quantityField.getText());

        String product =
                productComboBox.getValue();

        if (!hasEnoughStock(product, quantity)) {

            System.out.println("Not enough stock.");

            return;
        }

        CartItem item =
                new CartItem(
                        product,
                        quantity,
                        currentPrice
                );

        cartItems.add(item);

        quantityField.clear();

        calculateGrandTotal();
    }
    private void calculateGrandTotal() {

        double grandTotal = 0;

        for (CartItem item : cartItems) {

            grandTotal += item.getTotal();

        }

        totalLabel.setText(
                String.format("$%.2f", grandTotal));
    }
    @FXML
    private void removeItem() {

        CartItem selectedItem =
                cartTable.getSelectionModel()
                        .getSelectedItem();

        if (selectedItem == null) {

            System.out.println("Select an item to remove.");

            return;
        }

        cartItems.remove(selectedItem);

        calculateGrandTotal();
    }

}
