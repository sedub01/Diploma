package com.root;

import com.root.abstractEntities.ModuleFactory;
import com.root.gravityModels.GravityModuleFactory;
import com.root.momentumModels.MomentumModuleFactory;
import com.root.utils.Constants;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private ComboBox<String> moduleTitlesComboBox;
    @FXML
    private Label moduleLabel;
    @FXML
    private BorderPane borderPane;

    List<ModuleFactory> factories;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        moduleTitlesComboBox.setValue("<Не выбрано>");
        moduleLabel.setMinWidth(Constants.MIN_WIDTH);
        moduleTitlesComboBox.setOnAction(actionEvent -> {
            try {
                getModule(actionEvent);
            } catch (IOException e) {
                System.out.println("Почему-то не загрузился модуль " + e.getClass());
            }
        });

        initFactories();
        factories.forEach(f -> moduleTitlesComboBox.getItems().add(f.getModuleName()));
    }

    private void getModule(ActionEvent actionEvent) throws IOException {
        Parent root = null;
        for (ModuleFactory factory: factories){
            if (moduleTitlesComboBox.getValue().equals(factory.getModuleName())){
                root = factory.getCurrentScene();
                break;
            }
        }
        moduleLabel.setVisible(false);
        borderPane.setCenter(root);
    }

    private void initFactories(){
        factories = new ArrayList();
        factories.add(new GravityModuleFactory());
        factories.add(new MomentumModuleFactory());
    }
}