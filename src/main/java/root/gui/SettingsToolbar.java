package root.gui;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import root.utils.Global;

import java.util.Map;

/** Класс, определяющий поведение панели настроек модели*/
public class SettingsToolbar {
    /** Кнопка сворачивания/разворачивания модели*/
    private final Button mSettingsToolButton;
    /** Панель настроек*/
    private final ScrollPane mSettingsToolBar;
    /** Объект, создающий анимацию перемещения панели*/
    private final TranslateTransition mTranslate;
    /** Видима ли панель настроек*/
    private boolean mIsVisible = false;
    /** Контейнер, упорядочивающий вложенные элементы в виде таблицы*/
    private final GridPane mSettingsLayout = new GridPane();
    public SettingsToolbar(final Button button, final ScrollPane toolbar){
        mSettingsToolButton = button;
        mSettingsToolBar = toolbar;
        mTranslate = new TranslateTransition();
        mTranslate.setDuration(Duration.millis(500));
        mTranslate.setNode(mSettingsToolBar);

        mSettingsToolBar.setStyle(Global.getCSSThemeColor(0.4, "back")+
                Global.getCSSThemeColor());
        mSettingsToolButton.setStyle(Global.getCSSThemeColor()+
                "-fx-background-radius: 30px 10px 10px 30px;"+
                "-fx-border-radius: 30px 10px 10px 30px;"+
                "-fx-border-color: black;"
        );

        mSettingsToolButton.setOnAction(this::onSettingsToolButtonClicked);
        mTranslate.setOnFinished(e-> mSettingsToolButton.setDisable(false));
        mSettingsToolBar.setContent(mSettingsLayout);
        mSettingsLayout.setMaxWidth(mSettingsToolBar.getMaxWidth() - mSettingsToolButton.getMaxWidth());
        mSettingsLayout.setVgap(5);
        mSettingsLayout.setPadding(new Insets(5, 5, 5, 5));
    }

    /** Обработка нажатия на mSettingsToolButton*/
    private void onSettingsToolButtonClicked(final ActionEvent actionEvent) {
        final double width = mSettingsToolBar.getMaxWidth();
        final boolean bf = Math.abs(mSettingsToolBar.getTranslateX() - width)<0.001;
        setVisible(bf);
    }

    /** Управление видимостью панели*/
    public void setVisible(boolean b) {
        if (mIsVisible != b){
            mIsVisible = b;
            mTranslate.setByX(mSettingsToolBar.getMaxWidth() * (b? -1: 1));
            mTranslate.play();

            mSettingsToolButton.getGraphic().setRotate(b? 180: 0);
            mSettingsToolButton.setDisable(true);
        }
    }

    /** Установка настроек модели на панель*/
    public void setSettings(final Map<Label, Control> settingsMap) {
        mSettingsLayout.getChildren().clear();
        int count = 0;
        if (settingsMap != null){
            for (final var setting: settingsMap.entrySet()){
                mSettingsLayout.add(setting.getKey(), 0, count);
                mSettingsLayout.add(setting.getValue(), 1, count);
                count++;
            }
        }
    }
}
