module java.diploma {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.root to javafx.fxml;
    exports com.root;
    exports com.root.abstractEntities;
    opens com.root.abstractEntities to javafx.fxml;
    exports com.root.controllers;
    opens com.root.controllers to javafx.fxml;
}