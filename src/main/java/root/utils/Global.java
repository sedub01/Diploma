package root.utils;

import javafx.scene.paint.Color;

/**Хранилище глобальных функций*/
public class Global {

    public static String getCSSThemeColor(double colorness, String key){
        Color tColor = Constants.THEME_COLOR;
        if (colorness < 0) {
            tColor = tColor.saturate();
        }

        final int red = (int)(tColor.getRed()*255);
        final int green = (int)(tColor.getGreen()*255);
        final int blue = (int)(tColor.getBlue()*255);
        int dRed = (int)((255 - red)*colorness);
        int dGreen = (int)((255 - green)*colorness);
        int dBlue = (int)((255 - blue)*colorness);
        if (colorness < -1 || colorness > 1){
            Logger.log("Параметр colorness должен быть в пределах [-1; 1]");
            dRed = dGreen = dBlue = 0;
        }

        final String pattern = (key.equals("color"))? Constants.BG_THEME_COLOR_PATTERN: Constants.BG_THEME_PATTERN;
        return String.format(pattern, red + dRed, green + dGreen, blue + dBlue);
    }
    /** Получение строки со стилем цветовой палитры*/
    public static String getCSSThemeColor(double colorness){
        return getCSSThemeColor(colorness, "color");
    }
    public static String getCSSThemeColor(){
        return getCSSThemeColor(0);
    }
}
