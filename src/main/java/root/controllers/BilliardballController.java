package root.controllers;

import root.utils.Logger;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;

public class BilliardballController extends AbstractModelController {
    @FXML
    private TabPane tabPane;

    @Override
    protected void construct() {
        Logger.log("Загрузилась модель инерции");
        tabPane.paddingProperty();
        if (tabPane.getTabs().size() == 1)
            tabPane.setVisible(false);
    }

    @Override
    protected void createSettings() {    }
}
