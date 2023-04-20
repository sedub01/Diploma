package root.gui;

import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import root.utils.Constants;
import root.utils.DescriptionFileParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/** Класс диалогового окна с информацией о модели/программе*/
public class InfoDialog {
    /** Объект диалогового окна */
    private final Dialog<ButtonType> mDialog;
    /** Описание внутри диалогового окна*/
    private String mDescription;
    /** Идентификатор модели, чье описание представлено*/
    private int mHash = -1;
    /** Объект сцены, необходимый для связи с ее атрибутами*/
    private final Stage mStage;
    /** Объект, управляющий web-движком и отображающий его содержимое*/
    private final WebView mWebView;
    /** Информация о Java*/
    private String mJavaInfoStr;
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

        mJavaInfoStr = """
                  <style>
                   p {
                    font-family: Verdana, Arial, Helvetica, sans-serif;\s
                    font-size: 10pt; /* Размер шрифта в пунктах */\s
                margin-top: 0.5em;\s
                margin-bottom: 0.5em;\s
                   }
                  </style>""";
        try {
            mJavaInfoStr += generateJavaInfo();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Генерация информации о Java, берущейся из командной строки*/
    private String generateJavaInfo() throws IOException {
        final var builder = new ProcessBuilder("cmd.exe", "/c", "java", "--version");
        builder.redirectErrorStream(true);
        final Process p = builder.start();
        final var r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        return String.format("<p>%s</p>", r.readLine());
    }

    /** Установка описание программы*/
    public InfoDialog setDescription(final DialogType type){
        if (type == DialogType.programInfo){
            final var fileParser = DescriptionFileParser.getInstance();
            mDescription = String.format(fileParser.getProgramDescription(), mJavaInfoStr);
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

    /** Изменилась ли модели с момента последнего клика на infoButton (нужен
     * для уменьшения обращений к web-движку)*/
    public boolean hasChanged(final int hashCode){
        if (mHash != hashCode){
            mHash = hashCode;
            return true;
        }
        return false;
    }
}
