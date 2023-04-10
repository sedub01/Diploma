package root.utils;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import root.MainApplication;

/**Хранилище глобальных констант*/
public class Constants {
    private static final int part = 80;
    public static final int MIN_WIDTH = part*11; //11:9
    public static final int MIN_HEIGHT = part*9;
    public static final Color THEME_COLOR = Color.rgb(213,235,255);
    public static final String BG_THEME_COLOR_PATTERN = "-fx-background-color: rgb(%d,%d,%d);";
    public static final String BG_THEME_PATTERN = "-fx-background: rgb(%d,%d,%d);";
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
    public static final int HIDE_DELAY = 3000;
    public static final double g = 9.8;
}
