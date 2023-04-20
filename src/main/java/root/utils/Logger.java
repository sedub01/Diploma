package root.utils;

/**Статический класс для вывода текста на консоль*/
public class Logger {
    public static String formatStringWithLF(String str, int num){
        String[] array = str.split(" ");
        StringBuilder result = new StringBuilder();
        int index = 0;
        int i = 0;
        while (index < array.length){
            result.append(array[index++]).append(" ");
            i++;
            if (i >= num){
                result.append("\n");
                i = 0;
            }
        }
        return result.toString();
    }

    /** Вывод информации в консоль*/
    public static void log(Object... objects){
        if (Constants.DEBUG){
            for (final var obj: objects)
                System.out.print(obj + " ");
            System.out.println();
        }
    }
}
