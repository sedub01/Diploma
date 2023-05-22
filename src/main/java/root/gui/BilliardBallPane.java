package root.gui;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.WritableDoubleValue;
import javafx.geometry.Bounds;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import root.utils.Point;

public class BilliardBallPane{
    /** Полигональная линия, формирующая стрелку */
    private final Polygon mPolygonArrow;
    /** Координаты зажатой ЛКМ */
    private final Point mStartP = new Point();
    /** Сущность для управления поворотом ствола */
    private final Rotate mRotate = new Rotate();
    /** Изначальная скорость шара */
    private final DoubleProperty mInitVelocity;
    /** Изначальная скорость шара */
    private final DoubleProperty mClashVelocity;
    /** Масса шара */
    private final DoubleProperty mBallWeight;
    /** Импульс шара */
    private final DoubleProperty mMomentum;
    /** Траектория пути бильярдного шара */
    private Line mPath = new Line();

    private WritableDoubleValue mAnotherInitVelocity;
    private WritableDoubleValue mAnotherBallWeight;
    private WritableDoubleValue mAnotherAngle;
    /** Начался ли процесс бросания снаряда */
    private boolean mIsTransitionStarted = false;

    private Pane mBallPane;

    public BilliardBallPane(Circle ball, double angle, int velocity, double weight) {
        mPolygonArrow = new PolygonArrow((Color) ball.getFill());
        mPolygonArrow.setRotate(90);
        mPath.setStroke((Color) ball.getFill());
        mPath.setStrokeWidth(2);
        mPath.getStrokeDashArray().addAll(10d, 10d);

        mInitVelocity = new SimpleDoubleProperty(this, "initVelocity", velocity);
        mBallWeight = new SimpleDoubleProperty(this, "ballWeight", weight);
        mClashVelocity = new SimpleDoubleProperty(this, "clashVelocity");
        mMomentum = new SimpleDoubleProperty(this, "momentum", weight*velocity);
        
        translateToBall(ball);
        mBallPane.getChildren().add(mPolygonArrow);
        mBallPane.getChildren().add(mPath);
        mBallPane.getTransforms().add(mRotate);

        final Bounds bounds = mBallPane.sceneToLocal(mPolygonArrow.localToScene(mPolygonArrow.getBoundsInLocal()));
        mPath.setStartX(bounds.getMaxX());
        mPath.setStartY(bounds.getCenterY());
        mPath.setEndX(bounds.getMaxX()+2000); // Максимальное разрешение экрана
        mPath.setEndY(bounds.getCenterY());
        
        mRotate.setAngle(-angle);
        // TODO выделить вращающийся виджет в отдельный класс
        // Необходимые элементы:
        // -? точка, остлеживающая последнее нажатие мыши (объект Point); можно создать
        // внутри конструктора
        // -? сущность, управляющая поворотом объекта (объект Rotate); либо задать точку
        // привязки из X, Y
        // -- точка опоры (либо ImageView, либо Region); можно взять из объекта Rotate
        // -? точка, отслеживающая угол поворота (либо ImageView, либо Region); можно
        // взять точку X, Y взять из точки опоры
        // - вращающийся объект objectPane (объект AnchorPane, Pane, Region); Region -
        // точно нет, т.к. нужен метод getChildren()
        // Таким образом, в объект нужно загрузить две координаты точки опоры (по х и
        // у), коорд. x для отслеживания угла
        // (по умолчанию задать самую крайнюю координату) и сам вращающийся объект (в
        // конструкторе)

        // - методы setMouse и objectDragged, привязанный к objectPane (мб какой-нибудь
        // коллбек или метод класса)
        // Вопрос: надо ли в objectDragged передавать условие вращаемости как второй
        // параметр?
        // - условие вращаемости (мб какой-нибудь коллбек)
        mPolygonArrow.addEventHandler(MouseEvent.MOUSE_PRESSED, this::setMouse);
        mPolygonArrow.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::arrowDragged);
    }

    private void setMouse(final MouseEvent e) {
        mStartP.setCoord(e);
    }

    private void arrowDragged(final MouseEvent event) {
        if (!mIsTransitionStarted) {
            final Transform localToScene = mBallPane.getLocalToSceneTransform();

            final double endX = event.getSceneX();
            final double endY = event.getSceneY();
            final double px = mRotate.getPivotX() + localToScene.getTx();
            final double py = mRotate.getPivotY() + localToScene.getTy();

            final double th1 = clockAngle(mStartP.x - px, mStartP.y - py);
            final double th2 = clockAngle(endX - px, endY - py);
            final double angle = mRotate.getAngle() + th2 - th1;
            mRotate.setAngle(angle);
            setMouse(event);
        }
    }

    private double clockAngle(double dx, double dy) {
        double angle = Math.abs(Math.toDegrees(Math.atan2(dy, dx)));

        if (dy < 0) {
            angle = 360 - angle;
        }
        return angle;
    }

    // Придание нужных свойств объекту круга (или как-то так)
    private void translateToBall(Circle ball) {
        mBallPane = (Pane) ball.getParent();
        
        mRotate.setPivotX(mBallPane.getPrefWidth() / 2);
        mRotate.setPivotY(mBallPane.getPrefHeight() / 2);

        final double r = mBallPane.getPrefWidth() / 2;
        mPolygonArrow.setLayoutX(2 * r); // сделано с небольшим отступом
        mPolygonArrow.setLayoutY(r / 2 - 10);
    }

    public void setAngleSpinnerSettings(Spinner<Double> angleSpinner) {
        SpinnerValueFactory<Double> valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 360,
                -mRotate.getAngle());
        angleSpinner.setValueFactory(valueFactory);
        angleSpinner.setEditable(true);

        mRotate.setOnTransformChanged(e -> {
            valueFactory.setValue(-mRotate.getAngle());
        });
        valueFactory.valueProperty().addListener(e -> {
            mRotate.setAngle(-valueFactory.getValue());
        });
    }

    public void setAnotherBallProperties(DoubleProperty initVelocity,
            DoubleProperty ballWeight,
            DoubleProperty angleProperty) {
        mAnotherInitVelocity = initVelocity;
        mAnotherBallWeight = ballWeight;
        mAnotherAngle = angleProperty;
    }

    public void calculateSpeed() {
        final double m1 = mBallWeight.get();
        final double m2 = mAnotherBallWeight.get();
        double u1 = mInitVelocity.get();
        double u2 = mAnotherInitVelocity.get();
        //Один из исключительных случаев
        boolean isExceptiveForAnother = isExceptiveAngle(Math.abs(mAnotherAngle.get()));
        boolean isExceptiveForThis = isExceptiveAngle(Math.abs(mRotate.angleProperty().get()));
        if (isExceptiveForThis){
            u1 = -u1;
        }
        if (isExceptiveForAnother){
            u2 = -u2;            
        }

        double speedAfter = ((m1 - m2)*u1 + 2*m2*u2) / (m1 + m2);
        mClashVelocity.set(Math.abs(speedAfter));
    }

    //Если возвращается true, скорость должна быть отрицательной, т.е. противоположной оси Х
    private boolean isExceptiveAngle(double angle){
        return angle > 90 && angle < 270; // || ...
    }

    public DoubleProperty initVelocityProperty() {
        return mInitVelocity;
    }

    public DoubleProperty ballWeightProperty() {
        return mBallWeight;
    }

    public DoubleProperty clashVelocityProperty() {
        return mClashVelocity;
    }

    public DoubleProperty angleProperty() {
        return mRotate.angleProperty();
    }

    public DoubleProperty momentumProperty(){
        return mMomentum;
    }

    public Line getPath(){
        return mPath;
    }
}
