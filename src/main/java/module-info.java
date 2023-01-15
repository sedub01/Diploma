module java.diploma {
    requires javafx.controls;
    requires javafx.fxml;


    opens root to javafx.fxml;
    exports root;
    exports root.models;
    opens root.models to javafx.fxml;
    exports root.controllers;
    opens root.controllers to javafx.fxml;
}