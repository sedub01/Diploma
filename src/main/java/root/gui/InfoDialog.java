package root.gui;

import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import root.utils.Constants;
import root.utils.DescriptionFileParser;

/** Класс диалогового окна с информацией о модели/программе*/
public class InfoDialog {
    /** Объект диалогового окна */
    private final Dialog<ButtonType> mDialog;
    /** Описание внутри диалогового окна*/
    private String mDescription;
    /** Объект сцены, необходимый для связи с ее атрибутами*/
    private final Stage mStage;
    /** Объект, управляющий web-движком и отображающий его содержимое*/
    private final WebView mWebView;

    public enum DialogType{
        modelInfo,
        programInfo
    }

    public InfoDialog(final String title){
        mDialog = new Dialog<>();
        mDialog.setTitle(title);
        final var type = new ButtonType("Ок", ButtonBar.ButtonData.OK_DONE);
        mDialog.getDialogPane().getButtonTypes().add(type);

        mDialog.getDialogPane().setStyle("-fx-background-color: white;");
        mStage = (Stage) mDialog.getDialogPane().getScene().getWindow();

        mWebView = new WebView();
        mWebView.setPrefSize(400, Constants.MIN_HEIGHT/2);
        mDialog.getDialogPane().setContent(mWebView);
    }

    /** Установка описание программы*/
    public InfoDialog setDescription(final DialogType type){
        if (type == DialogType.programInfo){
            final var fileParser = DescriptionFileParser.getInstance();
            mDescription = fileParser.getProgramDescription();
        }
        return this;
    }

    /** Установка описание модели*/
    public InfoDialog setDescription(final String description){
        mDescription = description;
        return this;
    }

    /** Установка иконки модели на диалоговое окно*/
    public InfoDialog setIcon(final ImageView icon){
        mStage.getIcons().clear();
        final var mainIcon = Constants.mainIconImage;
        if (icon != null)
            mStage.getIcons().add(icon.getImage());
        else if (mainIcon != null)
            mStage.getIcons().add(mainIcon);
        return this;
    }

    /** Устанавливает информацию в виде HTML*/
    public void updateContent(){
        mWebView.getEngine().loadContent(mDescription);
    }

    /** Отображение диалогового окна*/
    public void showAndWait(){
        mDialog.showAndWait();
    }

    /** Изменилось ли описание с момента последнего клика на infoButton (нужен
     * для уменьшения обращений к web-движку)*/
    public boolean hasChanged(final String modelDesc){
        int start = modelDesc.length() - 15;
        String descPostfix = modelDesc.substring(start);
        return mDescription == null || !mDescription.endsWith(descPostfix);
    }
}
