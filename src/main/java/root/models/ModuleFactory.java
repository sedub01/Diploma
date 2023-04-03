package root.models;

import root.utils.DescriptionFileParser;
import root.utils.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ModuleFactory{
    private final String mModuleName;
    private final String mModuleDescription;
    /**Указатель на текущую модель*/
    private int mCurrentModelIndex = 0;
    private final List<Model> mModels = new ArrayList<>();

    public ModuleFactory(final HashMap<String, String> factory) {
        mModuleName = factory.get("moduleName");
        mModuleDescription = factory.get("moduleDescription");
        addModelsByFactory(factory.get("moduleNaming"));
    }

    /**Добавление моделей в модуль (фабрику) по совпадающим приставкам*/
    private void addModelsByFactory(final String factory){
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
                mModels.add(new Model(model));
            }
        }
    }

    public String getModuleName() {
        return mModuleName;
    }

    public String getModuleDescription() {
        return mModuleDescription;
    }

    public Model modelAt(int index){
        if (index >= 0 && index < mModels.size())
            return mModels.get(index);
        Logger.log("В модуле", mModuleName, "не существует модели с индексом", index);
        return null;
    }

    public int size(){
        return mModels.size();
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
