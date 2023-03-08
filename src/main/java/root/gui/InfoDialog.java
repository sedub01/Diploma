package root.gui;

import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import root.utils.Constants;

public class InfoDialog {
    private final Dialog<ButtonType> dialog;
    private String mDescription;
    private int mHash = -1; //нужен для идентификации модели
    private DialogType mDialogType;
    private final Stage stage;
    private final WebView webView;
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
    }

    public InfoDialog setDescription(DialogType type){
        if (type == DialogType.programInfo){
            mDescription = "Это супер программа";
        }
        else if (type == DialogType.javafxInfo){
            mDescription = "Javafx 19.0.1";
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
