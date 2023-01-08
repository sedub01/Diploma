package com.root.gravityModels;

import com.root.abstractEntities.ModuleFactory;

import java.util.ArrayList;

public class GravityModuleFactory extends ModuleFactory<GravityModel> {
    public GravityModuleFactory(){
        setModuleName("Гравитация");
        setModuleDescription("Показывает, на что способна гравитация!");

        //добавление всех наследников GravityModel
        models.add(new CannonballModel());
        models.add(new SpacemanModel());
        pCurrentModel = models.get(0);
    }
}
