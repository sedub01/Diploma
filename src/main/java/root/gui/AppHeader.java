package root.gui;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import root.utils.Global;

/** Класс, определяющий поведение хэдера*/
public class AppHeader {
    /** Объект сцены, необходимый для связи с ее атрибутами*/
    private Stage mStage;
    /** Объект хэдера*/
    private final Node mAppHeader;

    public AppHeader(final Node appHeader, final Button collapseButton,
                     final Button expandButton, final Button closeButton){
        final var buttonStyle = getClass().getResource(
                "/root/flatbutton.css").toExternalForm();
        Image usualCloseImage = new Image(getClass().getResourceAsStream("/root/img/icons/close-window.png"));
        Image enterCloseImage = new Image(getClass().getResourceAsStream("/root/img/icons/close-window-filled.png"));
        mAppHeader = appHeader;

        mAppHeader.setStyle(Global.getCSSThemeColor(-0.25));
        collapseButton.getStylesheets().add(buttonStyle);
        expandButton.getStylesheets().add(buttonStyle);
        closeButton.getStylesheets().add(buttonStyle);

        final var image = (ImageView) closeButton.getGraphic();
        if (image != null){
            closeButton.setOnMouseEntered(e->image.setImage(enterCloseImage));
            closeButton.setOnMouseExited(e->image.setImage(usualCloseImage));
        }

        mAppHeader.setOnMousePressed(pressEvent -> {
            mAppHeader.setOnMouseDragged(dragEvent -> {
                if (!mStage.isMaximized()){
                    mStage.setX(dragEvent.getScreenX() - pressEvent.getSceneX());
                    mStage.setY(dragEvent.getScreenY() - pressEvent.getSceneY());
                }
            });
        });

        Platform.runLater(()->{
            mStage = (Stage) mAppHeader.getScene().getWindow();
            closeButton.setOnAction(e->mStage.close());
            collapseButton.setOnAction(e->mStage.setIconified(true));
            expandButton.setOnAction(e->mStage.setMaximized(!mStage.isMaximized()));
        });
    }
}
