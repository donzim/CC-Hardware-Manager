package zw.co.cchardware.cchardwaremanager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

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
    public void initialize() {

        int totalProducts = DatabaseConnection.getTotalProducts();
        totalProductsLabel.setText(String.valueOf(totalProducts));

        int totalStockUnits = DatabaseConnection.getTotalStockUnits();
        totalStockLabel.setText(String.valueOf(totalStockUnits));

        double inventoryCost = DatabaseConnection.getInventoryCostValue();

        NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.US);

        inventoryCostLabel.setText(currency.format(inventoryCost));

        double potentialSales = DatabaseConnection.getPotentialSalesValue();

        potentialSalesLabel.setText("US" + currency.format(potentialSales));
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
}