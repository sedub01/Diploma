package com.root.controllers;

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
        System.out.println("Загрузился модуль инерции");
        tabPane.paddingProperty();
        if (tabPane.getTabs().size() == 1)
            tabPane.setVisible(false);
    }
}
