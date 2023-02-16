package root;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.util.StringConverter;
import root.models.ModuleFactory;
import root.models.Types.AllFactoriesEnum;
import root.utils.Constants;
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
    //TODO создать сворачиваемый виджет настроек модуля справа от сцены
    //https://www.youtube.com/watch?v=Y2BQhfVVrkk - плохой туториал
    //может это сойдет? https://www.youtube.com/watch?v=8FQ5jXuAhwE
    @FXML
    private TabPane modelsTabPane;
    @FXML
    private StackPane stackPane;

    List<ModuleFactory> factories;

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
    }

    private void getModule(ActionEvent actionEvent) {
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
            }
            modelsTabPane.getTabs().add(tab);

            tab.selectedProperty().addListener((choose) -> {
                boolean isSelected = (boolean)((ObservableValue<?>)choose).getValue();
                if (isSelected) { //если таб выбран, загрузить сцену (контент)
                    tab.setContent(model.getScene());
                    moduleFactory.setCurrentModelIndex(modelsTabPane.getTabs().indexOf(tab));
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
}