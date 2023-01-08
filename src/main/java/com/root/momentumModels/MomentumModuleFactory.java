package com.root.momentumModels;

import com.root.abstractEntities.ModuleFactory;

public class MomentumModuleFactory extends ModuleFactory<MomentumModel> {
    public MomentumModuleFactory(){
        setModuleName("Инерция");
        setModuleDescription("Показывает, на что способна инерция!");

        models.add(new BilliardballModel());
        pCurrentModel = models.get(0);
    }
}
