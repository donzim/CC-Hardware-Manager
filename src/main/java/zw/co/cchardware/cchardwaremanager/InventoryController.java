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

    private int nextId = 5;  public int getNextId() {
        System.out.println("Next ID: " + nextId);
        return nextId++;
    }

    private ObservableList<Item> items = FXCollections.observableArrayList();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        items.addAll(

                new Item(1, "4 Inch Nails", "Fasteners", 3.50, 4.50, 320),
                new Item(2, "PPC Cement", "Building", 11.00, 13.20, 75),
                new Item(3, "PVC Pipe", "Plumbing", 6.80, 8.90, 120),
                new Item(4, "Roofing Nails", "Fasteners", 4.20, 5.75, 180)

        );

        inventoryTable.setItems(items);
    }
    public void addItemToTable(Item item) {

        items.add(item);

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
        inventoryTable.refresh();
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

            items.remove(selectedItem);

        }
    }
}