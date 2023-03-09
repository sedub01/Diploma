package root;

import root.utils.Constants;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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
        if (Constants.mainIconImage != null)
            stage.getIcons().add(Constants.mainIconImage);
        stage.setScene(scene);
//        stage.initStyle(StageStyle.UNDECORATED); //TODO на потом

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
        //После увеличения
//        stage.fullScreenProperty()1040.0
//        stage.maximizedProperty()1536.0
        //После уменьшения
//        stage.maximizedProperty()1040.0
//        stage.fullScreenProperty()1040.0
        stage.fullScreenProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue){
                stage.setMaxHeight(HEIGHT);
                stage.setMaxWidth(WIDTH);
                stage.setMaximized(false);
            }
        });
        stage.setFullScreenExitHint("Нажмите Esc для выхода");
        root.setStyle(String.format(Constants.BACKGROUND_COLOR, 0.15));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
