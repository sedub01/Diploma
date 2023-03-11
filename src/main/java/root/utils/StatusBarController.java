package root.utils;

import javafx.application.Platform;
import javafx.scene.control.Label;
import java.util.Timer;
import java.util.TimerTask;

public class StatusBarController {
    private final Timer mTimer;
    private final TimerTask mTask;
    private String mLabelText;
    private final Label mStatusBar;

    public StatusBarController(Label statusBar){
        mStatusBar = statusBar;
        mTimer = new Timer();
        mTask = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(()->{
                    if (mStatusBar.getText().equals(mLabelText))
                        mStatusBar.setText("");
                    mTimer.cancel();
                });
            }
        };
        mTimer.cancel(); //т.к. это - статическая переменная
    }

    public void setText(String text){
        mLabelText = text;
        mStatusBar.setText(mLabelText);
    }

    public void schedule(){
        mTimer.schedule(mTask, Constants.HIDE_DELAY);
    }
}
