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

    public AppHeader(Node appHeader, Button collapseButton,
                     Button expandButton, Button closeButton){
        final var buttonStyle = getClass().getResource(
                "/root/flatbutton.css").toExternalForm();
        Image usualCloseImage = new Image(getClass().getResourceAsStream("/root/img/icons/close-window.png"));
        Image enterCloseImage = new Image(getClass().getResourceAsStream("/root/img/icons/close-window-filled.png"));
        mCloseButton = closeButton;
        mExpandButton = expandButton;
        mCollapseButton = collapseButton;
        mAppHeader = appHeader;

        mAppHeader.setStyle(Global.getCSSThemeColor(-0.25));
        mCollapseButton.getStylesheets().add(buttonStyle);
        mExpandButton.getStylesheets().add(buttonStyle);
        mCloseButton.getStylesheets().add(buttonStyle);

        var image = (ImageView)mCloseButton.getGraphic();
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
    }

    public void setStage(Stage stage){
        mStage = stage;
        setStageSettings();
    }

    private void setStageSettings() {
        mCloseButton.setOnAction(e->mStage.close());
        mCollapseButton.setOnAction(e->mStage.setIconified(true));
        mExpandButton.setOnAction(e->mStage.setMaximized(!mStage.isMaximized()));

        mStage.maximizedProperty().addListener((observable, oldValue, newValue) -> {
            mStage.setMaximized(newValue);
        });
    }
}
