package root.controllers;

import javafx.scene.control.*;
import root.utils.Logger;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;

public class CannonballController extends AbstactController {
    @FXML
    private ImageView barrel;

    private double mStartX;
    private double mStartY;
    private final Rotate mRotate = new Rotate();

    @Override
    protected void construct() {
        Logger.log("Загрузилась модель пушечного ядра");
        barrel.getTransforms().add(mRotate);

        //Определяем точку опоры
        mRotate.setPivotX(barrel.getFitWidth()/10);
        mRotate.setPivotY(barrel.getFitHeight()/2);

        barrel.addEventHandler(MouseEvent.MOUSE_PRESSED, this::setMouse);
        // Когда край пушки перетаскивается, вращаем её
        barrel.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::barrelDragged);
    }

    private void barrelDragged(final MouseEvent event){
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
            final Transform localToScene = barrel.getLocalToSceneTransform();

            //конечное положение точки (на момент конца локального перемещения)
            //координаты берутся относительно берущегося предмета
            final double endX = event.getSceneX();
            final double endY = event.getSceneY();
            //Афинные преобразования
            //Параметры tx, ty обозначают трансформированные точки для ствола
            final double px = mRotate.getPivotX() + localToScene.getTx();
            //Получает элемент преобразования координаты Y матрицы 3x4 + коорд. т. поворота (она постоянна)
            final double py = mRotate.getPivotY() + localToScene.getTy();

            // Определение углов поворота
            final double th1 = clockAngle(mStartX - px, mStartY - py);
            final double th2 = clockAngle(endX - px, endY - py);

            final double angle = mRotate.getAngle() + th2 - th1;
            //для изменения положения только в первой четверти
            if (angle <= 0 && angle >= -90){
                mRotate.setAngle(angle);
            }
            //TODO рассчитать красную пунктирную линию для траектории ядра
            setMouse(event);
        }
    }

    private void setMouse(final MouseEvent e){
        mStartX = e.getSceneX();
        mStartY = e.getSceneY();
    }

    public double clockAngle(double dx, double dy) {
        double angle = Math.abs(Math.toDegrees(Math.atan2(dy, dx)));

        if (dy < 0) {
            angle = 360 - angle;
        }
        return angle;
    }

    @Override
    protected void createSettings() {
        final var angleLabel = new Label("Угол наклона ствола");
        final var speedLabel = new Label("Начальная скорость снаряда");
        final var angleSpinner = new Spinner<Double>();
        final var speedText = new TextField();

        mModelSettings.put(angleLabel, angleSpinner);
        mModelSettings.put(speedLabel, speedText);

        SpinnerValueFactory<Double> valueFactory =
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 90);
        valueFactory.setValue(mRotate.getAngle());
        angleSpinner.setValueFactory(valueFactory);
        mRotate.setOnTransformChanged(e-> valueFactory.setValue(-mRotate.getAngle()));
        angleSpinner.valueProperty().addListener(e->
                mRotate.setAngle(-valueFactory.getValue()));
    }
}