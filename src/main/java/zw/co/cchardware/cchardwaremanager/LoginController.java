package zw.co.cchardware.cchardwaremanager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.Connection;

public class LoginController {

    public void initialize() {

        Connection connection = DatabaseConnection.connect();

        DatabaseConnection.createTable();

        if (connection != null) {
            System.out.println("Database connected successfully!");
        } else {
            System.out.println("Database connection failed!");
        }
    }

    @FXML
    private void login(ActionEvent event) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("dashboard.fxml"));

        Parent root = loader.load();

        Stage stage = (Stage)((javafx.scene.Node)event.getSource())
                .getScene()
                .getWindow();

        stage.setScene(new Scene(root,700,500));

        stage.setTitle("CC Hardware Manager");

        stage.show();
    }

}