package com.root.models;

public class Types {
    /*
        Перечисление фабрик
        Принадлежность модели к фабрике определяет приставка ДО ПЕРВОЙ ЗАГЛАВНОЙ БУКВЫ!
        Приставки должны быть уникальными и указывающими на конкретную фабрику

        g - gravity
        m - momentum
     */
    public enum AllFactories {
        gGravityModuleFactory,
        mMomentumModuleFactory
    }
    //Перечисление моделей
    public enum AllModels {
        gCannonballModel,
        gSpaceManModel,
        mBilliardballModel
    }
}
