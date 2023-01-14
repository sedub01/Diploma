package com.root.utils;

public class GlobalFuncs {
    public static String formatStringWithLF(String str, int num){
        var array = str.split(" ");
        String result = "";
        int index = 0;
        int i = 0;
        while (index < array.length){
            result += array[index++] + " ";
            i++;
            if (i >= num){
                result += "\n";
                i = 0;
            }
        }
        return result;
    }
}
