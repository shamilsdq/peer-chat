module com.shamilsdq.peerchat {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.shamilsdq.peerchat to javafx.fxml;
    exports com.shamilsdq.peerchat;
}
