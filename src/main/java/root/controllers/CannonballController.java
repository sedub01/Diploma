package root.controllers;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.stage.Stage;
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
    @FXML
    private ImageView pivotPoint;

    /** Координаты зажатой ЛКМ*/
    private final Point mStartP = new Point();
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
            final double th1 = clockAngle(mStartP.x - px, mStartP.y - py);
            final double th2 = clockAngle(endX - px, endY - py);

            final double angle = mRotate.getAngle() + th2 - th1;
            //для изменения положения только в первой четверти
            if (angle <= 0 && angle >= -90){
                mRotate.setAngle(angle);
            }
            setMouse(event);
        }
    }

    private void setMouse(final MouseEvent e){
        mStartP.setCoord(e);
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
        mSpeedText.focusedProperty().addListener((obs, oldV, newV)->{
            if (oldV){ //если фокус убран, меняем значение
                trLine.setVelocity(mSpeedText.getText());
                trLine.calculateTrajectory();
            }
        });
        mSpeedText.setOnKeyPressed(e->{
            if (e.getCode().equals(KeyCode.ENTER)){
                trLine.setVelocity(mSpeedText.getText());
                trLine.calculateTrajectory();
            }
        });

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
        /** Структура, хранящая координаты точек кривой траектории */
        private final List<Point> mCurveData = new LinkedList<>();
        /** Структура, хранящая передвижения кривой траектории */
        private final ObservableList<PathElement> mPathElements;
        /** Кол-во пикселей на 1 метр */
        final int PIXELS_PER_METER = (int)wheel.getFitHeight();
        /** Частота обновления значений (чем меньше значение, тем более гладкая кривая)*/
        final double FREQUENCY = 0.005;
        /** Высота крепления ствола в метрах*/
        final double HEIGHT = 0.5; // без учета dHeight;
        int mInitVelocity;
        /** Разница между начальной позиции и текущей */
        double mInitHeight;

        public TrajectoryLine(){
            final Path path = new Path();
            path.setStroke(Color.RED);
            path.setStrokeWidth(3);
            borderPane.getChildren().add(path);
            path.getStrokeDashArray().addAll(10d, 10d);
            mPathElements = path.getElements();
            Platform.runLater(()->{
                mInitHeight = pivotPoint.localToScene(pivotPoint.getBoundsInLocal()).getCenterY();

                Stage stage = (Stage)borderPane.getScene().getWindow();
//Т.к. значения GUI обновляются уже после максимизации сцены, а не во время нее, надо использовать метод ниже
                stage.maximizedProperty().addListener((obs)->{
                    Platform.runLater(()-> {
                        //эту переменную надо обновлять каждый раз при изменении размера окна
                        mInitHeight = pivotPoint.localToScene(pivotPoint.getBoundsInLocal()).getCenterY();
                        trLine.calculateTrajectory();
                    });
                });
            });
        }

        //TODO внести это в файл с данными
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
            mPathElements.clear();
            mCurveData.clear();

            final double angle = -mRotate.getAngle();
            //Местонахождение точки начала траектории в пространстве сцены
            final Bounds bounds = trackingPoint.localToScene(trackingPoint.getBoundsInLocal());
            //Разница по оси Y между точкой крепления ствола и концом ствола
            final double dHeight = (mInitHeight - bounds.getCenterY()) / PIXELS_PER_METER;
            // Итоговая высота с учетом наклона ствола
            final double totalHeight = HEIGHT+dHeight;
            //Вертикальная составляющая скорости
            final double V_y = (double) mInitVelocity * Math.sin(Math.toRadians(angle));
            //Горизонтальная составляющая скорости
            final double V_x = (double) mInitVelocity * Math.cos(Math.toRadians(angle));
            //Время полета
            final double timeFlight = (V_y + Math.sqrt(Math.pow(V_y, 2) + 2*Constants.g*totalHeight))/Constants.g;
            //Дальность полета
            final double distance = V_x * timeFlight;
            //Максимальная высота
            double h_max = totalHeight + Math.pow(V_y, 2)/(2*Constants.g);

            //Смещение точки начала полета по оси X
            final double deltaX = bounds.getCenterX();
            //Смещение точки начала полета по оси Y
            final double deltaY = bounds.getCenterY() - barrel.getFitHeight()/2 + dHeight*PIXELS_PER_METER;
            //Координата по оси Y, при которой траектория будет кончатся при соприкосновении с полом
            final double neededHeight = borderPane.getHeight() - floor.getFitHeight();
            //Множитель для значения X, чтобы не считать его в цикле
            final double xCoeff = distance / timeFlight * PIXELS_PER_METER;

            for (double time = 0, x, y = 0; y < neededHeight; time += FREQUENCY){
                x = time * xCoeff + deltaX;
                y = -(totalHeight + V_y * time -
                        Constants.g*Math.pow(time, 2)/2) * PIXELS_PER_METER + deltaY;
                mCurveData.add(new Point(x, y));
                mPathElements.add((time == 0)? new MoveTo(x, y): new LineTo(x, y));
            }
        }

        public void setVelocity(String velocity){
            mInitVelocity = Integer.parseInt(velocity);
        }


    }

    private class Point{
        public double x, y;
        public Point() {}
        public Point(double x1, double y1){
            x = x1;
            y = y1;
        }

        public void setCoord(MouseEvent e) {
            x = e.getSceneX();
            y = e.getSceneY();
        }
    }
}