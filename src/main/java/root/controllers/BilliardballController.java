package root.controllers;

import root.utils.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TabPane;

import java.net.URL;
import java.util.ResourceBundle;

public class BilliardballController implements Initializable {
    @FXML
    private TabPane tabPane;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Logger.log("Загрузилась модель инерции");
        tabPane.paddingProperty();
        if (tabPane.getTabs().size() == 1)
            tabPane.setVisible(false);
    }
}
