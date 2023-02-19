package root.gui;

import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import root.utils.Constants;

import java.util.Optional;

public class InfoDialog {
    private final Dialog<ButtonType> dialog;
    private String moduleDesc;
    private String modelDesc;
    private int mHash = -1; //нужен для идентификации модели
    private final Stage stage;
    private final WebView webView;

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

    public InfoDialog setModuleDescription(String desc){
        moduleDesc = desc;
        return this;
    }

    public InfoDialog setModelDescription(String desc){
        modelDesc = desc;
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
        webView.getEngine().loadContent(moduleDesc +
                "<hr>" + modelDesc);
    }

    public Optional<ButtonType> showAndWait(){
        return dialog.showAndWait();
    }

    public boolean hasChanged(int hashCode){
        if (mHash != hashCode){
            mHash = hashCode;
            return true;
        }
        return false;
    }
}
