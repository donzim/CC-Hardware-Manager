package zw.co.cchardware.cchardwaremanager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.ResultSet;
import java.sql.Statement;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.TextField;

import java.io.IOException;

public class PurchasesController {

    private ObservableList<Purchase> purchaseList = FXCollections.observableArrayList();

    @FXML
    private TextField productField;

    @FXML
    private TextField quantityField;

    @FXML
    private TextField unitCostField;

    @FXML
    private TextField supplierField;

    @FXML
    private TextArea notesArea;

    @FXML
    private Button recordPurchaseButton;

    @FXML
    private Button backButton;

    @FXML
    private TableView<Purchase> purchaseTable;

    @FXML
    private TableColumn<Purchase, String> dateColumn;

    @FXML
    private TableColumn<Purchase, String> productColumn;

    @FXML
    private TableColumn<Purchase, Integer> quantityColumn;

    @FXML
    private TableColumn<Purchase, Double> unitCostColumn;

    @FXML
    private TableColumn<Purchase, String> supplierColumn;

    @FXML
    private TableColumn<Purchase, String> notesColumn;

    @FXML
    private TextField sellingPriceField;
    @FXML
    private TextField searchField;

    @FXML
    private Button deletePurchaseButton;

    @FXML
    private Button editPurchaseButton;

    @FXML
    private void editPurchase(ActionEvent event) {

        Purchase selectedPurchase =
                purchaseTable.getSelectionModel().getSelectedItem();

        if (selectedPurchase == null) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select a purchase to edit.");
            alert.showAndWait();

            return;
        }

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("edit-purchase.fxml"));

            Parent root = loader.load();

            EditPurchaseController controller = loader.getController();

            controller.setPurchasesController(this);
            controller.setPurchase(selectedPurchase);

            Stage stage = new Stage();
            stage.setTitle("Edit Purchase");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void recordPurchase(ActionEvent event) {

        String product = productField.getText().trim();
        String quantityText = quantityField.getText().trim();
        String unitCostText = unitCostField.getText().trim();
        String sellingPriceText = sellingPriceField.getText().trim();
        String supplier = supplierField.getText().trim();
        String notes = notesArea.getText().trim();

        if (product.isEmpty() || quantityText.isEmpty() || unitCostText.isEmpty() || sellingPriceText.isEmpty()) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Missing Information");
            alert.setHeaderText(null);
            alert.setContentText("Please fill in Product, Quantity and Unit Cost.");
            alert.showAndWait();

            return;
        }
        int quantity;
        double unitCost;
        double sellingPrice;
        try {

            quantity = Integer.parseInt(quantityText);
            unitCost = Double.parseDouble(unitCostText);
            sellingPrice = Double.parseDouble(sellingPriceText);
        } catch (NumberFormatException e) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Input");
            alert.setHeaderText(null);
            alert.setContentText(
                    "Quantity must be a whole number and Unit Cost must be a valid number.");
            alert.showAndWait();

            return;
        }
        String purchaseDate = LocalDate.now().toString();

        String sql = """
        INSERT INTO purchases
        (product_name, quantity, purchase_price, supplier, notes, purchase_date)
        VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, product);
            pstmt.setInt(2, quantity);
            pstmt.setDouble(3, unitCost);
            pstmt.setString(4, supplier);
            pstmt.setString(5, notes);
            pstmt.setString(6, purchaseDate);

            pstmt.executeUpdate();

            DatabaseConnection.increaseStock(product, quantity, unitCost,
                    sellingPrice);;

            loadPurchases();

            productField.clear();
            quantityField.clear();
            unitCostField.clear();
            supplierField.clear();
            notesArea.clear();
            sellingPriceField.clear();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Purchase recorded successfully.");
            alert.showAndWait();

        } catch (SQLException e) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to record purchase.");

            alert.showAndWait();

            e.printStackTrace();
        }

    }
    public void loadPurchases() {

        purchaseList.clear();

        String sql = "SELECT * FROM purchases ORDER BY id DESC";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                Purchase purchase = new Purchase(
                        rs.getInt("id"),
                        rs.getString("product_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("purchase_price"),
                        rs.getString("supplier"),
                        rs.getString("notes"),
                        rs.getString("purchase_date")
                );

                purchaseList.add(purchase);
            }

            purchaseTable.setItems(purchaseList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void searchPurchases() {

        purchaseList.clear();

        String keyword = searchField.getText().toLowerCase();

        String sql =
                "SELECT * FROM purchases WHERE LOWER(product_name) LIKE ? ORDER BY id DESC";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + keyword + "%");

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {

                purchaseList.add(new Purchase(
                        rs.getInt("id"),
                        rs.getString("product_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("purchase_price"),
                        rs.getString("supplier"),
                        rs.getString("notes"),
                        rs.getString("purchase_date")
                ));
            }

            purchaseTable.setItems(purchaseList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {

        dateColumn.setCellValueFactory(new PropertyValueFactory<>("purchaseDate"));
        productColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        unitCostColumn.setCellValueFactory(new PropertyValueFactory<>("purchasePrice"));
        supplierColumn.setCellValueFactory(new PropertyValueFactory<>("supplier"));
        notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));

        loadPurchases();

        searchField.textProperty().addListener(
                (observable, oldValue, newValue) -> searchPurchases());
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

    @FXML
    private void deletePurchase(ActionEvent event) {

        Purchase selectedPurchase =
                purchaseTable.getSelectionModel().getSelectedItem();

        if (selectedPurchase == null) {

            Alert alert = new Alert(Alert.AlertType.WARNING);

            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select a purchase to delete.");

            alert.showAndWait();

            return;
        }

        Alert confirmation =
                new Alert(Alert.AlertType.CONFIRMATION);

        confirmation.setTitle("Delete Purchase");
        confirmation.setHeaderText(null);

        confirmation.setContentText(
                "Delete purchase of "
                        + selectedPurchase.getProductName()
                        + "?");

        ButtonType result =
                confirmation.showAndWait().orElse(ButtonType.CANCEL);

        if (result == ButtonType.OK) {

            DatabaseConnection.deletePurchase(
                    selectedPurchase.getId());

            loadPurchases();
        }
    }

}