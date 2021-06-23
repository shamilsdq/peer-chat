module com.shamilsdq.peerchat {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens com.shamilsdq.peerchat to javafx.fxml;
    exports com.shamilsdq.peerchat to javafx.fxml, javafx.graphics;
}
