package root.models;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import root.models.Types.AllModelsEnum;
import root.utils.Logger;

import java.io.IOException;

//Модели отличаются только параметрами характеристик,
//поэтому наследование избыточно
public class Model {
    protected String mModelName;
    protected String mModelDescription;
    protected String mModelFilePath;
    protected Node mScene;
    protected ImageView mIcon;
    protected boolean mIsGridNeeded = false;
    protected boolean mIsGridVisible = false;
    protected boolean mIsReplayNeeded = true;
    //была ли совершена попытка загрузки модели
    protected boolean mTriedToLoad = false;

    public Model(AllModelsEnum model){
        //Если путь начинается с /, начало берется из корневой папки
        String iconPath = null;
        switch (model) {
            case gCannonballModel -> {
                mModelName = "Пушечное ядро";
                mModelDescription = "<p>Показывает гравитацию пушечного ядра</p>";
                mModelFilePath = "cannonballModel.fxml";
                iconPath = "/root/img/icons/cannonBallModel.png";
            }
            case gSpaceManModel -> {
                mModelName = "Гравитация на планетах";
                mModelDescription = "Показывает всё, на что способен космонавт!";
            }
            case mBilliardballModel -> {
                mIsGridNeeded = true;
                mModelName = "Бильярдный шар";
                mModelDescription = "Показывает всякие штуки с шарами";
                mModelFilePath = "billiardballModel.fxml";
            }
            default -> Logger.log("Нет такого класса");
        }
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
            Logger.log("Не загрузилась модель " + mModelName + "\nПричина: " +
                    Logger.formatStringWithLF(e.getCause().toString(), 3) +
                    "\nВместо сцены возвращается null");
        } catch (IllegalStateException e) {
            Logger.log("Вероятнее всего, модель не загрузилась " +
                    "из-за нарушения структуры директорий");
        } catch (NullPointerException e) {
            Logger.log("Выброшено исключение");
            Logger.log("Модель: " + mModelName);
            Logger.log("Путь: " + mModelFilePath);
        } catch (Exception e) {
            Logger.log("Необработанное исключение :(");
            Logger.log(e.toString());
        }
    }

    public String getModelName() {
        return mModelName;
    }

    public ImageView getIcon() {
        return mIcon;
    }
}
