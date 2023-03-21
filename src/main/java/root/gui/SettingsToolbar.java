package root.gui;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.util.Duration;
import root.utils.Global;

public class SettingsToolbar {
    private final Button settingsToolButton;
    private final ScrollPane settingsToolBar;
    private final TranslateTransition translate;
    private boolean isVisible = false;
    public SettingsToolbar(Button button, ScrollPane toolbar){
        settingsToolButton = button;
        settingsToolBar = toolbar;
        translate = new TranslateTransition();
        translate.setDuration(Duration.millis(500));
        translate.setNode(settingsToolBar);

        settingsToolBar.setStyle(Global.getCSSThemeColor(0.4, "back")+
                Global.getCSSThemeColor());
        settingsToolButton.setStyle(Global.getCSSThemeColor()+
                "-fx-background-radius: 30px 10px 10px 30px;"+
                "-fx-border-radius: 30px 10px 10px 30px;"+
                "-fx-border-color: black;"
        );

        settingsToolButton.setOnAction(this::onSettingsToolButtonClicked);
        translate.setOnFinished(e->settingsToolButton.setDisable(false));
        settingsToolBar.focusedProperty().
                addListener((obs, newVal, oldVal)-> setVisible(oldVal));
    }

    private void onSettingsToolButtonClicked(ActionEvent actionEvent) {
        final double width = settingsToolBar.getMaxWidth();
        final boolean bf = Math.abs(settingsToolBar.getTranslateX() - width)<0.001;
        setVisible(bf);
        if (bf)
            settingsToolBar.requestFocus(); //Установка фокуса
    }

    public void setVisible(boolean b) {
        if (isVisible != b){
            isVisible = b;
            translate.setByX(settingsToolBar.getMaxWidth() * (b? -1: 1));
            translate.play();

            settingsToolButton.getGraphic().setRotate(b? 180: 0);
            settingsToolButton.setDisable(true);
        }
    }
}
