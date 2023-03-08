package root;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.util.StringConverter;
import root.gui.InfoDialog;
import root.models.Model;
import root.models.ModuleFactory;
import root.models.Types.AllFactoriesEnum;
import root.utils.Constants;
import root.gui.InfoDialog.DialogType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private ComboBox<ModuleFactory> moduleTitlesComboBox;
    @FXML
    private Label moduleLabel;
    @FXML
    private BorderPane borderPane;
    @FXML
    private ToolBar toolBar;
    @FXML
    private MenuBar menuBar;
    @FXML
    private Button infoButton;
    @FXML
    private Button gridButton;
    @FXML
    private Button executeButton;
    @FXML
    private MenuItem curModelInfoMenuItem;
    @FXML
    private MenuItem gridMenuItem;
    @FXML
    private MenuItem programInfoMenuItem;
    @FXML
    private MenuItem javafxInfoMenuItem;
    //TODO создать сворачиваемый виджет настроек модуля справа от сцены
    //https://www.youtube.com/watch?v=Y2BQhfVVrkk - плохой туториал
    //может это сойдет? https://www.youtube.com/watch?v=8FQ5jXuAhwE
    @FXML
    private TabPane modelsTabPane;
    @FXML
    private StackPane stackPane;

    private List<ModuleFactory> factories;
    private InfoDialog infoDialog;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initGUI();
        initFactories();
        moduleTitlesComboBox.getItems().addAll(factories);
    }

    private void initGUI() {
        infoButton.setShape(new Circle());
        borderPane.setStyle(String.format(Constants.BACKGROUND_COLOR, 25));
        toolBar.setStyle(String.format(Constants.BACKGROUND_COLOR, 50));
        menuBar.setStyle(String.format(Constants.BACKGROUND_COLOR, 80));
        moduleTitlesComboBox.setStyle(String.format(Constants.BACKGROUND_COLOR, 99));
        infoButton.setOnAction(e->onInfoButtonClicked(DialogType.modelInfo));
        curModelInfoMenuItem.setOnAction(e->onInfoButtonClicked(DialogType.modelInfo));
        programInfoMenuItem.setOnAction(e->onInfoButtonClicked(DialogType.programInfo));
        javafxInfoMenuItem.setOnAction(e->onInfoButtonClicked(DialogType.javafxInfo));

        gridButton.setOnAction(this::onGridButtonClicked);
        gridMenuItem.setOnAction(this::onGridButtonClicked);
        moduleTitlesComboBox.setOnAction(this::getModule);

        moduleTitlesComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(ModuleFactory factory) {
                return factory.getModuleName();
            }

            @Override
            public ModuleFactory fromString(String s) {
                return null;
            }
        });

        infoDialog = new InfoDialog("Информация");
    }

    private void onGridButtonClicked(ActionEvent actionEvent) {
        //включить сетку и расположить ее по центру
        //наверно, надо будет создать объект сетки
    }

    private void getModule(ActionEvent actionEvent) {
        infoButton.setDisable(false);
        executeButton.setDisable(false);
        curModelInfoMenuItem.setDisable(false);
        stackPane.getChildren().remove(moduleLabel);
        modelsTabPane.getTabs().clear();

        final ModuleFactory moduleFactory = moduleTitlesComboBox.getValue();
        for (int i = 0; i < moduleFactory.size(); i++) {
            final var model = moduleFactory.modelAt(i);
            Tab tab = new Tab(model.getModelName());
            tab.setClosable(false);
            tab.setGraphic(model.getIcon());
            if (i == moduleFactory.getCurrentModelIndex()) {
                tab.setContent(model.getScene());
                gridButton.setDisable(!model.isGridNeeded());
                gridMenuItem.setDisable(!model.isGridNeeded());
            }
            modelsTabPane.getTabs().add(tab);

            tab.selectedProperty().addListener((choose) -> {
                boolean isSelected = (boolean)((ObservableValue<?>)choose).getValue();
                if (isSelected) { //если таб выбран, загрузить сцену (контент)
                    moduleFactory.setCurrentModelIndex(modelsTabPane.getTabs().indexOf(tab));
                    //общий шаблон
                    tab.setContent(model.getScene());
                    gridButton.setDisable(!model.isGridNeeded());
                    gridMenuItem.setDisable(!model.isGridNeeded());
                    //TODO РЕАЛИЗОВАТЬ ШАБЛОН Observer (или использовать системный класс)
                    // Я задолбался танцевать с бубном, ища текущую модель!

                    //TODO привязывать действия к "объектам действия (action)", а не к самим объектам
                }
            });
        }
        modelsTabPane.getSelectionModel().select(moduleFactory.getCurrentModelIndex());
    }

    private void initFactories(){
        factories = new ArrayList<>();
        for (AllFactoriesEnum f: AllFactoriesEnum.values())
            factories.add(new ModuleFactory(f));
    }

    private void onInfoButtonClicked(DialogType type){
        final var module = moduleTitlesComboBox.getValue();
        Model model = null;
        if (type == DialogType.modelInfo){
            model = module.getCurrentModel();
        }
        //экономим ресурсы для снижения частоты запросов к движку html
        if (type == DialogType.modelInfo && infoDialog.hasChanged(model.hashCode(), type)){
            //Использование шаблона Builder
            infoDialog.
                setDescription(module.getModuleDescription()+"<hr>"+model.getModelDescription()).
                setIcon(model.getIcon()).
                updateContent();
        }
        else if (type == DialogType.programInfo || type == DialogType.javafxInfo){
            if (infoDialog.hasChanged(type))
                infoDialog.setDescription(type).setIcon(null).updateContent();
        }

        infoDialog.showAndWait();
    }
}