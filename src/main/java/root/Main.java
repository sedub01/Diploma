package root;

/** Точка входа в приложение.
 *  Находится именно здесь, т.к. Java не любит, когда класс входа в приложение от чего-либо наследуется.
 *  Такой трюк необходим для формирования jar-файла*/
public class Main {
    public static void main(String[] args) {
        MainApplication.main(args);
    }
}
