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

public class InfoDialog {
    private final Dialog<ButtonType> mDialog;
    private String mDescription;
    private int mHash = -1; //нужен для идентификации модели
    private final Stage mStage;
    private final WebView mWebView;
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

        mJavaInfoStr = "  <style>\n" +
                "   p {\n" +
                "    font-family: Verdana, Arial, Helvetica, sans-serif; \n" +
                "    font-size: 10pt; /* Размер шрифта в пунктах */ \n" +
                "margin-top: 0.5em; \n"+
                "margin-bottom: 0.5em; \n"+
                "   }\n" +
                "  </style>";
        try {
            mJavaInfoStr += generateJavaInfo();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateJavaInfo() throws IOException {
        final var builder = new ProcessBuilder("cmd.exe", "/c", "java", "--version");
        builder.redirectErrorStream(true);
        final Process p = builder.start();
        final var r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        return String.format("<p>%s</p>", r.readLine());
    }

    public InfoDialog setDescription(final DialogType type){
        if (type == DialogType.programInfo){
            final var fileParser = DescriptionFileParser.getInstance();
            mDescription = String.format(fileParser.getProgramDescription(), mJavaInfoStr);
        }
        return this;
    }

    public InfoDialog setDescription(final String description){
        mDescription = description;
        return this;
    }

    public InfoDialog setIcon(final ImageView icon){
        mStage.getIcons().clear();
        final var mainIcon = Constants.mainIconImage;
        if (icon != null)
            mStage.getIcons().add(icon.getImage());
        else if (mainIcon != null)
            mStage.getIcons().add(mainIcon);
        return this;
    }

    public void updateContent(){
        mWebView.getEngine().loadContent(mDescription);
    }

    public void showAndWait(){
        mDialog.showAndWait();
    }

    public boolean hasChanged(final int hashCode){
        if (mHash != hashCode){
            mHash = hashCode;
            return true;
        }
        return false;
    }
}
