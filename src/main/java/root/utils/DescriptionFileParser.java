package root.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class DescriptionFileParser {
    private static DescriptionFileParser instance;
    private final String[] modelKeyWords = {"modelName",
            "modelDescription",
            "modelFilePath",
            "iconPath",
            "isGridNeeded"};
    private final String[] moduleKeyWords = {"moduleName",
            "moduleDescription"};
    private static List<HashMap<String, String>> modulesMapList;
    private static List<HashMap<String, String>> modelsMapList;
    private static String programDescription;
    private final Properties properties = new Properties();

    private DescriptionFileParser(){
        try {
            final var fis = new FileInputStream("src/main/resources/root/mainschema.properties");
            properties.load(new InputStreamReader(fis, StandardCharsets.UTF_8));
            initModules();
            initModels();
            initProgramDescription();
        } catch (IOException e) {
            Logger.log("ОШИБКА! Файл свойств отсутствует");
        }
    }

    public static DescriptionFileParser getInstance(){
        if (instance == null){
            instance = new DescriptionFileParser();
        }
        return instance;
    }

    private void initModels() {
        modelsMapList = new LinkedList<>();
        final String models = properties.getProperty("models");
        //обрезаю скобки и формирую список
        List<String> modelsList = Arrays.asList(models.substring(1, models.length() - 1).split(","));
        //Обрезаю пробелы
        modelsList.replaceAll(String::trim);
        for (final var modelStr: modelsList){
            final var modelInfo = properties.getProperty("models."+modelStr);
            Set<String> fileKeyWordsSet = new LinkedHashSet<>();
            var modelsMap = createMapByType(fileKeyWordsSet, modelInfo, modelKeyWords);
            modelsMap.put("modelNaming", modelStr);
            modelsMapList.add(modelsMap);
            fileKeyWordsSet.forEach(f -> Logger.log("Неопознанный ключ", f, "в модели", modelStr));
        }
    }

    private void initModules() {
        modulesMapList = new LinkedList<>();
        final String modules = properties.getProperty("modules");
        //список значений названий модулей
        List<String> modulesList = Arrays.asList(modules.substring(1, modules.length() - 1).split(","));
        modulesList.replaceAll(String::trim);
        for (var moduleStr: modulesList){
            final var moduleInfo = properties.getProperty("modules."+moduleStr);
            Set<String> fileKeyWordsSet = new LinkedHashSet<>();
            HashMap<String, String> modulesMap = createMapByType(fileKeyWordsSet, moduleInfo, moduleKeyWords);
            modulesMap.put("moduleNaming", moduleStr);
            modulesMapList.add(modulesMap);
            fileKeyWordsSet.forEach(f -> Logger.log("Неопознанный ключ", f, "в модуле", moduleStr));
        }
    }

    private void initProgramDescription() {
        programDescription = properties.getProperty("programDescription");
    }

    private HashMap<String, String> createMapByType(Set<String> fileKeyWordsSet,
                                                    String objectInfo, String[] keyWords) {
        HashMap<String, String> objectMap = new HashMap<>();
        if (objectInfo != null){
            //список строк с информацией о модуле/модели
            var objectInfoList = Arrays.asList(objectInfo.substring(1, objectInfo.length()-1).
                    split("\n"));
            objectInfoList.replaceAll(String::trim);
            for (var objectInfoItem: objectInfoList){
                for (var key: keyWords){
                    final var fileKey = objectInfoItem.split(":")[0].trim();
                    if (fileKey.equals(key)){
                        objectMap.put(key, objectInfoItem.split(":")[1].trim());
                    }
                    else fileKeyWordsSet.add(fileKey);
                }
            }
        }
        for (var key: keyWords) fileKeyWordsSet.remove(key);

        return objectMap;
    }

    public List<HashMap<String, String>> getModulesMap(){
        return modulesMapList;
    }

    public List<HashMap<String, String>> getModelsMap(){
        return modelsMapList;
    }

    public String getProgramDescription() {
        return programDescription;
    }
}
