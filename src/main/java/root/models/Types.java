package root.models;

public class Types {
    /*
        Перечисление фабрик
        Принадлежность модели к фабрике определяет приставка ДО ПЕРВОЙ ЗАГЛАВНОЙ БУКВЫ!
        Приставки должны быть уникальными и указывающими на конкретную фабрику
        Параметры фабрики задаются в конструкторе ModuleFactory

        g - gravity
        m - momentum
     */
    public enum AllFactories {
        gGravityModuleFactory,
        mMomentumModuleFactory
    }
    //Перечисление моделей
    //Параметры модели задаются в конструкторе Model
    public enum AllModels {
        gCannonballModel,
        gSpaceManModel,
        mBilliardballModel
    }
}
