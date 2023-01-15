package com.root.controllers;

import com.root.utils.Logger;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Logger.log("Загрузился модуль пушечного ядра");
        floor.setFitWidth(1600);
        barrel.getTransforms().add(rotate);

        //Определяем точку опоры
        rotate.setPivotX(barrel.getFitWidth()/10);
        rotate.setPivotY(barrel.getFitHeight()/2);

        barrel.addEventHandler(MouseEvent.MOUSE_PRESSED, this::setMouse);
        // Когда край пушки перетаскивается, вращаем её
        barrel.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::barrelDragged);
        //TODO посмотреть теорию про MVC (для TabPane)
    }

    public double clockAngle(double x, double y, double px, double py) {
        final double dx = x - px;
        final double dy = y - py;
        double angle = Math.abs(Math.toDegrees(Math.atan2(dy, dx)));

        if (dy < 0) {
            angle = 360 - angle;
        }
        return angle;
    }

    private void barrelDragged(MouseEvent event){
        if (event.getX() >= barrel.getFitWidth() / 3){
            // Используется для получения положения крайнего угла объекта на сцене
            //https://docs.oracle.com/javase/8/javafx/api/javafx/scene/Node.html#localToSceneTransformProperty
            Transform localToScene = barrel.getLocalToSceneTransform();

            //конечное положение точки (на момент конца локального перемещения)
            //координаты берутся относительно берущегося предмета
            final double endX = event.getSceneX();
            final double endY = event.getSceneY();
            //Афинные преобразования
            final double px = rotate.getPivotX() + localToScene.getTx();
            final double py = rotate.getPivotY() + localToScene.getTy();

            // Определение углов поворота
            final double th1 = clockAngle(startX, startY, px, py);
            final double th2 = clockAngle(endX, endY, px, py);

            final double angle = rotate.getAngle() + th2 - th1;
            //для изменения положения только в первой четверти
            if (angle <= 0 && angle >= -90){
                rotate.setAngle(angle);
            }
            setMouse(event);
        }
    }

    private void setMouse(MouseEvent e){
        startX = e.getSceneX();
        startY = e.getSceneY();
    }
}