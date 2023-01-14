package com.root.abstractEntities;

import com.root.utils.GlobalFuncs;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

abstract public class ModuleFactory<T extends IModel> {
    private String mModuleName;
    private String mModuleDescription;
    private Image icon;
    /*Указатель на текущую модель*/
    protected IModel pCurrentModel;
    protected List<T> models;

    public ModuleFactory() {
        models = new ArrayList<>();
    }

    public String getCurrentModelFileName(){
        return pCurrentModel.getModelFilePath();
    }

    public Parent getCurrentScene() {
        //Для того чтобы URL != null, нужно в папке ресурсов иметь
        //такую же папочную структуру, как и в папке проекта
        if (!pCurrentModel.hasScene()){
            FXMLLoader loader = new FXMLLoader(getClass().
                    getResource(getCurrentModelFileName()));
            try {
                pCurrentModel.setScene(loader.load());
            } catch (IOException e) {
                System.out.println("Не загрузилась модель " +
                        getCurrentModelFileName() +
                        "\nПричина: " + GlobalFuncs.formatStringWithLF(e.getCause().toString(), 3) +
                        "\nВместо сцены возвращается null");
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
}
