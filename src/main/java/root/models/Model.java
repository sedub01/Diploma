package root.models;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import root.MainController;
import root.utils.Logger;

import java.io.IOException;
import java.util.HashMap;

//Модели отличаются только параметрами характеристик,
//поэтому наследование избыточно
public class Model {
    protected String mModelName;
    protected String mModelDescription;
    protected String mModelFilePath;
    protected Node mScene;
    protected ImageView mIcon;
    protected boolean mIsGridNeeded;
    protected boolean mIsGridVisible = false;
    protected boolean mIsReplayNeeded = true;
    //была ли совершена попытка загрузки модели
    protected boolean mTriedToLoad = false;

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

    public String getModelFilePath() {
        return mModelFilePath;
    }

    public boolean hasScene(){
        return mScene != null;
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
        } catch (IOException e) {
            MainController.displayOnStatusBar("Не загрузилась модель");
            Logger.log("Не загрузилась модель " + mModelName + "\nПричина: " +
                    Logger.formatStringWithLF(e.getCause().toString(), 3) +
                    "\nВместо сцены возвращается null");
        } catch (IllegalStateException e) {
            Logger.log("Программа не смогла найти путь до модели");
        } catch (NullPointerException e) {
            MainController.displayOnStatusBar("Не найден путь до модели");
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
}
