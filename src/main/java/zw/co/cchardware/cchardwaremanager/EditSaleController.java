package zw.co.cchardware.cchardwaremanager;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;

public class EditSaleController {

    @FXML
    private TextField productField;

    @FXML
    private TextField quantityField;

    @FXML
    private TextField priceField;

    private Sale selectedSale;

    public void setSale(Sale sale) {

        selectedSale = sale;

        productField.setText(
                sale.getProductName());

        quantityField.setText(
                String.valueOf(
                        sale.getQuantity()));

        priceField.setText(
                String.valueOf(
                        sale.getUnitPrice()));
    }

    @FXML
    private void saveChanges(ActionEvent event) {

        try {

            String productName = productField.getText().trim();
            int newQuantity = Integer.parseInt(quantityField.getText().trim());
            double newPrice = Double.parseDouble(priceField.getText().trim());

            int currentStock =
                    DatabaseConnection.getCurrentStock(
                            selectedSale.getProductName());

            if (productName.isEmpty()) {
                System.out.println("Product name cannot be empty.");
                return;
            }

            if (newQuantity <= 0) {
                System.out.println("Quantity must be greater than zero.");
                return;
            }

            if (newPrice <= 0) {
                System.out.println("Price must be greater than zero.");
                return;
            }

            double newTotal = newQuantity * newPrice;

            /*
             * Adjust stock.
             *
             * Old sale already removed old quantity from inventory.
             * Therefore:
             *
             * old 5 → new 7 = remove 2 more
             * old 5 → new 3 = return 2 to stock
             */
            int availableStock =
                    currentStock + selectedSale.getQuantity();

            if (newQuantity > availableStock) {

                System.out.println(
                        "Not enough stock available.");

                return;
            }


            int stockAdjustment =
                    selectedSale.getQuantity() - newQuantity;

            Alert confirm =
                    new Alert(Alert.AlertType.CONFIRMATION);

            confirm.setTitle("Confirm Edit");
            confirm.setHeaderText(null);
            confirm.setContentText(
                    "Save these changes?");

            java.util.Optional<javafx.scene.control.ButtonType> result =
                    confirm.showAndWait();

            if (result.isEmpty()
                    || result.get()
                    != javafx.scene.control.ButtonType.OK) {

                return;
            }

            DatabaseConnection.adjustStock(
                    selectedSale.getProductName(),
                    stockAdjustment);

            Sale updatedSale = new Sale(
                    selectedSale.getId(),
                    "",
                    productName,
                    newQuantity,
                    newPrice,
                    newTotal
            );

            DatabaseConnection.updateSale(updatedSale);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);

            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Sale updated successfully.");

            alert.showAndWait();

            goBack(event);

        } catch (NumberFormatException e) {

            System.out.println(
                    "Please enter valid numbers for quantity and price.");

        }

    }



    @FXML
    private void goBack(ActionEvent event) {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource("sales-history.fxml"));

            Scene scene =
                    new Scene(loader.load());

            Stage stage =
                    (Stage) ((Node) event.getSource())
                            .getScene()
                            .getWindow();

            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}