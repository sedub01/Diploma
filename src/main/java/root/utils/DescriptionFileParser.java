package root.utils;

import javafx.scene.control.Alert.AlertType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

/** Класс, реализующий парсинг информации о приложении из конфигурационного файла*/
public final class DescriptionFileParser {
    /** Сущность парсера */
    private static DescriptionFileParser mInstance;
    /** Ключевые слова для парсинга моделей*/
    private final String[] mModelKeyWords = {"modelName",
            "modelDescription",
            "modelFilePath",
            "iconPath",
            "isGridNeeded"};
    /** Ключевые слова для парсинга модулей*/
    private final String[] mModuleKeyWords = {"moduleName",
            "moduleDescription"};
    /** Список объектов описания модулей*/
    private final List<Map<String, String>> mModulesMapList = new LinkedList<>();
    /** Список объектов описания моделей*/
    private final List<Map<String, String>> mModelsMapList = new LinkedList<>();
    /** Структура с шаблонами для парсинга*/
    private final Map<String, String> mTemplateMap = new HashMap<>();
    /** Описание программы, взятое из файла*/
    private String mProgramDescription;
    /** Набор свойств из файла с парой ключ-значение*/
    private final Properties mProperties = new Properties();

    private DescriptionFileParser(){
        try {
            File file = new File("src/main/resources/root/mainschema.properties");
            if (!file.exists()){
                file = new File("mainschema.properties");
            }
            final var fis = new FileInputStream(file);
            mProperties.load(new InputStreamReader(fis, StandardCharsets.UTF_8));
            mTemplateMap.put("`", "<font face = \"Comic sans MS\">%s</font>");
            mTemplateMap.put("$", "<img src=\"file:///" + System.getProperty("user.dir") + "/%s\">");
            initModules();
            initModels();
            initProgramDescription();
        } catch (IOException e) {
            String message = "ОШИБКА! Файл свойств отсутствует";
            Logger.log(message);
            Logger.displayOnAlertWindow(message, AlertType.ERROR);
        }
    }

    /** Получение экземпляра парсера*/
    public static DescriptionFileParser getInstance(){
        if (mInstance == null){
            mInstance = new DescriptionFileParser();
        }
        return mInstance;
    }

    /** Инициализация моделей*/
    private void initModels() { 
        final String models = mProperties.getProperty("models");
        //обрезаю скобки и формирую список
        List<String> modelsList = Arrays.asList(models.substring(1, models.length() - 1).split(","));
        //Обрезаю пробелы
        modelsList.replaceAll(String::trim);
        for (final var modelStr: modelsList){
            final String modelInfo = mProperties.getProperty("models." + modelStr);
            Set<String> fileKeyWordsSet = new LinkedHashSet<>();
            var modelsMap = createMapByType(fileKeyWordsSet, modelInfo, mModelKeyWords);
            modelsMap.put("modelNaming", modelStr);
            mModelsMapList.add(modelsMap);
            fileKeyWordsSet.forEach(f -> Logger.log("Неопознанная строка", f, "в модели", modelStr));
        }
    }

    /** Инициализация модулей*/
    private void initModules() {
        final String modules = mProperties.getProperty("modules");
        //список значений названий модулей
        List<String> modulesList = Arrays.asList(modules.substring(1, modules.length() - 1).split(","));
        modulesList.replaceAll(String::trim);
        for (var moduleStr: modulesList){
            final var moduleInfo = mProperties.getProperty("modules."+moduleStr);
            Set<String> fileKeyWordsSet = new LinkedHashSet<>();
            var modulesMap = createMapByType(fileKeyWordsSet, moduleInfo, mModuleKeyWords);
            modulesMap.put("moduleNaming", moduleStr);
            mModulesMapList.add(modulesMap);
            fileKeyWordsSet.forEach(f -> Logger.log("Неопознанный ключ", f, "в модуле", moduleStr));
        }
    }

    /** Инициализация описания программы*/
    private void initProgramDescription() {
        mProgramDescription = mProperties.getProperty("programDescription");
    }

    /** Создание объекта модели/модуля по ключевым словам*/
    private HashMap<String, String> createMapByType(Set<String> fileKeyWordsSet,
                                                    String objectInfo, String[] keyWords) {
        HashMap<String, String> objectMap = new HashMap<>();
        if (objectInfo != null){
            objectInfo = parseWithSeparator(objectInfo, "`");
            objectInfo = parseWithSeparator(objectInfo, "$");
            //список строк с информацией о модуле/модели
            var objectInfoList = Arrays.asList(objectInfo.substring(1, objectInfo.length()-1).
                    split("\n"));
            objectInfoList.replaceAll(String::trim);
            for (var objectInfoItem: objectInfoList){
                for (var key: keyWords){
                    final var fileParts = objectInfoItem.split(":", 2);
                    final var fileKey = fileParts[0].trim();
                    if (fileKey.equals(key)){
                        objectMap.put(fileKey, fileParts[1].trim());
                    }
                    else fileKeyWordsSet.add(fileKey);
                }
            }
        }
        for (var key: keyWords) fileKeyWordsSet.remove(key);

        return objectMap;
    }

    /** Парсинг строки для получения картинки либо формулы*/
    private String parseWithSeparator(String objectInfo, String separator) {
        StringBuilder objectInfoParsed = new StringBuilder(objectInfo);
        if (objectInfo.contains(separator)){
            objectInfoParsed.setLength(0);
            String[] splitted = objectInfo.split(Pattern.quote(separator));
            String template = mTemplateMap.get(separator);
            for (int i = 0; i < splitted.length; i++){
                objectInfoParsed.append(i % 2 == 0? splitted[i]: String.format(template, splitted[i]));
            }
        }
        return objectInfoParsed.toString();
    }

    public List<Map<String, String>> getModulesMap(){
        return mModulesMapList;
    }

    public List<Map<String, String>> getModelsMap(){
        return mModelsMapList;
    }

    public String getProgramDescription() {
        return mProgramDescription;
    }
}
