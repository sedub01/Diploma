module java.diploma {
    requires transitive javafx.graphics;
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    opens root to javafx.fxml;
    exports root;
    exports root.models;
    opens root.models to javafx.fxml;
    exports root.controllers;
    opens root.controllers to javafx.fxml;
}