package zw.co.cchardware.cchardwaremanager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.TextField;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.net.URL;
import java.util.ResourceBundle;



public class InventoryController implements Initializable {

    @FXML
    private TableView<Item> inventoryTable;

    @FXML
    private TableColumn<Item, Integer> idColumn;

    @FXML
    private TableColumn<Item, String> nameColumn;

    @FXML
    private TableColumn<Item, String> categoryColumn;

    @FXML
    private TableColumn<Item, Double> priceColumn;

    @FXML
    private TableColumn<Item, Integer> quantityColumn;

    @FXML
    private TableColumn<Item, Double> purchasePriceColumn;

    @FXML
    private TextField searchField;


    private ObservableList<Item> items = FXCollections.observableArrayList();
    private FilteredList<Item> filteredItems;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        purchasePriceColumn.setCellValueFactory(
                new PropertyValueFactory<>("purchasePrice"));
        priceColumn.setCellValueFactory(
                new PropertyValueFactory<>("sellingPrice"));
        quantityColumn.setCellValueFactory(
                new PropertyValueFactory<>("quantity"));

        items.addAll(DatabaseConnection.getAllItems());

        filteredItems = new FilteredList<>(items, p -> true);

        inventoryTable.setItems(filteredItems);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {

            filteredItems.setPredicate(item -> {

                if (newValue == null || newValue.isBlank()) {
                    return true;
                }

                String search = newValue.toLowerCase();

                return item.getName().toLowerCase().contains(search)
                        || item.getCategory().toLowerCase().contains(search);

            });

        });
    }
    @FXML
    private void addItem() {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("add-item.fxml"));

            Parent root = loader.load();

            AddItemController controller = loader.getController();
            controller.setInventoryController(this);

            Stage stage = new Stage();
            stage.setTitle("Add New Item");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @FXML
    private void editItem() {

        Item selectedItem = inventoryTable.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            System.out.println("No item selected.");
            return;
        }

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("add-item.fxml"));

            Parent root = loader.load();

            AddItemController controller = loader.getController();

            controller.setInventoryController(this);

            controller.setItem(selectedItem);

            Stage stage = new Stage();
            stage.setTitle("Edit Item");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshTable() {

        items.clear();

        items.addAll(DatabaseConnection.getAllItems());

    }


    @FXML
    private void deleteItem() {

        Item selectedItem = inventoryTable.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select an item to delete.");
            alert.showAndWait();

            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);

        confirmation.setTitle("Delete Item");
        confirmation.setHeaderText(null);
        confirmation.setContentText(
                "Are you sure you want to delete "
                        + selectedItem.getName()
                        + "?");

        ButtonType result = confirmation.showAndWait().orElse(ButtonType.CANCEL);

        if (result == ButtonType.OK) {

            DatabaseConnection.deleteItem(selectedItem.getId());

            items.remove(selectedItem);

        }
    }
}