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
    private boolean mIsStart = false;

    public StatusBarController(Label statusBar){
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

    public void execute(String text){
        mLabelText = text;
        mStatusBar.setText(mLabelText);
        if (!mIsStart){
            mTimer.schedule(mTask, Constants.HIDE_DELAY);
            mIsStart = true;
        }
    }
}
