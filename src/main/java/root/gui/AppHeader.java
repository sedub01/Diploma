package root.gui;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import root.utils.Global;

public class AppHeader {
    private Stage mStage;
    private final Button mCloseButton;
    private final Button mExpandButton;
    private final Button mCollapseButton;
    private final Node mAppHeader;

    public AppHeader(final Node appHeader, final Button collapseButton,
                     final Button expandButton, final Button closeButton){
        final var buttonStyle = getClass().getResource(
                "/root/flatbutton.css").toExternalForm();
        final Image usualCloseImage = new Image(getClass().getResourceAsStream("/root/img/icons/close-window.png"));
        final Image enterCloseImage = new Image(getClass().getResourceAsStream("/root/img/icons/close-window-filled.png"));
        mCloseButton = closeButton;
        mExpandButton = expandButton;
        mCollapseButton = collapseButton;
        mAppHeader = appHeader;

        mAppHeader.setStyle(Global.getCSSThemeColor(-0.25));
        mCollapseButton.getStylesheets().add(buttonStyle);
        mExpandButton.getStylesheets().add(buttonStyle);
        mCloseButton.getStylesheets().add(buttonStyle);

        final var image = (ImageView)mCloseButton.getGraphic();
        if (image != null){
            mCloseButton.setOnMouseEntered(e->image.setImage(enterCloseImage));
            mCloseButton.setOnMouseExited(e->image.setImage(usualCloseImage));
        }

        mAppHeader.setOnMousePressed(pressEvent -> {
            mAppHeader.setOnMouseDragged(dragEvent -> {
                if (!mStage.isMaximized()){
                    mStage.setX(dragEvent.getScreenX() - pressEvent.getSceneX());
                    mStage.setY(dragEvent.getScreenY() - pressEvent.getSceneY());
                }
            });
        });

        mCloseButton.setOnAction(e->mStage.close());
        mCollapseButton.setOnAction(e->mStage.setIconified(true));
        mExpandButton.setOnAction(e->mStage.setMaximized(!mStage.isMaximized()));

        //Сцену приходится инициализировать в отдельном потоке с задержкой, т.к. по правилам JavaFX
        //нельзя инициализировать сцену в конструкторе контроллера (нужно хотя бы сразу после него)
        Thread t = new Thread(() -> {
            try {
//Думаю, за такое время пользователь точно не успеет ничего нажать, а сцена успеет прогрузиться
                Thread.sleep(100);
                mStage = (Stage) mAppHeader.getScene().getWindow();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        t.start();
    }
}
