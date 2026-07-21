module zw.co.cchardware.cchardwaremanager {
    requires javafx.controls;
    requires javafx.fxml;


    opens zw.co.cchardware.cchardwaremanager to javafx.fxml;
    exports zw.co.cchardware.cchardwaremanager;
}