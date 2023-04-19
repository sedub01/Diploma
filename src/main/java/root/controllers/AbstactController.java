package root.controllers;

import javafx.beans.property.BooleanProperty;
import javafx.fxml.Initializable;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import root.gui.StatusBarController;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

abstract public class AbstactController implements Initializable {
    protected Map<Label, Control> mModelSettings = new LinkedHashMap<>();
    protected Map<String, BooleanProperty> mPropertiesMap;

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

    /** Функция активации - подразумевается, что каждый контроллер её имеет, но может и не иметь  */
    public void execute() {}

    public void setProperties(Map<String, BooleanProperty> propertiesMap) {
        mPropertiesMap = propertiesMap;
    }
}
