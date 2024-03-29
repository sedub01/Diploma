package root;

import javafx.stage.StageStyle;
import root.utils.Constants;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import root.utils.Global;

import java.io.IOException;

/** Точка входа в приложение для фреймворка JavaFX */
public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
        Pane root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("PhysicalModelsEditor");
        if (Constants.MAIN_ICON_IMAGE != null)
            stage.getIcons().add(Constants.MAIN_ICON_IMAGE);
        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setHeight(Constants.MIN_HEIGHT);
        stage.setWidth(Constants.MIN_WIDTH);

        root.setStyle(Global.getCSSThemeColor(0.9));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
