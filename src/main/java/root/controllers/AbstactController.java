package root.controllers;

import javafx.fxml.Initializable;
import javafx.scene.control.Control;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

abstract public class AbstactController implements Initializable {
    protected Map<Label, Control> mModelSettings = new LinkedHashMap<>();
    /**Инициализирует HashMap с ключом названием настройки
    и значением - кастомным виджетом настройки*/
    abstract protected void createSettings();
    abstract protected void construct();

    @Override
    final public void initialize(URL url, ResourceBundle resourceBundle) {
        construct();
        createSettings();
    }
    final public Map<Label, Control> getSettings(){
        return mModelSettings;
    }
}
