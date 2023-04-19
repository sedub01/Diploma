package root.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

public final class DescriptionFileParser {
    private static DescriptionFileParser mInstance;
    private final String[] mModelKeyWords = {"modelName",
            "modelDescription",
            "modelFilePath",
            "iconPath",
            "isGridNeeded"};
    private final String[] mModuleKeyWords = {"moduleName",
            "moduleDescription"};
    private final List<HashMap<String, String>> mModulesMapList = new LinkedList<>();
    private final List<HashMap<String, String>> mModelsMapList = new LinkedList<>();
    private final Map<String, String> mTemplateMap = new HashMap<>();
    private String mProgramDescription;
    private final Properties mProperties = new Properties();

    private DescriptionFileParser(){
        try {
            final var fis = new FileInputStream("src/main/resources/root/mainschema.properties");
            mProperties.load(new InputStreamReader(fis, StandardCharsets.UTF_8));
            mTemplateMap.put("`", "<font face = \"Comic sans MS\">%s</font>");
            mTemplateMap.put("$", "<img src=\"file:///" + System.getProperty("user.dir") + "/%s\">");
            initModules();
            initModels();
            initProgramDescription();
        } catch (IOException e) {
            Logger.log("ОШИБКА! Файл свойств отсутствует");
        }
    }

    public static DescriptionFileParser getInstance(){
        if (mInstance == null){
            mInstance = new DescriptionFileParser();
        }
        return mInstance;
    }

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

    private void initProgramDescription() {
        mProgramDescription = mProperties.getProperty("programDescription");
    }

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

    public List<HashMap<String, String>> getModulesMap(){
        return mModulesMapList;
    }

    public List<HashMap<String, String>> getModelsMap(){
        return mModelsMapList;
    }

    public String getProgramDescription() {
        return mProgramDescription;
    }
}
