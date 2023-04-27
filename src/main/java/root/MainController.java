package root;

import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.util.StringConverter;
import root.gui.*;
import root.models.Model;
import root.models.ModuleFactory;
import root.gui.InfoDialog.DialogType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import root.utils.DescriptionFileParser;
import root.utils.Global;
import root.utils.Logger;

import java.net.URL;
import java.util.*;

/** Главный контроллер, отвечающий за управление приложением*/
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
    private TabPane modelsTabPane;
    @FXML
    private StackPane stackPane;
    @FXML
    private Label statusBar;
    @FXML
    private HBox statusBarBox;
    @FXML
    private Button settingsToolButton;
    @FXML
    private ScrollPane settingsToolBar;
    @FXML
    private HBox header;
    @FXML
    private Button collapseButton;
    @FXML
    private Button expandButton;
    @FXML
    private Button closeButton;
    @FXML
    private Pane markingGrid;

    /** Список фабрик (модулей)*/
    private final List<ModuleFactory> mFactories = new ArrayList<>();
    /** Информационное диалоговое окно*/
    private InfoDialog mInfoDialog;
    /** Панель управления моделью на текущей сцене*/
    private SettingsToolbar mSToolbar;
    /** Разметочная сетка*/
    private MarkingGrid mMarkingGrid;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initGUI();
        initFactories();
        moduleTitlesComboBox.getItems().addAll(mFactories);
    }

    /** Инициализация GUI-компонентов */
    private void initGUI() {
        final var buttonStyle = Global.getCSSThemeColor()+
                "-fx-border-radius: 5px;" +
                "-fx-background-radius: 5px;" +
                "-fx-border-color: black;";

        infoButton.setShape(new Circle());
        gridButton.setStyle(buttonStyle);
        executeButton.setStyle(buttonStyle);
        borderPane.setStyle(Global.getCSSThemeColor(0.75));
        toolBar.setStyle(Global.getCSSThemeColor(0.4));
        menuBar.setStyle(Global.getCSSThemeColor(-0.25));
        statusBarBox.setStyle(Global.getCSSThemeColor(0.2)+
                "-fx-border-color:black;-fx-border-width:1;");
        moduleTitlesComboBox.setStyle(Global.getCSSThemeColor());
        modelsTabPane.setStyle(Global.getCSSThemeColor(0.1, "back"));

        infoButton.setOnAction(e->onInfoButtonClicked(DialogType.modelInfo));
        curModelInfoMenuItem.setOnAction(e->onInfoButtonClicked(DialogType.modelInfo));
        programInfoMenuItem.setOnAction(e->onInfoButtonClicked(DialogType.programInfo));
        gridButton.setOnAction(this::onGridButtonClicked);
        gridMenuItem.setOnAction(this::onGridButtonClicked);
        executeButton.setOnAction(this::onExecuteClicked);

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

        mInfoDialog = new InfoDialog("Информация");
        mSToolbar = new SettingsToolbar(settingsToolButton, settingsToolBar);
        mMarkingGrid = new MarkingGrid(markingGrid);
        Logger.setSBController(statusBar);
        new AppHeader(header, collapseButton, expandButton, closeButton);


        StatusBarController.connectToStatusBar(infoButton);
        StatusBarController.connectToStatusBar(gridButton);
        StatusBarController.connectToStatusBar(executeButton);
    }

    /** Обработка нажатия на gridButton*/
    private void onGridButtonClicked(ActionEvent actionEvent) {
        mMarkingGrid.setVisible(!mMarkingGrid.isVisible());
    }

    /** Обработка нажатия на executeButton*/
    private void onExecuteClicked(ActionEvent actionEvent) {
        final ModuleFactory moduleFactory = moduleTitlesComboBox.getValue();
        final Model model = moduleFactory.getCurrentModel();
        model.execute();
    }

    /** Получение модуля из ComboBox*/
    private void getModule(ActionEvent actionEvent) {
        infoButton.setDisable(false);
        executeButton.setDisable(false);
        curModelInfoMenuItem.setDisable(false);
        stackPane.getChildren().remove(moduleLabel);
        settingsToolButton.setVisible(true);
        modelsTabPane.getTabs().clear();

        final ModuleFactory moduleFactory = moduleTitlesComboBox.getValue();
        for (int i = 0; i < moduleFactory.size(); i++) {
            final var model = moduleFactory.modelAt(i);
            Tab tab = new Tab(model.getModelName());
            tab.setClosable(false);
            tab.setGraphic(model.getIcon());
            tab.setStyle(Global.getCSSThemeColor());
            if (i == moduleFactory.getCurrentModelIndex()) {
                modelChanged(tab, model);
            }
            modelsTabPane.getTabs().add(tab);

            tab.selectedProperty().addListener((choose) -> {
                boolean isSelected = (boolean)((ObservableValue<?>)choose).getValue();
                if (isSelected) { //если таб выбран, загрузить сцену (контент)
                    moduleFactory.setCurrentModelIndex(modelsTabPane.getTabs().indexOf(tab));
                    modelChanged(tab, model);
                }
            });
        }
        modelsTabPane.getSelectionModel().select(moduleFactory.getCurrentModelIndex());
    }

    /** Событие при смене отображения текущей модели*/
    private void modelChanged(Tab tab, Model model) {
        tab.setContent(model.getScene());
        gridButton.setDisable(!model.isGridNeeded());
        gridMenuItem.setDisable(!model.isGridNeeded());
        mSToolbar.setVisible(false);
        mMarkingGrid.setVisible(false);
        mSToolbar.setSettings(model.getSettings());
    }

    /** Инициализация модулей */
    private void initFactories(){
        //инициализация парсера
        DescriptionFileParser fileParser = DescriptionFileParser.getInstance();
        var propertiesMap = getPropertiesMap();
        for (final var moduleHashMap: fileParser.getModulesMap()) {
            var module = new ModuleFactory(moduleHashMap);
            module.setProperties(propertiesMap);
            mFactories.add(module);
        }
    }

    /** Получение параметров главной сцены*/
    private Map<String, BooleanProperty> getPropertiesMap() {
        BooleanProperty execButtonProperty = executeButton.disableProperty();
        BooleanProperty expandButtonProperty = expandButton.disableProperty();

        Map<String, BooleanProperty> propertiesMap = new HashMap<>();
        propertiesMap.put("execButtonProperty", execButtonProperty);
        propertiesMap.put("expandButtonProperty", expandButtonProperty);
        return propertiesMap;
    }

    /** Обработка нажатия на infoButton*/
    private void onInfoButtonClicked(final DialogType type){
        final var module = moduleTitlesComboBox.getValue();
        final var model = module != null? module.getCurrentModel(): null;
        //экономим ресурсы для снижения частоты запросов к движку html
        if (type == DialogType.modelInfo && model != null){
            if (mInfoDialog.hasChanged(model.getModelDescription())){
                //Использование шаблона Builder
                mInfoDialog.
                        setDescription(module.getModuleDescription()+"<hr>"+model.getModelDescription()).
                        setIcon(model.getIcon()).
                        updateContent();
            }
        }
        else if (type == DialogType.programInfo){
            mInfoDialog.setDescription(type).setIcon(null).updateContent();
        }

        mInfoDialog.showAndWait();
    }
}