package root.utils;

public class Logger {
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

    public static void log(Object... objects){
        if (Constants.DEBUG)
            for (final var obj: objects)
                System.out.print(obj + " ");
        System.out.println();
    }
}
