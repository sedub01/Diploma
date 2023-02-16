package root.models;

import javafx.scene.Node;
import root.models.Types.AllFactoriesEnum;
import root.models.Types.AllModelsEnum;
import root.utils.Logger;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

public class ModuleFactory{
    private String mModuleName;
    private String mModuleDescription;
    private Image icon;
    /*Указатель на текущую модель*/
    protected int mCurrentModelIndex = 0;
    protected List<Model> models;

    public ModuleFactory(AllFactoriesEnum factory) {
        models = new ArrayList<>();
        switch (factory){
            case gGravityModuleFactory -> {
                mModuleName = "Гравитация";
                mModuleDescription = "Показывает, на что способна гравитация!";
            }
            case mMomentumModuleFactory -> {
                mModuleName = "Инерция";
                mModuleDescription = "Показывает, на что способна инерция!";
            }
            default -> Logger.log("Нет такой фабрики");
        }
        addModelsByFactory(factory);
        //Если в списке ничего не будет (т.е. пользователь налажал),
        //то высветится IndexOutOfBoundsException
    }

    //Добавление моделей в модуль (фабрику) по совпадающим приставкам
    private void addModelsByFactory(AllFactoriesEnum factory){
        //взятие приставки до первой заглавной буквы названия фабрики
        final String prefix = factory.name().split("(?=\\p{Lu})")[0];
        for (AllModelsEnum m: AllModelsEnum.values())
            if (m.name().startsWith(prefix)) {
                models.add(new Model(m));
            }
    }

    public String getCurrentModelFileName(){
        return models.get(mCurrentModelIndex).getModelFilePath();
    }

    public Node getCurrentScene() {
        return getSceneByIndex(mCurrentModelIndex);
    }

    public Node getSceneByIndex(int index){
        return models.get(index).getScene();
    }

    public String getModuleName() {
        return mModuleName;
    }

    public void setModuleName(String moduleName) {
        this.mModuleName = moduleName;
    }

    public String getModuleDescription() {
        return mModuleDescription;
    }

    public void setModuleDescription(String moduleDescription) {
        this.mModuleDescription = moduleDescription;
    }

    public Model modelAt(int index){
        return models.get(index);
    }

    public int size(){
        return models.size();
    }

    public int getCurrentModelIndex() {
        return mCurrentModelIndex;
    }

    public void setCurrentModelIndex(int currentModelIndex) {
        mCurrentModelIndex = currentModelIndex;
    }
}
