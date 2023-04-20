package root.gui;

import javafx.application.Platform;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Font;
import javafx.util.Duration;
import root.utils.Constants;
import root.utils.Global;

import java.util.Timer;
import java.util.TimerTask;

/** Класс, определяющий поведение строки состояния*/
public class StatusBarController {
    /** Объект, позволяющий планировать запуск задания*/
    private final Timer mTimer;
    /** Объект строки состояния*/
    private static Label mStatusBar = null;
    /** Текущее задание*/
    private TimerTask mCurrentTask = null;
    /** Запущен ли таймер*/
    private static boolean mIsExecuted = false;

    public StatusBarController(final Label statusBar){
        mStatusBar = statusBar;
        mTimer = new Timer(true);
    }

    /** Приведение таймера в исполнение*/
    public void execute(final String text){
        mStatusBar.setText(text);
        mTimer.schedule(createNewTask(), Constants.HIDE_DELAY);
    }

    /** Связывание контроллера со строкой состояния*/
    public static void connectToStatusBar(final Control control) {
        final var tooltip = control.getTooltip();
        if (tooltip != null){
            tooltip.setFont(new Font( "Calibre", 14));
            tooltip.setStyle("-fx-border-width: 1px; " +
                    "-fx-border-color: white;" +
                    "-fx-background-radius: 0px;" +
                    "-fx-padding: 4 4 4 4;" +
                    "-fx-text-fill: black;" +
                    Global.getCSSThemeColor()
            );
            tooltip.setShowDelay(new Duration(500));
        }
        control.setOnMouseEntered(e->changeStatusBar(tooltip));
        control.setOnMouseExited(e-> {
            if (!mIsExecuted)
                mStatusBar.setText("");
        });
    }

    /** Отображение подсказки контроллера на строке состояния*/
    private static void changeStatusBar(final Tooltip tooltip) {
        if (!mIsExecuted){
            final var toolTipText = (tooltip != null)?
                    tooltip.getText():
                    "Ошибка отображения подсказки";
            mStatusBar.setText(toolTipText);
        }
    }

    /** Создание нового задания*/
    private TimerTask createNewTask(){
        if (mCurrentTask != null)  // Оказывается, теперь нужно сохранять текущее состояние
            mCurrentTask.cancel(); // Строка нужна для отмены предыдущей задачи
        //Альтернативой этому является создание нового экземпляра Timer, либо наследование
        mIsExecuted = true;
        mCurrentTask = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(()->{
                    mStatusBar.setText("");
                    mIsExecuted = false;
                });
            }
        };
        return mCurrentTask;
    }
}
