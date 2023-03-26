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
    private final Dialog<ButtonType> dialog;
    private String mDescription;
    private int mHash = -1; //нужен для идентификации модели
    private DialogType mDialogType;
    private final Stage stage;
    private final WebView webView;
    private String javaInfoStr;
    public enum DialogType{
        modelInfo,
        programInfo,
        javafxInfo
    }

    public InfoDialog(String title){
        dialog = new Dialog<>();
        dialog.setTitle(title);
        ButtonType type = new ButtonType("Ок", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(type);

        dialog.getDialogPane().setStyle("-fx-background-color: white;");
        stage = (Stage)dialog.getDialogPane().getScene().getWindow();

        webView = new WebView();
        webView.setPrefSize(400, Constants.MIN_HEIGHT/2);
        dialog.getDialogPane().setContent(webView);

        javaInfoStr = "  <style>\n" +
                "   p {\n" +
                "    font-family: Verdana, Arial, Helvetica, sans-serif; \n" +
                "    font-size: 10pt; /* Размер шрифта в пунктах */ \n" +
                "margin-top: 0.5em; \n"+
                "margin-bottom: 0.5em; \n"+
                "   }\n" +
                "  </style>";
        try {
            javaInfoStr += generateJavaInfo();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateJavaInfo() throws IOException {
        ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "java", "--version");
        builder.redirectErrorStream(true);
        Process p = builder.start();
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        return String.format("<p>%s</p>", r.readLine());
    }

    public InfoDialog setDescription(DialogType type){
        if (type == DialogType.programInfo){
            final var fileParser = DescriptionFileParser.getInstance();
            mDescription = String.format(fileParser.getProgramDescription(), javaInfoStr);
        }
        return this;
    }

    public InfoDialog setDescription(String description){
        mDescription = description;
        return this;
    }

    public InfoDialog setIcon(ImageView icon){
        stage.getIcons().clear();
        final var mainIcon = Constants.mainIconImage;
        if (icon != null)
            stage.getIcons().add(icon.getImage());
        else if (mainIcon != null)
            stage.getIcons().add(mainIcon);
        return this;
    }

    public void updateContent(){
        webView.getEngine().loadContent(mDescription);
    }

    public void showAndWait(){
        dialog.showAndWait();
    }

    public boolean hasChanged(int hashCode, DialogType type){
        if (mHash != hashCode){
            mHash = hashCode;
            return true;
        }
        return hasChanged(type);
    }

    public boolean hasChanged(DialogType type){
        if (mDialogType != type){
            mDialogType = type;
            return true;
        }
        return false;
    }
}
