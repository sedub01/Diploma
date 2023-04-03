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

public class StatusBarController {
    private final Timer mTimer;
    private final TimerTask mTask;
    private String mLabelText;
    private static Label mStatusBar = null;
    private boolean mIsStart = false;

    public StatusBarController(final Label statusBar){
        mStatusBar = statusBar;
        mTimer = new Timer(true);
        mTask = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(()->{
                    if (mStatusBar.getText().equals(mLabelText))
                        mStatusBar.setText("");
                    mIsStart = false;
                });
            }
        };
    }

    public void execute(final String text){
        mLabelText = text;
        mStatusBar.setText(mLabelText);
        if (!mIsStart){
            mTimer.schedule(mTask, Constants.HIDE_DELAY);
            mIsStart = true;
        }
    }

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
        control.setOnMouseExited(e->mStatusBar.setText(""));
    }

    private static void changeStatusBar(final Tooltip tooltip) {
        final var toolTipText = (tooltip != null)?
                tooltip.getText():
                "Ошибка отображения подсказки";
        mStatusBar.setText(toolTipText);
    }
}
