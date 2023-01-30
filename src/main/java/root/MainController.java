package root;

import javafx.scene.control.*;
import javafx.scene.shape.Circle;
import root.models.ModuleFactory;
import root.models.Types.AllFactories;
import root.utils.Constants;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

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
    @FXML
    private ToolBar toolBar;
    @FXML
    private MenuBar menuBar;
    @FXML
    private Button infoButton;
    //TODO создать сворачиваемый виджет настроек модуля справа от сцены
    //https://www.youtube.com/watch?v=Y2BQhfVVrkk - плохой туториал
    //может это сойдет? https://www.youtube.com/watch?v=8FQ5jXuAhwE

    List<ModuleFactory> factories;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        moduleTitlesComboBox.setValue("<Не выбрано>");
        moduleLabel.setMinWidth(Constants.MIN_WIDTH);
        moduleLabel.setMinHeight(Constants.MIN_HEIGHT/3);
        infoButton.setShape(new Circle());
        borderPane.setStyle(String.format(Constants.BACKGROUND_COLOR, 25));
        toolBar.setStyle(String.format(Constants.BACKGROUND_COLOR, 50));
        menuBar.setStyle(String.format(Constants.BACKGROUND_COLOR, 80));
        moduleTitlesComboBox.setStyle(String.format(Constants.BACKGROUND_COLOR, 99));
        moduleTitlesComboBox.setOnAction(this::getModule);

        initFactories();
        factories.forEach(f -> moduleTitlesComboBox.getItems().add(f.getModuleName()));
    }

    private void getModule(ActionEvent actionEvent) {
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
        factories = new ArrayList<>();
        for (AllFactories f: AllFactories.values())
            factories.add(new ModuleFactory(f));
    }
}