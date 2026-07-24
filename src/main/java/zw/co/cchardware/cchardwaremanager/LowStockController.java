package zw.co.cchardware.cchardwaremanager;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.paint.Color;

public class LowStockController {

    @FXML
    private TableView<Item> lowStockTable;

    @FXML
    private TableColumn<Item, String> nameColumn;

    @FXML
    private TableColumn<Item, String> categoryColumn;

    @FXML
    private TableColumn<Item, Integer> quantityColumn;

    @FXML
    private TableColumn<Item, String> statusColumn;

    @FXML
    public void initialize() {

        nameColumn.setCellValueFactory(
                new PropertyValueFactory<>("name"));

        categoryColumn.setCellValueFactory(
                new PropertyValueFactory<>("category"));

        quantityColumn.setCellValueFactory(
                new PropertyValueFactory<>("quantity"));

        statusColumn.setCellValueFactory(
                new PropertyValueFactory<>("status"));


        statusColumn.setCellFactory(column -> new TableCell<Item, String>() {

            @Override
            protected void updateItem(String item, boolean empty) {

                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {

                    setText(item);

                    if (item.equals("Critical")) {
                        setTextFill(Color.RED);
                    } else {
                        setTextFill(Color.ORANGE);
                    }
                }
            }
        });

        ObservableList<Item> items =
                DatabaseConnection.getLowStockItems();

        lowStockTable.setItems(items);
    }
}