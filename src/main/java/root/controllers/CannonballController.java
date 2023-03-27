package root.controllers;

import javafx.scene.control.*;
import root.utils.Logger;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import root.utils.StatusBarController;

public class CannonballController extends AbstactController {

    @FXML
    private ImageView barrel;

    private double startX;
    private double startY;
    private final Rotate rotate = new Rotate();

    @Override
    protected void construct() {
        Logger.log("Загрузилась модель пушечного ядра");
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

    @Override
    protected void createSettings() {
        final var angleLabel = new Label("Угол наклона ствола");
        final var speedLabel = new Label("Начальная скорость снаряда");
        angleLabel.setTooltip(new Tooltip(angleLabel.getText()));
        speedLabel.setTooltip(new Tooltip(speedLabel.getText()));
        StatusBarController.connectToStatusBar(angleLabel);
        StatusBarController.connectToStatusBar(speedLabel);
        final var angleSpinner = new Spinner<Double>();
        final var speedText = new TextField();

        mModelSettings.put(angleLabel, angleSpinner);
        mModelSettings.put(speedLabel, speedText);

        SpinnerValueFactory<Double> valueFactory =
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 90);
        valueFactory.setValue(rotate.getAngle());
        angleSpinner.setValueFactory(valueFactory);
        rotate.setOnTransformChanged(e-> valueFactory.setValue(-rotate.getAngle()));
        angleSpinner.valueProperty().addListener(e->
                rotate.setAngle(-valueFactory.getValue()));
    }
}