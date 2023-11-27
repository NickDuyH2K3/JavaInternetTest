module InternetCheck {
    requires javafx.controls;
    requires javafx.fxml;

    exports application;

    opens application to javafx.fxml;
}
