package root.controllers;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.fxml.Initializable;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.util.converter.NumberStringConverter;
import root.gui.StatusBarController;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

/** Абстрактный класс контроллера модели*/
abstract public class AbstractModelController implements Initializable {
    /** Структура, содержащая настройки модели*/
    protected Map<Label, Control> mModelSettings = new LinkedHashMap<>();
    /** Структура, хранящая атрибуты кнопок сцены для управления ими изнутри*/
    protected Map<String, BooleanProperty> mPropertiesMap;

    /**Инициализирует LinkedHashMap с ключом названием настройки
    и значением - кастомным виджетом настройки*/
    abstract protected void createSettings();
    /** Инициализация модели */
    abstract protected void construct();

    @Override
    final public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(()->{ //Вызывается уже после установки сцены
            construct();
            createSettings();
            __setToolTips__();
        });
    }

    /** Установка всплывающих подсказок для панели настроек*/
    private void __setToolTips__() {
        for (final var labelSetting: mModelSettings.keySet()){
            labelSetting.setTooltip(new Tooltip(labelSetting.getText()));
            StatusBarController.connectToStatusBar(labelSetting);
        }
    }

    /** Получение настроек*/
    final public Map<Label, Control> getSettings(){
        return mModelSettings;
    }

    /** Функция активации - подразумевается, что каждый контроллер её имеет, но может и не иметь  */
    public void execute() {}

    /** Установка атрибутов сцены*/
    public void setProperties(Map<String, BooleanProperty> propertiesMap) {
        mPropertiesMap = propertiesMap;
    }

    /** Связывание текстового поля настройки с изменяемыми атрибутами модели property*/
    final public void bidirectionalBinding(TextField field, Property<Number> property) {
        //field - то, что зависит; property - то, от чего зависит
        bidirectionalBinding(field, property, true);
    }

    /**
     * {@code disable} по умолчанию true.
     * @see AbstractModelController#bidirectionalBinding(TextField, Property<Number>)
     */
    final public void bidirectionalBinding(TextField field, Property<Number> property, boolean disable) {
        Bindings.bindBidirectional(field.textProperty(), property, new NumberStringConverter());
        field.setDisable(disable);
    }
}
