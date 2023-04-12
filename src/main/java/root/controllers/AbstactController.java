package root.controllers;

import javafx.fxml.Initializable;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import root.gui.StatusBarController;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

abstract public class AbstactController implements Initializable {
    protected Map<Label, Control> mModelSettings = new LinkedHashMap<>();
    protected Stage mStage;
    /**Инициализирует HashMap с ключом названием настройки
    и значением - кастомным виджетом настройки*/
    abstract protected void createSettings();
    abstract protected void construct();

    @Override
    final public void initialize(URL url, ResourceBundle resourceBundle) {
        construct();
        createSettings();
        __setToolTips__();
    }

    private void __setToolTips__() {
        for (final var labelSetting: mModelSettings.keySet()){
            labelSetting.setTooltip(new Tooltip(labelSetting.getText()));
            StatusBarController.connectToStatusBar(labelSetting);
        }
    }

    final public Map<Label, Control> getSettings(){
        return mModelSettings;
    }
}
