# Diploma
Проект разрабатывался в рамках выпускной квалификационной работы, полная информация о приложении представлена в отчете ВКР. Проект делался в свободное от основной работы время, поэтому глубоко исследовать разные технологии было некогда. Часто приходилось выбирать не самое оптимальное решение, а первое, которое показалось приемлемым, и с хорошо описанной документацией.

Для запуска приложения достаточно скачать Java, т.к. все JavaFX-классы представлены самим Oracle; туториал внизу, скорее, представлен для разработки.

Тэг afterInvest в коде означает, что выделенный кусок кода будет разрабатываться после привлечения "потенциального инвестора".

Как запустить приложение на Windows:
- Посмотреть туториал: https://youtu.be/_7OM-cMYWbQ
- Скачать файлы из релиза последней версии
- Зайти в директорию с jar файлом, затем в консоль и ввести команду 
>`java -jar PME-1.1.jar`

Как запустить приложение на Linux (Kubuntu):
- Установить java `sudo apt install openjdk-17-jre-headless`
- Установить глобальную переменную `export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64/lib/` (у меня установилось сюда)
- Проверить переменную `echo $JAVA_HOME`
- Скачать SDK по ссылке: https://gluonhq.com/products/javafx/
- Установить глобальную переменную уже для javaFX: `export PATH_TO_FX=/home/username/snap/javafx-sdk-17.0.7/lib/`
- Проверить переменную `echo $PATH_TO_FX`
- Скачать файлы из релиза последней версии
- Зайти в директорию с jar файлом, затем в консоль и ввести команду
>`java -jar --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.fxml,javafx.web PME-1.1.jar`
  
Вы великолепны!
