package root.gui;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import root.utils.Global;
import root.utils.Logger;
import root.utils.Point;

public class BilliardBallPane extends Pane{
    /** Полигональная линия, формирующая стрелку */
    private Polygon mPolygon = new Polygon();
    /** Координаты зажатой ЛКМ*/
    private final Point mStartP = new Point();
    /** Сущность для управления поворотом ствола*/
    private final Rotate mRotate = new Rotate();
    /** Точка, от которой начинается отсчет кривой траектории*/
    private final ImageView mTrackingPoint = new ImageView();
    /** Точка, совпадающая с точкой вращения*/
    private final ImageView mPivotPoint = new ImageView();
    /** Изначальная скорость шара */
    private final DoubleProperty mInitVelocity;
    /** Изначальная скорость шара */
    private final DoubleProperty mClashVelocity;
    /** Масса шара */
    private final DoubleProperty mBallWeight;
    /** Начался ли процесс бросания снаряда*/
    private boolean mIsTransitionStarted = false;

    private Pane mBallPane;
    public BilliardBallPane(Circle ball, double angle, int velocity, double weight){
        mPolygon.setFill((Color) ball.getFill());
        mPolygon.setStrokeWidth(2);
        mPolygon.setStroke(Color.BLACK);
        this.getChildren().add(mPolygon);
        var list = mPolygon.getPoints();
        
        //7 точек - 14 добавлений (сначала x, потом y)
        list.add(80.0); list.add(30.0); //коорд. наконечника стрелки
        list.add(50.0); list.add(60.0);
        list.add(50.0); list.add(40.0);
        list.add(0.0); list.add(40.0);
        list.add(0.0); list.add(20.0);
        list.add(50.0); list.add(20.0);
        list.add(50.0);  list.add(0.0);

        mInitVelocity = new SimpleDoubleProperty(this, "initVelocity", velocity);
        mClashVelocity = new SimpleDoubleProperty(this, "clashVelocity");
        mBallWeight = new SimpleDoubleProperty(this, "ballWeight", weight);
        //Придание нужных свойств объекту круга
        translateToBall(ball);
        mRotate.setAngle(-angle);

        //TODO выделить вращающийся виджет в отдельный класс
        //Необходимые элементы:
        //-? точка, остлеживающая последнее нажатие мыши (объект Point); можно создать внутри конструктора
        //-? сущность, управляющая поворотом объекта (объект Rotate); либо задать точку привязки из X, Y
        //-- точка опоры (либо ImageView, либо Region);  можно взять из объекта Rotate 
        //-? точка, отслеживающая угол поворота (либо ImageView, либо Region); можно взять точку X, Y взять из точки опоры
        //- вращающийся объект objectPane (объект AnchorPane, Pane, Region); Region - точно нет, т.к. нужен метод getChildren()
        //Таким образом, в объект нужно загрузить две координаты точки опоры (по х и у), коорд. x для отслеживания угла 
        //(по умолчанию задать самую крайнюю координату) и сам вращающийся объект (в конструкторе) 

        //- методы setMouse и objectDragged, привязанный к objectPane (мб какой-нибудь коллбек или метод класса)
        //Вопрос: надо ли в objectDragged передавать условие вращаемости как второй параметр?
        //- условие вращаемости (мб какой-нибудь коллбек)
        mPolygon.addEventHandler(MouseEvent.MOUSE_PRESSED, this::setMouse);
        mPolygon.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::arrowDragged);
    }

    private void setMouse(final MouseEvent e){
        mStartP.setCoord(e);
    }

    private void arrowDragged(final MouseEvent event){
        if (!mIsTransitionStarted){
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
    
    private void translateToBall(Circle ball) {
        mBallPane = (Pane) ball.getParent();
        mBallPane.getChildren().add(this);
        mBallPane.getTransforms().add(mRotate);

        mRotate.setPivotX(mBallPane.getPrefWidth()/2);
        mRotate.setPivotY(mBallPane.getPrefHeight()/2);

        final double r = mBallPane.getPrefWidth()/2;
        this.setLayoutX(1.5*r); //сделано с небольшим отступом
        this.setLayoutY(r/2);
    }

    public void setAngleSpinnerSettings(Spinner<Double> angleSpinner) {
        SpinnerValueFactory<Double> valueFactory =
            new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 360, -mRotate.getAngle());
        angleSpinner.setValueFactory(valueFactory);
        angleSpinner.setEditable(true);

        mRotate.setOnTransformChanged(e->{
            valueFactory.setValue(-mRotate.getAngle());
        });
        valueFactory.valueProperty().addListener(e->{
            mRotate.setAngle(-valueFactory.getValue());
        });
    }

    public DoubleProperty initVelocityProperty() {
        return mInitVelocity;
    }

    public DoubleProperty ballWeightProperty() {
        return mBallWeight;
    }
}
