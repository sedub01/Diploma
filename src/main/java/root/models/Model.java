package root.models;

import root.models.Types.AllModels;
import root.utils.Logger;
import javafx.scene.Parent;

//Модели отличаются только параметрами характеристик,
//поэтому наследование избыточно
public class Model {
    protected String mModelName;
    protected String mModelDescription;
    protected String mModuleFilePath;
    protected Parent mRoot;
    protected boolean mIsGridNeeded = false;
    protected boolean mIsGridVisible = false;
    protected boolean mIsReplayNeeded = true;

    public Model(AllModels model){
        switch (model) {
            case gCannonballModel -> {
                mModelName = "Пушечное ядро";
                mModelDescription = "Показывает гравитацию пушечного ядра";
                mModuleFilePath = "cannonballModel.fxml";
            }
            case gSpaceManModel -> {
                mModelName = "Гравитация на планетах";
                mModelDescription = "Показывает всё, на что способен космонавт!";
            }
            case mBilliardballModel -> {
                mIsGridNeeded = true;
                mModelName = "Блярдный шар";
                mModelDescription = "Показывает всякие штуки с шарами";
                mModuleFilePath = "billiardballModel.fxml";
            }
            default -> Logger.log("Нет такого класса");
        }
    }

    public String getModelFilePath() {
        return mModuleFilePath;
    }

    public boolean hasScene(){
        return mRoot != null;
    }
    public void setScene(Parent root){
        mRoot = root;
    }
    public Parent getScene(){
        return mRoot;
    }

    public String getModelName() {
        return mModelName;
    }
}
