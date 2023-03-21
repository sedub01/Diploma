package root.utils;

import javafx.scene.paint.Color;

//Хранилище глобальных функций
public class Global {
    public static String getCSSThemeColor(double whiteness, String key){
        final Color tColor = Constants.THEME_COLOR;
        final int red = (int)(tColor.getRed()*255);
        final int green = (int)(tColor.getGreen()*255);
        final int blue = (int)(tColor.getBlue()*255);
        int dRed = (int)((255 - red)*whiteness);
        int dGreen = (int)((255 - green)*whiteness);
        int dBlue = (int)((255 - blue)*whiteness);
        if (whiteness < 0 || whiteness > 1){
            Logger.log("Параметр whiteness должен быть в пределах [0; 1]");
            dRed = dGreen = dBlue = 0;
        }

        final String pattern = (key.equals("color"))? Constants.BG_THEME_COLOR_PATTERN: Constants.BG_THEME_PATTERN;
        return String.format(pattern, red + dRed, green + dGreen, blue + dBlue);
    }
    public static String getCSSThemeColor(double whiteness){
        return getCSSThemeColor(whiteness, "color");
    }
    public static String getCSSThemeColor(){
        return getCSSThemeColor(0);
    }
}
