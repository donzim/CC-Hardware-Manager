package zw.co.cchardware.cchardwaremanager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

import java.io.IOException;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;

public class SalesHistoryController implements Initializable {


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
    private TableView<Sale> salesTable;

    @FXML
    private TableColumn<Sale, String> dateColumn;

    @FXML
    private TableColumn<Sale, String> productColumn;

    @FXML
    private TableColumn<Sale, Integer> quantityColumn;

    @FXML
    private TableColumn<Sale, Double> priceColumn;

    @FXML
    private TableColumn<Sale, Double> totalColumn;

    @FXML
    private TextField searchField;

    @FXML
    private Button deleteSaleButton;

    @FXML
    private Button editSaleButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("saleDate"));
        productColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        priceColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", item));
                }
            }
        });

        totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
        totalColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", item));
                }
            }
        });

        loadSales();
        searchField.textProperty().addListener(
                (observable, oldValue, newValue) -> searchSales());
    }

    private void loadSales() {

        ObservableList<Sale> salesList = FXCollections.observableArrayList();

        String sql = "SELECT * FROM sales";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                salesList.add(new Sale(
                        rs.getInt("id"),
                        rs.getString("sale_date"),
                        rs.getString("product_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("unit_price"),
                        rs.getDouble("total")
                ));

            }

            salesTable.setItems(salesList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void searchSales() {

        ObservableList<Sale> filteredList = FXCollections.observableArrayList();

        String keyword = searchField.getText().toLowerCase();

        String sql = "SELECT * FROM sales WHERE LOWER(product_name) LIKE ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + keyword + "%");

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {

                filteredList.add(new Sale(
                        rs.getInt("id"),
                        rs.getString("sale_date"),
                        rs.getString("product_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("unit_price"),
                        rs.getDouble("total")
                ));
            }

            salesTable.setItems(filteredList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void deleteSale(ActionEvent event) {

        Sale selectedSale =
                salesTable.getSelectionModel()
                        .getSelectedItem();

        if (selectedSale == null) {

            Alert alert =
                    new Alert(Alert.AlertType.WARNING);

            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText(
                    "Please select a sale to delete.");

            alert.showAndWait();

            return;
        }

        DatabaseConnection.adjustStock(
                selectedSale.getProductName(),
                selectedSale.getQuantity());

        DatabaseConnection.deleteSale(
                selectedSale.getId());

        loadSales();

        Alert alert =
                new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(
                "Sale deleted successfully.");

        alert.showAndWait();
    }

    @FXML
    private void editSale(ActionEvent event) {

        Sale selectedSale =
                salesTable.getSelectionModel()
                        .getSelectedItem();

        if (selectedSale == null) {

            Alert alert =
                    new Alert(Alert.AlertType.WARNING);

            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText(
                    "Please select a sale to edit.");

            alert.showAndWait();

            return;
        }

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource("edit-sale.fxml"));

            Scene scene =
                    new Scene(loader.load());

            EditSaleController controller =
                    loader.getController();

            controller.setSale(selectedSale);

            Stage stage =
                    (Stage) ((Node) event.getSource())
                            .getScene()
                            .getWindow();

            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {

            e.printStackTrace();

            Alert alert =
                    new Alert(Alert.AlertType.ERROR);

            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(
                    "Unable to open Edit Sale.");

            alert.showAndWait();
        }
    }
}
