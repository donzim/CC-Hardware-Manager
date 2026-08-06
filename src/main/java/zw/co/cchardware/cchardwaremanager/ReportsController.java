package zw.co.cchardware.cchardwaremanager;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.ComboBox;

import java.io.IOException;




public class ReportsController implements Initializable {
    @FXML
    private Label inventoryValueLabel;

    @FXML
    private Label potentialSalesLabel;

    @FXML
    private Label lowStockLabel;

    @FXML
    private Label purchasesLabel;

    @FXML
    private Label expensesLabel;

    @FXML
    private Label profitLabel;
    @FXML
    private Label weekSalesLabel;

    @FXML
    private Label monthSalesLabel;

    @FXML
    private Label yearSalesLabel;

    @FXML
    private Label todaySalesLabel;

    @FXML
    private ComboBox<String> monthComboBox;

    @FXML
    private ComboBox<Integer> yearComboBox;

    @FXML
    private Button refreshButton;

    @FXML
    private TableView<TopSellingProduct> topProductsTable;

    @FXML
    private TableColumn<TopSellingProduct, String> productColumn;

    @FXML
    private TableColumn<TopSellingProduct, Integer> quantitySoldColumn;

    @FXML
    private Button backButton;

    @FXML
    private void refreshReport(ActionEvent event) {

        String month = monthComboBox.getValue();
        Integer year = yearComboBox.getValue();

        int monthNumber = switch (month) {
            case "January" -> 1;
            case "February" -> 2;
            case "March" -> 3;
            case "April" -> 4;
            case "May" -> 5;
            case "June" -> 6;
            case "July" -> 7;
            case "August" -> 8;
            case "September" -> 9;
            case "October" -> 10;
            case "November" -> 11;
            case "December" -> 12;
            default -> 1;
        };
        monthSalesLabel.setText(
                String.format("$%.2f",
                        DatabaseConnection.getMonthlySales(monthNumber, year)));

        purchasesLabel.setText(
                String.format("$%.2f",
                        DatabaseConnection.getMonthlyPurchases(monthNumber, year)));

        expensesLabel.setText(
                String.format("$%.2f",
                        DatabaseConnection.getMonthlyExpenses(monthNumber, year)));

        profitLabel.setText(
                String.format("$%.2f",
                        DatabaseConnection.getEstimatedProfit(monthNumber, year)));

        topProductsTable.setItems(
                DatabaseConnection.getTopSellingProducts(
                        monthNumber,
                        year));
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        inventoryValueLabel.setText(
                String.format("$%.2f",
                        DatabaseConnection.getInventoryCostValue()));

        potentialSalesLabel.setText(
                String.format("$%.2f",
                        DatabaseConnection.getPotentialSalesValue()));

        lowStockLabel.setText(
                String.valueOf(DatabaseConnection.getLowStockCount()));

        productColumn.setCellValueFactory(
                new PropertyValueFactory<>("productName"));

        quantitySoldColumn.setCellValueFactory(
                new PropertyValueFactory<>("quantitySold"));

        topProductsTable.setItems(
                DatabaseConnection.getTopSellingProducts());

        purchasesLabel.setText(
                String.format("$%.2f",
                        DatabaseConnection.getTotalPurchasesValue()));

        expensesLabel.setText(
                String.format("$%.2f",
                        DatabaseConnection.getTotalExpenses()));

        profitLabel.setText(
                String.format("$%.2f",
                        DatabaseConnection.getEstimatedProfit()));

        todaySalesLabel.setText(
                String.format("$%.2f",
                        DatabaseConnection.getTodaySales()));

        weekSalesLabel.setText(
                String.format("$%.2f",
                        DatabaseConnection.getWeeklySales()));

        monthSalesLabel.setText(
                String.format("$%.2f",
                        DatabaseConnection.getMonthlySales()));

        yearSalesLabel.setText(
                String.format("$%.2f",
                        DatabaseConnection.getYearlySales()));

        monthComboBox.getItems().addAll(
                "January",
                "February",
                "March",
                "April",
                "May",
                "June",
                "July",
                "August",
                "September",
                "October",
                "November",
                "December"
        );
        yearComboBox.getItems().addAll(

                2025,
                2026,
                2027

        );

        monthComboBox.getSelectionModel().select("August");

        yearComboBox.getSelectionModel().select(2026);


    }
    @FXML
    private void goHome(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("dashboard.fxml"));

        Scene scene = new Scene(loader.load());

        Stage stage = (Stage)((Node)event.getSource())
                .getScene()
                .getWindow();

        stage.setScene(scene);
        stage.show();
    }

}
