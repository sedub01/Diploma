package root.models;

import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import root.controllers.AbstractModelController;
import root.utils.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/** Класс, хранящий и обрабатывающий характеристики физической модели
 *  Характеристики берутся из конфигурационного файла
 *  Модели отличаются только параметрами характеристик, поэтому наследование избыточно*/
public class Model {
    /** Название модели*/
    private final String mModelName;
    /** Описание модели*/
    private final String mModelDescription;
    /** Путь до .fxml файле модели*/
    private final String mModelFilePath;
    /** Объект сцены*/
    private Node mScene;
    /** Иконка модели*/
    private ImageView mIcon;
    /** Необходима ли для этой модели сетка*/
    private final boolean mIsGridNeeded;

    /**Была ли совершена попытка загрузки модели*/
    private boolean mTriedToLoad = false;
    /** Структура, содержащая настройки модели (для передачи контроллеру)*/
    private Map<Label, Control> mSettingsMap;
    /** Объект управления моделью*/
    private AbstractModelController mController = null;
    /** Структура, хранящая атрибуты кнопок сцены для управления ими изнутри*/
    private Map<String, BooleanProperty> mPropertiesMap;

    public Model(Map<String, String> model){
        mModelName = model.get("modelName");
        mModelDescription = model.get("modelDescription");
        mModelFilePath = model.get("modelFilePath");
        String iconPath = model.get("iconPath");
        mIsGridNeeded = Boolean.parseBoolean(model.get("isGridNeeded"));
        initIcon(iconPath);
    }

    /** Инициализация иконки*/
    private void initIcon(String iconPath) {
        try {
            var icon = new Image(getClass().getResourceAsStream(iconPath));
            mIcon = new ImageView(icon);
            mIcon.setFitWidth(20);
            mIcon.setFitHeight(20);
        }
        catch (Exception ignored){}
    }

    /** Установка сцены*/
    public void setScene(Node root){
        mScene = root;
    }

    /** Получение сцены*/
    public Node getScene() {
        if (mScene == null && !mTriedToLoad) {
            mTriedToLoad = true;
            constructScene();
        }
        return mScene;
    }

    /** Инициализация сцены*/
    private void constructScene() {
        //Для того чтобы URL != null, нужно в папке ресурсов иметь
        //такую же папочную структуру, как и в папке проекта,
        //иначе выдает исключение IllegalStateException
        try {
            FXMLLoader loader = new FXMLLoader(getClass().
                    getResource(mModelFilePath));
            mScene = loader.load();
            mController = loader.getController();
            mSettingsMap = mController.getSettings();
            mController.setProperties(mPropertiesMap);
        } catch (IOException e) {
            Logger.displayOnStatusBar("Не загрузилась модель");
            Logger.log("Не загрузилась модель " + mModelName + "\nПричина: " +
                    Logger.formatStringWithLF(e.getCause().toString(), 3) +
                    "\nВместо сцены возвращается null");
        } catch (IllegalStateException e) {
            Logger.log("Программа не смогла найти путь до модели");
        } catch (NullPointerException e) {
            String message = "Какой-то объект при инициализации равен null";
            Logger.displayOnStatusBar(message);
            Logger.log(message);
            Logger.log("Модель: " + mModelName);
            Logger.log("Путь: " + mModelFilePath);
            e.printStackTrace();
        } catch (ClassCastException e) {
            Logger.log("Ошибка кастинга в " + e.toString().split(":")[1].trim().split(" ")[1]);
        } catch (Exception e) {
            String message = "Необработанное исключение :(";
            Logger.displayOnStatusBar(message);
            Logger.log(message);
            Logger.log(Logger.formatStringWithLF(e.toString(), 3));
        }
    }

    public String getModelName() {
        return mModelName;
    }

    public ImageView getIcon() {
        return mIcon;
    }

    public String getModelDescription(){
        return mModelDescription;
    }

    public boolean isGridNeeded(){
        return mIsGridNeeded;
    }

    public Map<Label, Control> getSettings(){
        return mSettingsMap;
    }

    /** Установка атрибутов сцены*/
    public void setProperties(Map<String, BooleanProperty> propertiesMap){
        mPropertiesMap = propertiesMap;
    }

    /** Функция активации*/
    public void execute(){
        if (mController != null){
            mController.execute();
        }
        else Logger.displayOnAlertWindow("Демонстрация невозможна, т.к. mController == null");
    }
}
