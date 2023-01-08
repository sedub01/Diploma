package com.root.controllers;

import com.root.utils.Constants;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.ResourceBundle;

public class CannonballController implements Initializable {
    @FXML
    private Rectangle floorRect;
    @FXML
    private BorderPane borderPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Загрузился модуль пушечного ядра");
        floorRect.setWidth(Constants.MIN_WIDTH);
        borderPane.setMinHeight(Constants.MIN_HEIGHT-30);
    }
    public void init() {
        System.out.println("Init!");
    }

}
