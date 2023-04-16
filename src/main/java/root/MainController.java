package root;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
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

    private final List<ModuleFactory> mFactories = new ArrayList<>();
    private InfoDialog mInfoDialog;
    private SettingsToolbar mSToolbar;
    private AppHeader mAppHeader;
    private MarkingGrid mMarkingGrid;
    private static StatusBarController mSBController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initGUI();
        initFactories();
        moduleTitlesComboBox.getItems().addAll(mFactories);
    }

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
        mSBController = new StatusBarController(statusBar);
        mSToolbar = new SettingsToolbar(settingsToolButton, settingsToolBar);
        mAppHeader = new AppHeader(header, collapseButton, expandButton, closeButton);
        mMarkingGrid = new MarkingGrid(markingGrid);

        StatusBarController.connectToStatusBar(infoButton);
        StatusBarController.connectToStatusBar(gridButton);
        StatusBarController.connectToStatusBar(executeButton);
    }

    private void onGridButtonClicked(ActionEvent actionEvent) {
        mMarkingGrid.setVisible(!mMarkingGrid.isVisible());
    }

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
                    //общий шаблон
                    modelChanged(tab, model);
                    //TODO РЕАЛИЗОВАТЬ ШАБЛОН Observer (или использовать системный класс)
                    // Я задолбался танцевать с бубном, ища текущую модель!
                    // привязывать действия к "объектам действия (action)", а не к самим объектам
                }
            });
        }
        modelsTabPane.getSelectionModel().select(moduleFactory.getCurrentModelIndex());
    }

    /**событие при смене отображения текущей модели*/
    private void modelChanged(Tab tab, Model model) {
        tab.setContent(model.getScene());
        gridButton.setDisable(!model.isGridNeeded());
        gridMenuItem.setDisable(!model.isGridNeeded());
        mSToolbar.setVisible(false);
        mMarkingGrid.setVisible(false);
        mSToolbar.setSettings(model.getSettings());
    }

    private void initFactories(){
        //инициализация парсера
        DescriptionFileParser fileParser = DescriptionFileParser.getInstance();

        for (final var moduleHashMap: fileParser.getModulesMap())
            mFactories.add(new ModuleFactory(moduleHashMap));
    }

    private void onInfoButtonClicked(final DialogType type){
        final var module = moduleTitlesComboBox.getValue();
        final var model = module != null? module.getCurrentModel(): null;
        //экономим ресурсы для снижения частоты запросов к движку html
        if (type == DialogType.modelInfo && model != null && mInfoDialog.hasChanged(model.hashCode())){
            //Использование шаблона Builder
            mInfoDialog.
                setDescription(module.getModuleDescription()+"<hr>"+model.getModelDescription()).
                setIcon(model.getIcon()).
                updateContent();
        }
        else if (type == DialogType.programInfo){
            mInfoDialog.setDescription(type).setIcon(null).updateContent();
        }

        mInfoDialog.showAndWait();
    }

    public static void displayOnStatusBar(final String text){
        mSBController.execute(text);
    }

    public void setStage(final Stage stage) {
        mAppHeader.setStage(stage);
    }
}