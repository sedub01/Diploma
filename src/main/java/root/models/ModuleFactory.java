package root.models;

import javafx.beans.property.BooleanProperty;
import root.utils.DescriptionFileParser;
import root.utils.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Класс, являющийся модулем и хранящий в себе тематические модели.
 *  По сути, является фабрикой моделей*/
public class ModuleFactory{
    /** Название модуля*/
    private final String mModuleName;
    /** Описания модуля*/
    private final String mModuleDescription;
    /**Указатель на текущую модель*/
    private int mCurrentModelIndex = 0;
    /** Список моделей*/
    private final List<Model> mModels = new ArrayList<>();

    public ModuleFactory(final Map<String, String> factory) {
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
        for (var modelMap: fileParser.getModelsMap()) {
            final var modelName = modelMap.get("modelNaming");
            if (!prefix.isEmpty() && modelName.startsWith(prefix)){
                mModels.add(new Model(modelMap));
            }
        }
    }

    public String getModuleName() {
        return mModuleName;
    }

    public String getModuleDescription() {
        return mModuleDescription;
    }

    /** Получение модели по индексу*/
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

    /** Установка атрибутов сцен для всех моделей*/
    public void setProperties(Map<String, BooleanProperty> propertiesMap) {
        mModels.forEach(m->m.setProperties(propertiesMap));
    }
}
