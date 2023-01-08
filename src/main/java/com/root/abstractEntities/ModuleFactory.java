package com.root.abstractEntities;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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

    public String getCurrentModuleFile(){
        return pCurrentModel.getModuleFilePath();
    }

    public Parent getCurrentScene() throws IOException {
        //Для того чтобы URL != null, нужно в папке ресурсов иметь
        //такую же папочную структуру, как и в папке проекта
        if (!pCurrentModel.hasScene()){
            FXMLLoader loader = new FXMLLoader(getClass().
                    getResource(getCurrentModuleFile()));
            pCurrentModel.setScene(loader.load());
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
