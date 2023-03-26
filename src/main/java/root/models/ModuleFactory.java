package root.models;

import javafx.scene.Node;
import root.utils.DescriptionFileParser;
import root.utils.Logger;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ModuleFactory{
    private String mModuleName;
    private String mModuleDescription;
    private Image icon;
    /*Указатель на текущую модель*/
    protected int mCurrentModelIndex = 0;
    protected List<Model> models = new ArrayList<>();

    public ModuleFactory(HashMap<String, String> factory) {
        mModuleName = factory.get("moduleName");
        mModuleDescription = factory.get("moduleDescription");
        addModelsByFactory(factory.get("moduleNaming"));
    }

    //Добавление моделей в модуль (фабрику) по совпадающим приставкам
    private void addModelsByFactory(String factory){
        if (mModuleName == null){
            Logger.log("Необработанный модуль", factory);
            return;
        }
        final var fileParser = DescriptionFileParser.getInstance();
        //взятие приставки до первой заглавной буквы названия фабрики
        final String prefix = factory.split("(?=\\p{Lu})")[0];
        for (var model: fileParser.getModelsMap()) {
            final var modelName = model.get("modelNaming");
            if (!prefix.isEmpty() && modelName.startsWith(prefix)){
                models.add(new Model(model));
            }
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
        if (index >= 0 && index < models.size())
            return models.get(index);
        Logger.log("В модуле", mModuleName, "не существует модели с индексом", index);
        return null;
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

    public Model getCurrentModel(){
        return modelAt(mCurrentModelIndex);
    }
}
