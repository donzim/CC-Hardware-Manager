package zw.co.cchardware.cchardwaremanager;
import javafx.scene.control.Alert;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class AddItemController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField categoryField;

    @FXML
    private TextField purchasePriceField;

    @FXML
    private TextField sellingPriceField;

    @FXML
    private TextField quantityField;

    private InventoryController inventoryController;

    private Item itemBeingEdited;

    public void setInventoryController(InventoryController inventoryController) {
        this.inventoryController = inventoryController;
    }

    public void setItem(Item item) {

        itemBeingEdited = item;

        nameField.setText(item.getName());
        categoryField.setText(item.getCategory());
        purchasePriceField.setText(
                String.valueOf(item.getPurchasePrice()));

        sellingPriceField.setText(
                String.valueOf(item.getSellingPrice()));
        quantityField.setText(String.valueOf(item.getQuantity()));
    }



    @FXML
    private void saveItem() {

        String name = nameField.getText();
        String category = categoryField.getText();

        if (!validateInput(name, category)) {
            return;
        }
        try {
            double purchasePrice =
                    Double.parseDouble(purchasePriceField.getText());

            double sellingPrice =
                    Double.parseDouble(sellingPriceField.getText());  //don
            int quantity = Integer.parseInt(quantityField.getText());

            if (itemBeingEdited == null) {

                Item item = buildItem(
                        name,
                        category,
                        purchasePrice,
                        sellingPrice,
                        quantity
                );

                DatabaseConnection.insertItem(item);
                inventoryController.refreshTable();

            } else {

                itemBeingEdited.setName(name);
                itemBeingEdited.setCategory(category);
                itemBeingEdited.setPurchasePrice(purchasePrice);
                itemBeingEdited.setSellingPrice(sellingPrice);
                itemBeingEdited.setQuantity(quantity);

                DatabaseConnection.updateItem(itemBeingEdited);

                inventoryController.refreshTable();

            }

            clearForm();


        } catch (NumberFormatException e) {

            Alert alert = new Alert(Alert.AlertType.ERROR);

            alert.setTitle("Invalid Number");
            alert.setHeaderText(null);
            alert.setContentText(
                    "Price must be a number and Quantity must be a whole number."
            );

            alert.showAndWait();
        }
    }
    private void clearForm() {

        nameField.clear();
        categoryField.clear();
        purchasePriceField.clear();
        sellingPriceField.clear();
        quantityField.clear();

        nameField.requestFocus();
    }

    private Item buildItem(String name,
                           String category,
                           double purchasePrice,
                           double sellingPrice,
                           int quantity) {

        return new Item(
                name,
                category,
                purchasePrice,
                sellingPrice,
                quantity
        );
    }
    private boolean validateInput(String name, String category) {

        if (name.isBlank()
                || category.isBlank()
                || purchasePriceField.getText().isBlank()
                || sellingPriceField.getText().isBlank()
                || quantityField.getText().isBlank()) {

            Alert alert = new Alert(Alert.AlertType.WARNING);

            alert.setTitle("Missing Information");
            alert.setHeaderText(null);
            alert.setContentText("Please fill in all fields.");

            alert.showAndWait();

            return false;
        }

        return true;
    }
}
