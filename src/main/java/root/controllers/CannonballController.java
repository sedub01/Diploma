package root.controllers;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import root.utils.Constants;
import root.utils.Logger;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;

import java.util.*;

public class CannonballController extends AbstactController {
    @FXML
    private ImageView barrel;
    @FXML
    private ImageView wheel;
    @FXML
    private ImageView floor;
    @FXML
    private BorderPane borderPane;
    @FXML
    private AnchorPane barrelPane;
    @FXML
    private ImageView trackingPoint;

    /** Координата Х зажатой ЛКМ*/
    private double mStartX;
    /** Координата Y зажатой ЛКМ*/
    private double mStartY;
    private final Rotate mRotate = new Rotate();
    private final TextField mSpeedText = new TextField();
    private TrajectoryLine trLine;

    @Override
    protected void construct() {
        Logger.log("Загрузилась модель пушечного ядра");
        barrelPane.getTransforms().add(mRotate);
        trLine = new TrajectoryLine();

        //Определяем точку опоры
        mRotate.setPivotX(barrel.getFitWidth()/10);
        mRotate.setPivotY(barrel.getFitHeight()/2);

        barrelPane.addEventHandler(MouseEvent.MOUSE_PRESSED, this::setMouse);
        // Когда край пушки перетаскивается, вращаем её
        barrelPane.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::barrelDragged);
    }

    private void barrelDragged(final MouseEvent event){
        if (event.getX() >= barrelPane.getWidth() / 3){
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
            setMouse(event);
        }
    }

    private double trajectoryFormula(double x, double angle, double v){
        double a = -Constants.g/(2*v*v*Math.pow(Math.cos(Math.toRadians(angle)), 2));
        double b = Math.tan(Math.toRadians(angle));
        return -b/a;
//        return x*Math.tan(Math.toRadians(angle)) - Constants.g*x*x /
//                (2*v*v*Math.pow(Math.cos(Math.toRadians(angle)), 2));
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
        final var speedLabel = new Label("Начальная скорость снаряда (в м/с)");
        final var angleSpinner = new Spinner<Double>();

        mModelSettings.put(angleLabel, angleSpinner);
        mModelSettings.put(speedLabel, mSpeedText);

        mSpeedText.setText("1");
        trLine.setVelocity(mSpeedText.getText());

        SpinnerValueFactory<Double> valueFactory =
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 90);
        valueFactory.setValue(mRotate.getAngle());
        angleSpinner.setValueFactory(valueFactory);
        mRotate.setOnTransformChanged(e-> {
            valueFactory.setValue(-mRotate.getAngle());
            trLine.calculateTrajectory();
        });
        angleSpinner.valueProperty().addListener(e->
                mRotate.setAngle(-valueFactory.getValue()));
    }

    private class TrajectoryLine{
        private final List<Point> mCurveData = new LinkedList<>();
        private final Path mPath = new Path();
        final int PIXELS_PER_METER = (int)wheel.getFitHeight();
        final double FREQUENCY = 0.05;
        //допустим, длина пушки == 1.5 м, тогда высота колеса == 1 м
        final double HEIGHT = (double)1. / 2; //+dHeight;
        int mInitVelocity;

        public TrajectoryLine(){
            mPath.setStroke(Color.RED);
            mPath.setStrokeWidth(3);
            borderPane.getChildren().add(mPath);
            mPath.getStrokeDashArray().addAll(10d, 10d);
        }

        //https://www.omnicalculator.com/physics/projectile-motion
        //Горизонтальная составляющая скорости: V_x = V * cos a
        //Вертикальная составляющая скорости:   V_y = V * sin a
        //Дистанция по горизонтали: x = V_x * t, где t - время
        //Дистанция по вертикали:   y = h + V_y * t - gt^2 / 2
        //Горизонтальная скорость: V = V_x
        //Вертикальная скорость:   V = V_y - gt
        //Ускорение по горизонтали: a = 0
        //Ускорение по вертикали: a = -g
        //Время полета (выводится из "Дистанции по вертикали"):
        //      t = (V_y + sqrt(V_y^2 + 2gh))/g
        //Дальность полета: R = V_x*t, где t берется из пред. формулы
        //Максимальная высота: h_max = h + (V^2 * sin^2 a) / (2g)
        public void calculateTrajectory() {
            mCurveData.clear();
            mPath.getElements().clear();

            final double angle = -mRotate.getAngle();
            final Bounds bounds = trackingPoint.localToScene(trackingPoint.getBoundsInLocal());

            final double V_y = (double) mInitVelocity * Math.sin(Math.toRadians(angle));
            // 1 м = PIXELS_PER_METER пикселей
            final double V_x = (double) mInitVelocity * Math.cos(Math.toRadians(angle));
            final double timeFlight = (V_y + Math.sqrt(Math.pow(V_y, 2) + 2*Constants.g*HEIGHT))/Constants.g;
            //px/s + sqrt( (px/s)^2 + px ) = px/s + px/s + px^0.5

            final double distance = V_x * timeFlight;
            double h_max = HEIGHT + Math.pow(V_y, 2)/(2*Constants.g); //надо ли добавлять PIXELS_PER_METER?

            Logger.log("timeFlight:", timeFlight);

            final double deltaX = bounds.getCenterX();
            final double deltaY = bounds.getCenterY() - barrel.getFitHeight()/2;
            final double neededHeight = borderPane.getHeight() - floor.getFitHeight();

            double time = 0;
            double x;
            double y = 0;
            double highestY = y;
            while (y < neededHeight){
                time += FREQUENCY;
                x = V_x * time * PIXELS_PER_METER + deltaX;
                y = -(HEIGHT * PIXELS_PER_METER + V_y * time * PIXELS_PER_METER - Constants.g*Math.pow(time, 2)/2) + deltaY;
//                mCurveData.put(x,y);
                if (y > highestY) highestY = y;
                mCurveData.add(new Point(x, y));
            }
            //Нормализация: X_norm = (X - X_min)/(X_max - X_min)
            final double lastX = V_x * time * PIXELS_PER_METER;
            final double xCoeff = distance / lastX;
            final double yCoeff = h_max / highestY;
//            Logger.log(h_max, highestY, yCoeff);
//            for (var point: mCurveData){
//                point.x = point.x * xCoeff * PIXELS_PER_METER + deltaX; //[deltaX; lastX] --- [0; distance]
//                point.x = (point.x - 0)/(distance - 0)*PIXELS_PER_METER + deltaX;
//                point.y = point.y * yCoeff * PIXELS_PER_METER + deltaY;
//            }

            //Последний х == 784,9; distance == 0.32
            //2387,25 = 17,6833 * PIXELS_PER_METER
//        event.getScreenx -- нужен mouseEvent

            int index = 0;
            for (final var point: mCurveData){
                final double dX = point.x;
                final double dY = point.y;
                PathElement el = (index++ == 0)? new MoveTo(dX, dY): new LineTo(dX, dY);
                mPath.getElements().add(el);
            }
        }

        public void setVelocity(String velocity){
            mInitVelocity = Integer.parseInt(velocity);
        }

        private class Point{
            public double x, y;
            public Point(double x1, double y1){
                x = x1;
                y = y1;
            }
        }
    }
}