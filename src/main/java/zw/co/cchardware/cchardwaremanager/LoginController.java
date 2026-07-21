package zw.co.cchardware.cchardwaremanager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LoginController {

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