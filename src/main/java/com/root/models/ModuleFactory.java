package com.root.models;

import com.root.models.Types.AllFactories;
import com.root.models.Types.AllModels;
import com.root.utils.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ModuleFactory{
    private String mModuleName;
    private String mModuleDescription;
    private Image icon;
    /*Указатель на текущую модель*/
    protected Model pCurrentModel;
    protected List<Model> models;
    //TODO сделать tabList из моделей
    protected TabPane modelsTabPane;

    public ModuleFactory(AllFactories factory) {
        models = new ArrayList<>();
        modelsTabPane = new TabPane();
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
        pCurrentModel = models.get(0);
        //Если в списке ничего не будет (т.е. пользователь налажал),
        //то высветится IndexOutOfBoundsException

        //???
//        Tab tab = new Tab(pCurrentModel.getModelName());
//        tab.setContent(new Rectangle(200,200, Color.LIGHTSTEELBLUE));
//        modelsTabPane.getTabs().add(tab);
        //TODO вставить эту панель на сцену (с помощью MVC)
    }

    private void addModelsByFactory(AllFactories factory){
        String factoryName = factory.name();
        String prefix = "";
        for (char ch: factoryName.toCharArray())
            if (Character.isUpperCase(ch)) {
                prefix = factoryName.split(String.valueOf(ch))[0];
                break;
            }
        for (AllModels m: AllModels.values())
            if (m.name().startsWith(prefix)) {
                models.add(new Model(m));
            }
        //Какое уродство!
//        Arrays.stre!am(AllModels.values()).filter(m->m.name().
//                startsWith(prefix)).forEach(m->models.add(new Model(m)));
    }

    public String getCurrentModelFileName(){
        return pCurrentModel.getModelFilePath();
    }

    public Parent getCurrentScene() {
        //Для того чтобы URL != null, нужно в папке ресурсов иметь
        //такую же папочную структуру, как и в папке проекта,
        //иначе выдает исключение IllegalStateException
        if (!pCurrentModel.hasScene()){
            FXMLLoader loader = new FXMLLoader(getClass().
                    getResource(getCurrentModelFileName()));
            try {
                pCurrentModel.setScene(loader.load());
            } catch (IOException e) {
                Logger.log("Не загрузилась модель " +
                        getCurrentModelFileName() +
                        "\nПричина: " + Logger.formatStringWithLF(e.getCause().toString(), 3) +
                        "\nВместо сцены возвращается null");
            } catch (IllegalStateException e){
                Logger.log("Вероятнее всего, модуль не загрузился " +
                        "из-за нарушения структуры директорий");
            }
        }
        return pCurrentModel.getScene();
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
}
