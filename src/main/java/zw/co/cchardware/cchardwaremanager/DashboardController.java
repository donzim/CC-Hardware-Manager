package zw.co.cchardware.cchardwaremanager;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {

    public void openInventory(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(
                getClass().getResource("inventory.fxml")
        );

        Stage stage = (Stage) ((javafx.scene.Node)event.getSource())
                .getScene()
                .getWindow();

        stage.setScene(new Scene(root, 500, 450));
        stage.setTitle("Inventory");

        stage.show();
    }
}