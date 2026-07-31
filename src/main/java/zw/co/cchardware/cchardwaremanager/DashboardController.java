package zw.co.cchardware.cchardwaremanager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;
public class DashboardController {

    @FXML
    private Label totalProductsLabel;

    @FXML
    private Label totalStockLabel;

    @FXML
    private Label inventoryCostLabel;

    @FXML
    private Label potentialSalesLabel;

    @FXML
    private Label lowStockLabel;

    @FXML
    public void initialize() {

        int totalProducts = DatabaseConnection.getTotalProducts();
        totalProductsLabel.setText(String.valueOf(totalProducts));

        int totalStockUnits = DatabaseConnection.getTotalStockUnits();
        totalStockLabel.setText(String.valueOf(totalStockUnits));

        double inventoryCost = DatabaseConnection.getInventoryCostValue();

        NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.US);

        inventoryCostLabel.setText(currency.format(inventoryCost));

        double potentialSales = DatabaseConnection.getPotentialSalesValue();

        potentialSalesLabel.setText("" + currency.format(potentialSales));

        int lowStock = DatabaseConnection.getLowStockCount();

        lowStockLabel.setText(String.valueOf(lowStock));
    }

    public void openInventory(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(
                getClass().getResource("inventory.fxml")
        );

        Stage stage = (Stage) ((javafx.scene.Node) event.getSource())
                .getScene()
                .getWindow();

        stage.setScene(new Scene(root, 850, 500));
        stage.setTitle("Inventory");

        stage.show();
    }

    @FXML
    private void openLowStock(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("low-stock.fxml"));

        Parent root = loader.load();

        Stage stage = new Stage();

        stage.setTitle("Low Stock Items");
        stage.setScene(new Scene(root));

        stage.show();
    }
    @FXML
    private void openSales(ActionEvent event) throws IOException {

        FXMLLoader loader =
                new FXMLLoader(getClass().getResource("sales.fxml"));

        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setScene(new Scene(root));
        stage.setTitle("New Sale");
        stage.show();
    }

    @FXML
    private void openSalesHistory(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("sales-history.fxml"));

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        Scene scene = new Scene(loader.load());

        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void openPurchases(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("purchases.fxml"));

        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setScene(new Scene(root));

        stage.show();
    }
}