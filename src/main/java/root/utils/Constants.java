package root.utils;

import javafx.scene.image.Image;
import root.MainApplication;

public class Constants {
    private static final int part = 80;
    public static final int MIN_WIDTH = part*11; //11:9
    public static final int MIN_HEIGHT = part*9;
    public static final String BACKGROUND_COLOR = "-fx-background-color: rgb(213,235,255,%s);";
    public static final boolean DEBUG = true;
    public static final Image mainIconImage;
    static {
        Image tempImage = null;
        try{
            tempImage = new Image(MainApplication.class.getResourceAsStream("img/icons/icon.png"));
        }
        catch (Exception e){Logger.log("Заглавная иконка не найдена");}
        mainIconImage = tempImage;
    }
}
