package root.gui;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import root.utils.Global;

import java.util.Map;

public class SettingsToolbar {
    private final Button settingsToolButton;
    private final ScrollPane settingsToolBar;
    private final TranslateTransition translate;
    private boolean isVisible = false;
    private final GridPane settingsLayout = new GridPane();
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
        settingsToolBar.setContent(settingsLayout);
        settingsLayout.setMaxWidth(settingsToolBar.getMaxWidth() - settingsToolButton.getMaxWidth());
        settingsLayout.setVgap(5);
        settingsLayout.setPadding(new Insets(5, 5, 5, 5));
    }

    private void onSettingsToolButtonClicked(ActionEvent actionEvent) {
        final double width = settingsToolBar.getMaxWidth();
        final boolean bf = Math.abs(settingsToolBar.getTranslateX() - width)<0.001;
        setVisible(bf);
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

    public void setSettings(Map<Label, Control> settingsMap) {
        settingsLayout.getChildren().clear();
        int count = 0;
        if (settingsMap != null){
            for (final var setting: settingsMap.entrySet()){
                settingsLayout.add(setting.getKey(), 0, count);
                settingsLayout.add(setting.getValue(), 1, count);
                count++;
            }
        }
    }
}
