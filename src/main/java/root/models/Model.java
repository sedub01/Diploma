package root.models;

import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import root.MainController;
import root.controllers.AbstactController;
import root.utils.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//Модели отличаются только параметрами характеристик,
//поэтому наследование избыточно
public class Model {
    private final String mModelName;
    private final String mModelDescription;
    private final String mModelFilePath;
    private Node mScene;
    private ImageView mIcon;
    private final boolean mIsGridNeeded;

    /**Была ли совершена попытка загрузки модели*/
    private boolean mTriedToLoad = false;
    private Map<Label, Control> mSettingsMap;
    private AbstactController mController = null;
    private Map<String, BooleanProperty> mPropertiesMap;

    public Model(HashMap<String, String> model){
        mModelName = model.get("modelName");
        mModelDescription = model.get("modelDescription");
        mModelFilePath = model.get("modelFilePath");
        String iconPath = model.get("iconPath");
        mIsGridNeeded = Boolean.parseBoolean(model.get("isGridNeeded"));
        initIcon(iconPath);
    }

    private void initIcon(String iconPath) {
        try {
            var icon = new Image(getClass().getResourceAsStream(iconPath));
            mIcon = new ImageView(icon);
            mIcon.setFitWidth(20);
            mIcon.setFitHeight(20);
        }
        catch (Exception ignored){}
    }

    public void setScene(Node root){
        mScene = root;
    }

    public Node getScene() {
        if (mScene == null && !mTriedToLoad) {
            mTriedToLoad = true;
            constructScene();
        }
        return mScene;
    }

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
            MainController.displayOnStatusBar("Не загрузилась модель");
            Logger.log("Не загрузилась модель " + mModelName + "\nПричина: " +
                    Logger.formatStringWithLF(e.getCause().toString(), 3) +
                    "\nВместо сцены возвращается null");
        } catch (IllegalStateException e) {
            Logger.log("Программа не смогла найти путь до модели");
        } catch (NullPointerException e) {
            MainController.displayOnStatusBar("Какой-то объект при инициализации равен null");
            Logger.log("Выброшено исключение");
            Logger.log("Модель: " + mModelName);
            Logger.log("Путь: " + mModelFilePath);
        } catch (ClassCastException e) {
            Logger.log("Ошибка кастинга в " + e.toString().split(":")[1].trim().split(" ")[1]);
        } catch (Exception e) {
            MainController.displayOnStatusBar("Необработанное исключение :(");
            Logger.log("Необработанное исключение :(");
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

    public void setProperties(Map<String, BooleanProperty> propertiesMap){
        mPropertiesMap = propertiesMap;
    }

    public void execute(){
        if (mController != null){
            mController.execute();
        }
        else MainController.displayOnStatusBar("Демонстрация невозможна, т.к. mController == null");
    }
}
