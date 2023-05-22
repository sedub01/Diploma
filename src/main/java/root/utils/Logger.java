package root.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import root.gui.StatusBarController;

/**Статический класс для вывода информации пользователю*/
public class Logger {
    /** Строка состояния*/
    private static StatusBarController mSBController;
    /** Диалоговое окно*/
    private static final Alert mAlert;
    static {
        mAlert = new Alert(AlertType.INFORMATION);
        final var mainIcon = Constants.MAIN_ICON_IMAGE;
        final var stage = (Stage) mAlert.getDialogPane().getScene().getWindow();
        if (mainIcon != null)
            stage.getIcons().add(mainIcon);
        mAlert.setHeaderText(null);
    }

    public static String formatStringWithLF(String str, int num){
        String[] array = str.split(" ");
        StringBuilder result = new StringBuilder();
        int index = 0;
        int i = 0;
        while (index < array.length){
            result.append(array[index++]).append(" ");
            i++;
            if (i >= num){
                result.append("\n");
                i = 0;
            }
        }
        return result.toString();
    }

    /** Вывод информации в консоль*/
    public static void log(Object... objects){
        if (Constants.DEBUG){
            for (final var obj: objects)
                System.out.print(obj + " ");
            System.out.println();
        }
    }

    public static void setSBController(Label statusBar) {
        mSBController = new StatusBarController(statusBar);
    }

    /** Отображение текста на строке состояния*/
    public static void displayOnStatusBar(final String text){
        mSBController.execute(text);
    }

    /** Отображение текста в диалоговом окне*/
    public static void displayOnAlertWindow(String content){
        displayOnAlertWindow(content, AlertType.INFORMATION);
    }

    /** Перегрузка displayOnAlertWindow*/
    public static void displayOnAlertWindow(String content, AlertType type){
        mAlert.setAlertType(type);
        String title = type == AlertType.ERROR? "Ошибка": "Внимание";
        displayOnAlertWindow(content, title);
    }

    /** Перегрузка displayOnAlertWindow*/
    public static void displayOnAlertWindow(String content, String title){
        mAlert.setTitle(title);
        mAlert.setContentText(content);
        mAlert.showAndWait();
    }
}
