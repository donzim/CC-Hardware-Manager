package zw.co.cchardware.cchardwaremanager;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

public class EditPurchaseController {

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
    private void updatePurchase(ActionEvent event) {

        // STEP 1
        String product = productField.getText().trim();

        int newQuantity;
        double newPrice;

        try {

            newQuantity = Integer.parseInt(quantityField.getText().trim());
            newPrice = Double.parseDouble(unitCostField.getText().trim());

        } catch (NumberFormatException e) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Input");
            alert.setHeaderText(null);
            alert.setContentText(
                    "Quantity must be a whole number and Unit Cost must be a valid number.");
            alert.showAndWait();

            return;
        }

        String supplier = supplierField.getText().trim();
        String notes = notesArea.getText().trim();

        // ===========================
        // STEP 2
        // ===========================

        int difference = newQuantity - purchase.getQuantity();


        // ===========================
        // STEP 3
        // ===========================

        purchase.setProductName(product);
        purchase.setQuantity(newQuantity);
        purchase.setPurchasePrice(newPrice);
        purchase.setSupplier(supplier);
        purchase.setNotes(notes);

        // ===========================
        // STEP 4
        // ===========================

        DatabaseConnection.updatePurchase(purchase);

        // ===========================
        // STEP 5
        // ===========================

        DatabaseConnection.adjustStock(product, difference);

        // ===========================
        // STEP 6
        // ===========================

        purchasesController.loadPurchases();

        // ===========================
        // STEP 7
        // ===========================

        Stage stage =
                (Stage) updatePurchaseButton.getScene().getWindow();

        stage.close();
    }

    @FXML
    private Button updatePurchaseButton;

    private Purchase purchase;

    private PurchasesController purchasesController;

    public void setPurchase(Purchase purchase) {

        this.purchase = purchase;

        productField.setText(purchase.getProductName());

        quantityField.setText(
                String.valueOf(purchase.getQuantity()));

        unitCostField.setText(
                String.valueOf(purchase.getPurchasePrice()));

        supplierField.setText(
                purchase.getSupplier());

        notesArea.setText(
                purchase.getNotes());
    }


    public void setPurchasesController(
            PurchasesController purchasesController) {

        this.purchasesController = purchasesController;

    }

}


