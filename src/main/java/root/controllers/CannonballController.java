package root.controllers;

import root.utils.Constants;
import root.utils.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;

import java.net.URL;
import java.util.ResourceBundle;

public class CannonballController implements Initializable {

    @FXML
    private ImageView floor;
    @FXML
    private ImageView barrel;

    private double startX;
    private double startY;

    private final Rotate rotate = new Rotate();
    //TODO
    //В получасовом туториале должно быть так (а потом иниц. в initializable)
    //private Model model;
    //...
    //tabPane.setData(model.method());
    //Также пользуемый контроллер там создается явно (с конструктором, без привязки в .fxml)

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Logger.log("Загрузился модуль пушечного ядра");
        floor.setFitWidth(Constants.MIN_WIDTH); //пока что обойдусь таким костылем
        barrel.getTransforms().add(rotate);

        //Определяем точку опоры
        rotate.setPivotX(barrel.getFitWidth()/10);
        rotate.setPivotY(barrel.getFitHeight()/2);

        barrel.addEventHandler(MouseEvent.MOUSE_PRESSED, this::setMouse);
        // Когда край пушки перетаскивается, вращаем её
        barrel.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::barrelDragged);
    }

    private void barrelDragged(MouseEvent event){
        if (event.getX() >= barrel.getFitWidth() / 3){
            /*
             Используется для получения положения крайнего угла объекта на сцене
             Афинное преобразование - это линейное, которое переводит объект из двумерного(трех-)
             пространство в другое двумерное пространство, сохраняя неизменной прямолинейность
             и параллельность линий.Одна точка А с координатой (x, y, z) в трехмерном пространстве
             переместится в позицию A' с координатой (x', y', z') путем матричного умножения
             https://docs.oracle.com/javase/8/javafx/api/javafx/scene/Node.html#localToSceneTransformProperty
             https://o7planning.org/11157/javafx-transformation
            */
            Transform localToScene = barrel.getLocalToSceneTransform();

            //конечное положение точки (на момент конца локального перемещения)
            //координаты берутся относительно берущегося предмета
            final double endX = event.getSceneX();
            final double endY = event.getSceneY();
            //Афинные преобразования
            //Параметры tx, ty обозначают трансформированные точки для ствола
            final double px = rotate.getPivotX() + localToScene.getTx();
            //Получает элемент преобразования координаты Y матрицы 3x4 + коорд. т. поворота (она постоянна)
            final double py = rotate.getPivotY() + localToScene.getTy();

            // Определение углов поворота
            final double th1 = clockAngle(startX - px, startY - py);
            final double th2 = clockAngle(endX - px, endY - py);

            final double angle = rotate.getAngle() + th2 - th1;
            //для изменения положения только в первой четверти
            if (angle <= 0 && angle >= -90){
                rotate.setAngle(angle);
            }
            //TODO рассчитать красную пунктирную линию для траектории ядра
            setMouse(event);
        }
    }

    private void setMouse(MouseEvent e){
        startX = e.getSceneX();
        startY = e.getSceneY();
    }

    public double clockAngle(double dx, double dy) {
        double angle = Math.abs(Math.toDegrees(Math.atan2(dy, dx)));

        if (dy < 0) {
            angle = 360 - angle;
        }
        return angle;
    }
}