package com.root;

import com.root.utils.Constants;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    private final int WIDTH = Constants.MIN_WIDTH;
    private final int HEIGHT = Constants.MIN_HEIGHT;

    @Override
    public void start(Stage stage) throws IOException {
        Pane root = FXMLLoader.load(getClass().getResource("main-view.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("PhysicalModelsEditor");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("img/icon.png")));
        stage.setScene(scene);

        //Установка неизменяемого (фиксированного) окна, а также
        //установка текущего размера (программно)
        //Если setResizable, тогда нельзя отобр. в полный экран
        stage.setHeight(HEIGHT);
        stage.setWidth(WIDTH);
        stage.setMinHeight(HEIGHT);
        stage.setMinWidth(WIDTH);
        stage.setMaxHeight(HEIGHT);
        stage.setMaxWidth(WIDTH);

        stage.maximizedProperty().addListener((observable, oldValue, newValue) -> {
            //public void changed
            if (newValue){
                stage.setMaxHeight(Integer.MAX_VALUE);
                stage.setMaxWidth(Integer.MAX_VALUE);
                stage.setFullScreen(true);
            }
        });
        stage.fullScreenProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue){
                stage.setMaxHeight(HEIGHT);
                stage.setMaxWidth(WIDTH);
                stage.setMaximized(false);
            }
        });
        stage.setFullScreenExitHint("Нажмите Esc для выхода");
        root.setStyle(String.format(Constants.BACKGROUND_COLOR, 15));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
